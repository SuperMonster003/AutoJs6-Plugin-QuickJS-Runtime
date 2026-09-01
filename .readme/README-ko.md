<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/drawable-nodpi/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>분리된 Android 프로세스에서 독립 QuickJS 엔진으로 JavaScript 실행</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어

******

현재 README.md는 다음 언어를 지원합니다:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### 소개

******

QuickJS Runtime은 AutoJs6에서 [QuickJS](https://bellard.org/quickjs/)를 독립 JavaScript 엔진으로 선택할 수 있게 하는 독립 Android 플러그인입니다. 각 요청은 격리된 `:quickjs_runtime` 프로세스에서 새 native runtime과 context를 받습니다. 소스와 이벤트는 버전이 지정된 Binder contract를 통과하므로 Rhino, Node.js, Bun의 alias가 아닌 실제 QuickJS 실행입니다.

******

### 설치 및 사용

******

1. Android 7.0 (API 24) 이상에서 AutoJs6 build 5279 (6.8.0) 이상을 사용합니다.
2. 기기 ABI에 맞는 release APK를 설치합니다. ABI를 모르면 더 큰 `universal` APK를 사용합니다.
3. AutoJs6 플러그인 센터를 열고 QuickJS Runtime을 활성화합니다. Android가 새 패키지를 중지 상태로 두었다면 host의 `활성화` 작업을 사용합니다.
4. `.js` 또는 `.mjs` 파일 시작 부분에 독립 directive `"quickjs";`를 놓고 AutoJs6에서 평소처럼 실행합니다.

> 버전 1.0은 요청마다 변경 불가능한 소스 snapshot 하나를 받습니다. Project tree를 전송하거나 상대 file import를 해석하지 않으며 AutoJs6 globals, Java bridge, filesystem, network, `std`, `os` module을 의도적으로 노출하지 않습니다.

******

### 빠른 시작

******

이 파일을 실행하여 AutoJs6가 QuickJS를 선택하고 native runtime을 시작했는지 확인합니다:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

출력은 QuickJS 2026-06-04를 표시하고 Unicode 텍스트와 BigInt 식 결과를 출력합니다.

******

### 호환성과 범위

******

- 엔진: 공식 QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), source archive SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 플랫폼: Android 7.0 (API 24) 이상. 단일 ABI APK는 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`을 지원하며 `universal` APK는 네 가지 모두를 포함합니다.
- 언어: JavaScript script와 단일 파일 ES module을 지원하며 BigInt, Unicode, Promise job, module top-level await를 포함합니다. TypeScript는 parse하지 않습니다.
- 요청 제한: 소스 8 MiB, stdout과 stderr 합계 4 MiB, 기본 timeout 60 seconds, 기본 memory 64 MiB, 최대 memory 256 MiB, 기본 stack 1 MiB.
- Runtime 경계: 플러그인은 제한된 `console`과 `print` 출력 helper만 추가합니다. Host automation API, Java object, filesystem, network API, `std`, `os`, native addon, timer, 다중 파일 module graph는 버전 1.0 범위 밖입니다.

******

### 권한과 무결성

******

- Wake Activity와 두 exported service는 `org.autojs.permission.PLUGIN` 권한을 요구합니다. 실행은 플러그인 UID로 별도 `:quickjs_runtime` 프로세스에서 진행됩니다.
- 각 요청마다 새 `JSRuntime`과 `JSContext`를 만듭니다. Memory, stack, time, source, output 제한을 적용하며 취소는 QuickJS interrupt handler에 전달됩니다.
- 소스는 read-only `ParcelFileDescriptor`로 전달됩니다. 출력은 제한된 oneway callback chunk를 사용하며 terminal Bundle에는 상태와 진단 요약만 포함됩니다.
- 저장소는 고정된 공식 archive의 byte-exact file을 보관하고 size와 SHA-256을 검증합니다. `quickjs-libc`, filesystem helper, network helper는 엔진에 compile되지 않습니다.

******

### 플러그인 인터페이스

******

AutoJs6 host와 플러그인 개발자를 위한 안정적인 식별자와 제한:

```text
application id: io.github.supermonster003.autojs6.plugin.quickjs.runtime
plugin id: quickjs-runtime
engine: quickjs
variant: quickjs-2026-06-04
service action: org.autojs.plugin.quickjs.RUNTIME
service category: quickjs
aidl interface: org.autojs.plugin.quickjs.runtime.api.IQuickJsRuntimePlugin
contract version: 1
aidl methods: getInfo(), getRuntimeInfo(), runScript(request, source, callback), cancelScript(executionId), prewarmRuntime()
minimum host build: 5279 (6.8.0)
source limit: 8 MiB
combined stdout/stderr budget: 4 MiB
default timeout: 60 seconds
default memory limit: 64 MiB
maximum memory limit: 256 MiB
default stack limit: 1 MiB
```

`QuickJsRuntimeService`는 action `org.autojs.plugin.quickjs.RUNTIME`와 category `quickjs`로 검색됩니다. `runScript`는 request Bundle과 read-only source descriptor를 받고 제한된 stdout과 stderr 이벤트를 callback으로 stream한 뒤 상태, 시간, 진단이 든 terminal Bundle을 반환합니다. `cancelScript`는 dispatch 전과 실행 중 안전하며 `prewarmRuntime`은 native engine readiness를 확인합니다.

16 KB 상태: native linker는 16 KB maximum page size로 설정되며 APK verifier는 각 ELF PT_LOAD alignment와 uncompressed ZIP data alignment를 검사합니다. 실제 16 KB page-size 기기나 emulator에서 아직 실행하지 않았으므로 end-to-end 지원은 주장하지 않습니다.

******

### 릴리스 기록

******

#### v1.0.0

_2026/09/02_

- `안내` 첫 release는 immutable JavaScript source snapshot 하나를 받으며 AutoJs6 globals, Java bridge, filesystem, network, timer, native addon, multi-file import를 제공하지 않습니다
- `기능` `"quickjs";` directive로 선택한 내장 공식 QuickJS 2026-06-04 engine에서 JavaScript를 실행하며 Rhino, Node.js, Bun으로 전달하지 않습니다
- `기능` Versioned AIDL로 protected runtime service를 검색하고 `ParcelFileDescriptor`로 source를 전송하며 제한된 stdout과 stderr 이벤트를 oneway callback으로 stream합니다
- `기능` Script와 단일 파일 ES module에서 BigInt, Unicode, Promise job, top-level await, error, timeout, output, memory, stack limit, cancellation, prewarm을 지원합니다
- `기능` 동일한 native engine contract를 사용하는 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`, `universal` APK를 제공합니다
- `기능` Wake activation, 전체 PluginInfo metadata, 10개 언어의 instruction, README, changelog를 제공합니다
- `개선` 격리된 `:quickjs_runtime` 프로세스에서 요청마다 새 `JSRuntime`과 `JSContext`를 만들고 `quickjs-libc`, filesystem helper, network helper를 제외합니다
- `개선` 공식 byte-exact source를 archive URL, archive SHA-256, upstream commit, 각 file size, 각 SHA-256으로 고정합니다
- `개선` Native library를 16 KB alignment로 빌드하고 ELF PT_LOAD와 uncompressed APK data alignment를 검증하며 실제 16 KB page test가 완료되지 않았음을 기록합니다
- `개선` 실제 Android 9, 12, 13, 15 arm64 기기에서 runtime을 확인하고 JVM test, instrumentation test, Android Lint, source verification, APK inventory check를 추가합니다
- `의존성` MIT license의 공식 QuickJS 2026-06-04 source를 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)에서 포함합니다

