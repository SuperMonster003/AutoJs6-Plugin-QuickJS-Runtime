******

### 릴리스 기록

******

# v1.0.3

###### 2026/09/19

* `수정` 공유 빌드 플러그인 1.8.3을 통해 AGP 9.1의 SDK XML v4 파싱 경고 및 JVM 단위 테스트 조립 작업에서 APK 네이티브 라이브러리 정렬 검사가 잘못 실행되는 문제 해결
* `개선` compileSdk 와 targetSdk 를 37 (Android 17) 로 올리며, 플러그인 동작은 새 대상 버전의 영향을 받지 않음

# v1.0.2

###### 2026/09/13

* `수정` 빌드 환경 언어와 관계없이 플러그인 메타데이터의 빌드 날짜를 영어로 통일

# v1.0.1

###### 2026/09/13

* `개선` 64비트 네이티브 라이브러리의 16 KB 페이지 정렬을 빌드 시 검증, manifest 계약 검사 및 JSON 보고서 지원
* `개선` 호스트 활성화, 메타데이터, 다국어 문서 및 서명된 APK 수집을 공통 규칙에 맞게 정리

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
* `개선` Native library를 16 KB alignment로 빌드하고 ELF PT_LOAD와 uncompressed APK data alignment를 검증하며 `PAGE_SIZE=16384`인 API 37 x86_64 `google_apis_ps16k` emulator에서 Binder와 QuickJS instrumentation suite 3개를 모두 통과합니다
* `개선` 실제 Android 9, 12, 13, 15 arm64 기기에서 runtime을 확인하고 JVM test, instrumentation test, Android Lint, source verification, APK inventory check를 추가합니다
* `의존성` MIT license의 공식 QuickJS 2026-06-04 source를 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)에서 포함합니다
