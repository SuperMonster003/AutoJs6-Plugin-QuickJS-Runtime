package io.github.supermonster003.autojs6.plugin.quickjs.runtime;

final class NativeQuickJsRuntime {
    static final int STREAM_STDOUT = 1;
    static final int STREAM_STDERR = 2;

    static {
        System.loadLibrary("autojs_quickjs_runtime");
    }

    interface OutputSink {
        void onOutput(int stream, byte[] utf8);
    }

    static native String nativeVersion();

    static native long nativeCreateExecution(
            long timeoutMillis,
            long memoryLimitBytes,
            long stackLimitBytes,
            long outputLimitBytes
    );

    static native String[] nativeExecute(
            long handle,
            byte[] source,
            String sourceName,
            int sourceMode,
            OutputSink outputSink
    );

    static native void nativeCancel(long handle);

    static native void nativeDestroyExecution(long handle);

    private NativeQuickJsRuntime() {
    }
}
