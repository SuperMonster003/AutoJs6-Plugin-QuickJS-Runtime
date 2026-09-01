#include <jni.h>

#include <algorithm>
#include <atomic>
#include <chrono>
#include <cctype>
#include <cstdint>
#include <iterator>
#include <limits>
#include <new>
#include <string>
#include <vector>

extern "C" {
#include "vendor/quickjs-2026-06-04/quickjs.h"
}

namespace {

using Clock = std::chrono::steady_clock;

constexpr int kSourceModeScript = 0;
constexpr int kSourceModeModule = 1;
constexpr int kStreamStdout = 1;
constexpr int kStreamStderr = 2;
constexpr size_t kMaxCallbackChunkBytes = 64U * 1024U;

constexpr const char *kStatusSuccess = "SUCCESS";
constexpr const char *kStatusEvaluation = "EVALUATION_ERROR";
constexpr const char *kStatusOutOfMemory = "OUT_OF_MEMORY";
constexpr const char *kStatusCancelled = "CANCELLED";
constexpr const char *kStatusTimeout = "TIMEOUT";
constexpr const char *kStatusOutputLimit = "OUTPUT_LIMIT";
constexpr const char *kStatusInternal = "INTERNAL";

struct ExecutionState {
    ExecutionState(
        int64_t timeout_millis,
        uint64_t memory_limit_bytes,
        uint64_t stack_limit_bytes,
        uint64_t output_limit_bytes)
        : deadline(Clock::now() + std::chrono::milliseconds(timeout_millis)),
          memory_limit(memory_limit_bytes),
          stack_limit(stack_limit_bytes),
          output_limit(output_limit_bytes) {}