##### 더 많은 릴리스 기록

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드와 검증

******

빌드에는 JDK 17 이상, Python 3, Android SDK 36, NDK 28.2.13676358, CMake 3.22.1이 필요합니다. 표준 검사는 고정 QuickJS source, 생성 문서, JVM test, 모든 APK variant, instrumentation package, Android Lint, ELF alignment, native library inventory를 검증합니다.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### 현지화와 문서 생성

******

JSON과 Markdown template를 편집한 뒤 `py .python/generate_markdown.py`를 실행합니다. 생성된 README, changelog, plugin instruction 파일을 직접 편집하지 마십시오. `--check`는 파일을 쓰지 않고 language shape, version alignment, localized resource, orphan file, drift를 검증합니다.

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.readme/template_plugin_instruction.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values*/strings.xml
app/src/main/res/raw*/plugin_instruction.md
```

******

### 라이선스

******

플러그인 코드는 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE)을 사용합니다. 포함된 공식 QuickJS source는 MIT입니다. 출처와 포함 notice는 [Third-Party Notices](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md)를 참조하십시오.

******

### 링크

******

- AutoJs6 프로젝트: https://github.com/SuperMonster003/AutoJs6
- QuickJS 공식 사이트: https://bellard.org/quickjs/
- QuickJS 문서: https://bellard.org/quickjs/quickjs.html
- QuickJS 소스 저장소: https://github.com/bellard/quickjs
- 제3자 고지: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md
