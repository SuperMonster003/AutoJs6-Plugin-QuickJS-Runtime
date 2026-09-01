******

### 릴리스 기록

******

# v1.0.0

###### 2026/09/02

* `안내` 첫 release는 immutable JavaScript source snapshot 하나를 받으며 AutoJs6 globals, Java bridge, filesystem, network, timer, native addon, multi-file import를 제공하지 않습니다
* `기능` `"quickjs";` directive 또는 `*.quickjs.js`와 `*.quickjs.mjs` marker로 선택한 내장 공식 QuickJS 2026-06-04 engine에서 JavaScript를 실행하며 Rhino, Node.js, Bun으로 전달하지 않습니다
* `기능` Versioned AIDL로 protected runtime service를 검색하고 `ParcelFileDescriptor`로 source를 전송하며 제한된 stdout과 stderr 이벤트를 oneway callback으로 stream합니다
* `기능` Script와 단일 파일 ES module에서 BigInt, Unicode, Promise job, top-level await, error, timeout, output, memory, stack limit, cancellation, prewarm을 지원합니다
* `기능` 동일한 native engine contract를 사용하는 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`, `universal` APK를 제공합니다
* `기능` Wake activation, 전체 PluginInfo metadata, 10개 언어의 instruction, README, changelog를 제공합니다
* `개선` 격리된 `:quickjs_runtime` 프로세스에서 요청마다 새 `JSRuntime`과 `JSContext`를 만들고 `quickjs-libc`, filesystem helper, network helper를 제외합니다
* `개선` 공식 byte-exact source를 archive URL, archive SHA-256, upstream commit, 각 file size, 각 SHA-256으로 고정합니다
* `개선` Native library를 16 KB alignment로 빌드하고 ELF PT_LOAD와 uncompressed APK data alignment를 검증하며 실제 16 KB page test가 완료되지 않았음을 기록합니다
* `개선` 실제 Android 9, 12, 13, 15 arm64 기기에서 runtime을 확인하고 JVM test, instrumentation test, Android Lint, source verification, APK inventory check를 추가합니다
* `의존성` MIT license의 공식 QuickJS 2026-06-04 source를 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)에서 포함합니다