    const Clock::time_point deadline;
    const uint64_t memory_limit;
    const uint64_t stack_limit;
    const uint64_t output_limit;
    std::atomic<uint64_t> output_bytes{0};
    std::atomic<bool> executing{false};
    std::atomic<bool> cancelled{false};
    std::atomic<bool> timed_out{false};
    std::atomic<bool> output_limited{false};
    std::atomic<bool> callback_failed{false};
    JNIEnv *env = nullptr;
    jobject output_sink = nullptr;
    jmethodID output_method = nullptr;
};

ExecutionState *fromHandle(jlong handle) {
    return reinterpret_cast<ExecutionState *>(static_cast<intptr_t>(handle));
}

jlong toHandle(ExecutionState *state) {
    return static_cast<jlong>(reinterpret_cast<intptr_t>(state));
}

int interruptHandler(JSRuntime *, void *opaque) {
    auto *state = static_cast<ExecutionState *>(opaque);
    if (state->cancelled.load(std::memory_order_acquire)) {
        return 1;
    }
    if (Clock::now() >= state->deadline) {
        state->timed_out.store(true, std::memory_order_release);
        state->cancelled.store(true, std::memory_order_release);
        return 1;
    }
    return 0;
}

std::string utf16ToUtf8(JNIEnv *env, jstring value) {
    if (value == nullptr) {
        return {};
    }
    const jsize length = env->GetStringLength(value);
    const jchar *characters = env->GetStringChars(value, nullptr);
    if (characters == nullptr) {
        return {};
    }
    std::string result;
    result.reserve(static_cast<size_t>(length) * 3U);
    for (jsize index = 0; index < length; ++index) {
        uint32_t code_point = characters[index];
        if (code_point >= 0xD800U && code_point <= 0xDBFFU && index + 1 < length) {
            const uint32_t low = characters[index + 1];
            if (low >= 0xDC00U && low <= 0xDFFFU) {
                code_point = 0x10000U + ((code_point - 0xD800U) << 10U) + (low - 0xDC00U);
                ++index;
            }
        }
        if (code_point <= 0x7FU) {
            result.push_back(static_cast<char>(code_point));
        } else if (code_point <= 0x7FFU) {
            result.push_back(static_cast<char>(0xC0U | (code_point >> 6U)));
            result.push_back(static_cast<char>(0x80U | (code_point & 0x3FU)));
        } else if (code_point <= 0xFFFFU) {
            result.push_back(static_cast<char>(0xE0U | (code_point >> 12U)));
            result.push_back(static_cast<char>(0x80U | ((code_point >> 6U) & 0x3FU)));
            result.push_back(static_cast<char>(0x80U | (code_point & 0x3FU)));
        } else {
            result.push_back(static_cast<char>(0xF0U | (code_point >> 18U)));
            result.push_back(static_cast<char>(0x80U | ((code_point >> 12U) & 0x3FU)));
            result.push_back(static_cast<char>(0x80U | ((code_point >> 6U) & 0x3FU)));
            result.push_back(static_cast<char>(0x80U | (code_point & 0x3FU)));
        }
    }
    env->ReleaseStringChars(value, characters);
    return result;
}

jstring newUtf8String(JNIEnv *env, const std::string &value) {
    jclass string_class = env->FindClass("java/lang/String");
    if (string_class == nullptr) {
        return nullptr;
    }
    jmethodID constructor = env->GetMethodID(string_class, "<init>", "([BLjava/lang/String;)V");
    if (constructor == nullptr) {
        env->DeleteLocalRef(string_class);
        return nullptr;
    }
    auto byte_count = static_cast<jsize>(std::min<size_t>(
        value.size(),
        static_cast<size_t>(std::numeric_limits<jsize>::max())));
    jbyteArray bytes = env->NewByteArray(byte_count);
    if (bytes == nullptr) {
        env->DeleteLocalRef(string_class);
        return nullptr;
    }
    if (byte_count > 0) {
        env->SetByteArrayRegion(
            bytes,
            0,
            byte_count,
            reinterpret_cast<const jbyte *>(value.data()));
    }
    jstring encoding = env->NewStringUTF("UTF-8");
    auto result = static_cast<jstring>(env->NewObject(string_class, constructor, bytes, encoding));
    env->DeleteLocalRef(encoding);
    env->DeleteLocalRef(bytes);
    env->DeleteLocalRef(string_class);
    return result;
}

jobjectArray makeResult(JNIEnv *env, const char *status, const std::string &message) {
    jclass string_class = env->FindClass("java/lang/String");
    if (string_class == nullptr) {
        return nullptr;
    }
    jobjectArray result = env->NewObjectArray(2, string_class, nullptr);
    if (result == nullptr) {
        env->DeleteLocalRef(string_class);
        return nullptr;
    }
    jstring status_value = env->NewStringUTF(status);
    jstring message_value = newUtf8String(env, message);
    env->SetObjectArrayElement(result, 0, status_value);
    env->SetObjectArrayElement(result, 1, message_value);
    env->DeleteLocalRef(status_value);
    env->DeleteLocalRef(message_value);
    env->DeleteLocalRef(string_class);
    return result;
}

bool reserveOutput(ExecutionState *state, size_t size) {
    const uint64_t requested = static_cast<uint64_t>(size);
    uint64_t current = state->output_bytes.load(std::memory_order_acquire);
    while (true) {
        if (requested > state->output_limit - std::min(current, state->output_limit)) {
            state->output_limited.store(true, std::memory_order_release);
            state->cancelled.store(true, std::memory_order_release);
            return false;
        }
        if (state->output_bytes.compare_exchange_weak(
                current,
                current + requested,
                std::memory_order_acq_rel,
                std::memory_order_acquire)) {
            return true;
        }
    }
}

bool emitOutput(ExecutionState *state, int stream, const std::string &text) {
    if (state->env == nullptr || state->output_sink == nullptr || state->output_method == nullptr) {
        state->callback_failed.store(true, std::memory_order_release);
        state->cancelled.store(true, std::memory_order_release);
        return false;
    }
    if (!reserveOutput(state, text.size())) {
        return false;
    }
    size_t offset = 0;
    while (offset < text.size()) {
        size_t end = std::min(offset + kMaxCallbackChunkBytes, text.size());
        if (end < text.size()) {
            while (end > offset &&
                   (static_cast<unsigned char>(text[end]) & 0xC0U) == 0x80U) {
                --end;
            }
        }
        if (end == offset) {
            end = std::min(offset + kMaxCallbackChunkBytes, text.size());
        }
        const auto count = static_cast<jsize>(end - offset);
        jbyteArray bytes = state->env->NewByteArray(count);
        if (bytes == nullptr) {
            state->callback_failed.store(true, std::memory_order_release);
            state->cancelled.store(true, std::memory_order_release);
            return false;
        }
        state->env->SetByteArrayRegion(
            bytes,
            0,
            count,
            reinterpret_cast<const jbyte *>(text.data() + offset));
        state->env->CallVoidMethod(state->output_sink, state->output_method, stream, bytes);
        state->env->DeleteLocalRef(bytes);
        if (state->env->ExceptionCheck()) {
            state->env->ExceptionClear();
            state->callback_failed.store(true, std::memory_order_release);
            state->cancelled.store(true, std::memory_order_release);
            return false;
        }
        offset = end;
    }
    return true;
}

JSValue consoleFunction(
    JSContext *context,
    JSValueConst,
    int argument_count,
    JSValueConst *arguments,
    int magic) {
    auto *state = static_cast<ExecutionState *>(JS_GetContextOpaque(context));
    std::string line;
    for (int index = 0; index < argument_count; ++index) {
        size_t size = 0;
        const char *value = JS_ToCStringLen(context, &size, arguments[index]);
        if (value == nullptr) {
            return JS_EXCEPTION;
        }
        if (index > 0) {
            line.push_back(' ');
        }
        line.append(value, size);
        JS_FreeCString(context, value);
    }
    line.push_back('\n');
    if (!emitOutput(state, magic, line)) {
        return JS_ThrowInternalError(context, "QuickJS output delivery was interrupted");
    }
    return JS_UNDEFINED;
}

bool installHostGlobals(JSContext *context, const std::string &source_name) {
    JSValue global = JS_GetGlobalObject(context);
    if (JS_IsException(global)) {
        return false;
    }
    JSValue console = JS_NewObject(context);
    if (JS_IsException(console)) {
        JS_FreeValue(context, global);
        return false;
    }
    struct Method {
        const char *name;
        int stream;
    };
    constexpr Method methods[] = {
        {"debug", kStreamStdout},
        {"info", kStreamStdout},
        {"log", kStreamStdout},
        {"warn", kStreamStderr},
        {"error", kStreamStderr},
    };
    for (const auto &method : methods) {
        JSValue function = JS_NewCFunctionMagic(
            context,
            consoleFunction,
            method.name,
            1,
            JS_CFUNC_generic_magic,
            method.stream);
        if (JS_IsException(function) ||
            JS_SetPropertyStr(context, console, method.name, function) < 0) {
            JS_FreeValue(context, console);
            JS_FreeValue(context, global);
            return false;
        }
    }
    if (JS_SetPropertyStr(context, global, "console", console) < 0) {
        JS_FreeValue(context, global);
        return false;
    }
    JSValue print = JS_NewCFunctionMagic(
        context,
        consoleFunction,
        "print",
        1,
        JS_CFUNC_generic_magic,
        kStreamStdout);
    if (JS_IsException(print) || JS_SetPropertyStr(context, global, "print", print) < 0) {
        JS_FreeValue(context, global);
        return false;
    }
    JSValue script_args = JS_NewArray(context);
    if (JS_IsException(script_args) ||
        JS_SetPropertyUint32(
            context,
            script_args,
            0,
            JS_NewStringLen(context, source_name.data(), source_name.size())) < 0 ||
        JS_SetPropertyStr(context, global, "scriptArgs", script_args) < 0) {
        JS_FreeValue(context, global);
        return false;
    }
    JS_FreeValue(context, global);
    return true;
}

std::string valueToString(JSContext *context, JSValueConst value) {
    size_t size = 0;
    const char *characters = JS_ToCStringLen(context, &size, value);
    if (characters == nullptr) {
        JSValue conversion_error = JS_GetException(context);
        JS_FreeValue(context, conversion_error);
        return "QuickJS value could not be converted to text";
    }
    std::string result(characters, size);
    JS_FreeCString(context, characters);
    return result;
}

std::string takeException(JSContext *context) {
    JSValue exception = JS_GetException(context);
    std::string summary = valueToString(context, exception);
    JSValue stack = JS_GetPropertyStr(context, exception, "stack");
    std::string trace;
    if (!JS_IsException(stack) && !JS_IsUndefined(stack) && !JS_IsNull(stack)) {
        trace = valueToString(context, stack);
    }
    std::string result = summary;
    if (!trace.empty() && trace != summary) {
        if (!result.empty()) result.push_back('\n');
        result.append(trace);
    }
    JS_FreeValue(context, stack);
    JS_FreeValue(context, exception);
    return result;
}

bool isOutOfMemory(const std::string &message) {
    std::string lower;
    lower.reserve(message.size());
    std::transform(message.begin(), message.end(), std::back_inserter(lower), [](unsigned char value) {
        return static_cast<char>(std::tolower(value));
    });
    return lower.find("out of memory") != std::string::npos;
}

bool shouldStop(ExecutionState *state) {
    if (state->cancelled.load(std::memory_order_acquire)) {
        return true;
    }
    if (Clock::now() >= state->deadline) {
        state->timed_out.store(true, std::memory_order_release);
        state->cancelled.store(true, std::memory_order_release);
        return true;
    }
    return false;
}

bool finishModule(
    JSContext *context,
    JSRuntime *runtime,
    JSValue value,
    ExecutionState *state,
    std::string *error) {
    while (true) {
        if (shouldStop(state)) {
            JS_FreeValue(context, value);
            return false;
        }
        const int promise_state = static_cast<int>(JS_PromiseState(context, value));
        if (promise_state == JS_PROMISE_FULFILLED) {
            JSValue result = JS_PromiseResult(context, value);
            JS_FreeValue(context, result);
            JS_FreeValue(context, value);
            return true;
        }
        if (promise_state == JS_PROMISE_REJECTED) {
            JSValue reason = JS_PromiseResult(context, value);
            *error = valueToString(context, reason);
            JS_FreeValue(context, reason);
            JS_FreeValue(context, value);
            return false;
        }
        if (promise_state != JS_PROMISE_PENDING) {
            *error = "QuickJS module evaluation returned an invalid completion value";
            JS_FreeValue(context, value);
            return false;
        }
        JSContext *job_context = nullptr;
        const int jobs = JS_ExecutePendingJob(runtime, &job_context);
        if (jobs < 0) {
            *error = takeException(job_context == nullptr ? context : job_context);
            JS_FreeValue(context, value);
            return false;
        }
        if (jobs == 0) {
            *error = "The module is waiting for an unavailable host event loop";
            JS_FreeValue(context, value);
            return false;
        }
    }
}

bool drainPendingJobs(
    JSContext *context,
    JSRuntime *runtime,
    ExecutionState *state,
    std::string *error) {
    while (!shouldStop(state)) {
        JSContext *job_context = nullptr;
        const int jobs = JS_ExecutePendingJob(runtime, &job_context);
        if (jobs == 0) {
            return true;
        }
        if (jobs < 0) {
            *error = takeException(job_context == nullptr ? context : job_context);
            return false;
        }
    }
    return false;
}

const char *statusFor(ExecutionState *state, const std::string &message) {
    if (state->output_limited.load(std::memory_order_acquire)) {
        return kStatusOutputLimit;
    }
    if (state->timed_out.load(std::memory_order_acquire)) {
        return kStatusTimeout;
    }
    if (state->callback_failed.load(std::memory_order_acquire)) {
        return kStatusInternal;
    }
    if (state->cancelled.load(std::memory_order_acquire)) {
        return kStatusCancelled;
    }
    if (isOutOfMemory(message)) {
        return kStatusOutOfMemory;
    }
    return kStatusEvaluation;
}

std::string defaultMessage(const char *status, const std::string &message) {
    if (!message.empty()) {
        return message;
    }
    if (status == kStatusOutputLimit) return "QuickJS output exceeded the configured byte limit";
    if (status == kStatusTimeout) return "QuickJS execution timed out";
    if (status == kStatusCancelled) return "QuickJS execution was cancelled";
    if (status == kStatusOutOfMemory) return "QuickJS exhausted its configured memory budget";
    if (status == kStatusInternal) return "QuickJS output delivery failed";
    return "QuickJS evaluation failed";
}

}  // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_supermonster003_autojs6_plugin_quickjs_runtime_NativeQuickJsRuntime_nativeVersion(
    JNIEnv *env,
    jclass) {
    return env->NewStringUTF("2026-06-04");
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_supermonster003_autojs6_plugin_quickjs_runtime_NativeQuickJsRuntime_nativeCreateExecution(
    JNIEnv *,
    jclass,
    jlong timeout_millis,
    jlong memory_limit_bytes,
    jlong stack_limit_bytes,
    jlong output_limit_bytes) {
    if (timeout_millis <= 0 || memory_limit_bytes <= 0 || stack_limit_bytes <= 0 ||
        output_limit_bytes <= 0) {
        return 0;
    }
    auto *state = new (std::nothrow) ExecutionState(
        timeout_millis,
        static_cast<uint64_t>(memory_limit_bytes),
        static_cast<uint64_t>(stack_limit_bytes),
        static_cast<uint64_t>(output_limit_bytes));
    return state == nullptr ? 0 : toHandle(state);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_io_github_supermonster003_autojs6_plugin_quickjs_runtime_NativeQuickJsRuntime_nativeExecute(
    JNIEnv *env,
    jclass,
    jlong handle,
    jbyteArray source,
    jstring source_name,
    jint source_mode,
    jobject output_sink) {
    ExecutionState *state = fromHandle(handle);
    if (state == nullptr || source == nullptr || source_name == nullptr || output_sink == nullptr ||
        (source_mode != kSourceModeScript && source_mode != kSourceModeModule)) {
        return makeResult(env, kStatusInternal, "Invalid native QuickJS execution arguments");
    }
    bool expected = false;
    if (!state->executing.compare_exchange_strong(expected, true, std::memory_order_acq_rel)) {
        return makeResult(env, kStatusInternal, "QuickJS execution handle was already used");
    }

    const jsize source_size = env->GetArrayLength(source);
    // QuickJS consumes input_len bytes but also requires input[input_len] to be NUL.
    // Keep the terminator outside the immutable source snapshot passed to JS_Eval.
    std::vector<uint8_t> source_bytes(static_cast<size_t>(source_size) + 1U, 0);
    if (source_size > 0) {
        env->GetByteArrayRegion(
            source,
            0,
            source_size,
            reinterpret_cast<jbyte *>(source_bytes.data()));
    }
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        state->executing.store(false, std::memory_order_release);
        return makeResult(env, kStatusInternal, "Unable to copy the QuickJS source snapshot");
    }
    const std::string logical_name = utf16ToUtf8(env, source_name);
    jclass sink_class = env->GetObjectClass(output_sink);
    jmethodID output_method = sink_class == nullptr
        ? nullptr
        : env->GetMethodID(sink_class, "onOutput", "(I[B)V");
    if (sink_class != nullptr) {
        env->DeleteLocalRef(sink_class);
    }
    if (output_method == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        state->executing.store(false, std::memory_order_release);
        return makeResult(env, kStatusInternal, "QuickJS output sink is incompatible");
    }
    state->env = env;
    state->output_sink = output_sink;
    state->output_method = output_method;

    JSRuntime *runtime = JS_NewRuntime();
    if (runtime == nullptr) {
        state->env = nullptr;
        state->output_sink = nullptr;
        state->output_method = nullptr;
        state->executing.store(false, std::memory_order_release);
        return makeResult(env, kStatusOutOfMemory, "Unable to create a QuickJS runtime");
    }
    JS_SetMemoryLimit(runtime, static_cast<size_t>(state->memory_limit));
    JS_SetMaxStackSize(runtime, static_cast<size_t>(state->stack_limit));
    JS_SetInterruptHandler(runtime, interruptHandler, state);
    JSContext *context = JS_NewContext(runtime);
    if (context == nullptr) {
        JS_FreeRuntime(runtime);
        state->env = nullptr;
        state->output_sink = nullptr;
        state->output_method = nullptr;
        state->executing.store(false, std::memory_order_release);
        return makeResult(env, kStatusOutOfMemory, "Unable to create a QuickJS context");
    }
    JS_SetContextOpaque(context, state);

    std::string error;
    bool succeeded = installHostGlobals(context, logical_name);
    if (!succeeded) {
        error = takeException(context);
    } else if (shouldStop(state)) {
        succeeded = false;
    } else {
        const int eval_flags = source_mode == kSourceModeModule
            ? JS_EVAL_TYPE_MODULE
            : JS_EVAL_TYPE_GLOBAL;
        JSValue value = JS_Eval(
            context,
            reinterpret_cast<const char *>(source_bytes.data()),
            static_cast<size_t>(source_size),
            logical_name.c_str(),
            eval_flags);
        if (JS_IsException(value)) {
            error = takeException(context);
            succeeded = false;
        } else if (source_mode == kSourceModeModule) {
            succeeded = finishModule(context, runtime, value, state, &error);
        } else {
            JS_FreeValue(context, value);
            succeeded = drainPendingJobs(context, runtime, state, &error);
        }
    }

    std::fill(source_bytes.begin(), source_bytes.end(), 0);
    JS_FreeContext(context);
    JS_FreeRuntime(runtime);
    state->env = nullptr;
    state->output_sink = nullptr;
    state->output_method = nullptr;
    state->executing.store(false, std::memory_order_release);

    if (succeeded && !state->cancelled.load(std::memory_order_acquire)) {
        return makeResult(env, kStatusSuccess, "");
    }
    const char *status = statusFor(state, error);
    return makeResult(env, status, defaultMessage(status, error));
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_supermonster003_autojs6_plugin_quickjs_runtime_NativeQuickJsRuntime_nativeCancel(
    JNIEnv *,
    jclass,
    jlong handle) {
    ExecutionState *state = fromHandle(handle);
    if (state != nullptr) {
        state->cancelled.store(true, std::memory_order_release);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_supermonster003_autojs6_plugin_quickjs_runtime_NativeQuickJsRuntime_nativeDestroyExecution(
    JNIEnv *,
    jclass,
    jlong handle) {
    ExecutionState *state = fromHandle(handle);
    if (state != nullptr) {
        state->cancelled.store(true, std::memory_order_release);
        delete state;
    }
}
