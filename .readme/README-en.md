<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/drawable-nodpi/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>Runs JavaScript with an independent QuickJS engine in an isolated Android process</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The current README.md supports the following languages:

- English [en] # current
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### Introduction

******

QuickJS Runtime is a standalone Android plugin that lets AutoJs6 select [QuickJS](https://bellard.org/quickjs/) as an independent JavaScript engine. Every request gets a fresh native runtime and context inside the isolated `:quickjs_runtime` process. Source and events cross a versioned Binder contract, so this is real QuickJS execution and not an alias for Rhino, Node.js, or Bun.

******

### Install and Use

******

1. Use AutoJs6 build 5279 (6.8.0) or later on Android 7.0 (API 24) or later.
2. Install the release APK matching the device ABI, or use the larger `universal` APK when the ABI is unknown.
3. Open the AutoJs6 plugin center and enable QuickJS Runtime. If Android left the newly installed package stopped, use the host `Activate` action.
4. Put the standalone directive `"quickjs";` at the beginning of a `.js` or `.mjs` file, then run it normally from AutoJs6.

> Version 1.0 accepts one immutable source snapshot per request. It does not transfer a project tree or resolve relative file imports, and it deliberately exposes no AutoJs6 globals, Java bridge, filesystem, network, `std`, or `os` module.

******

### Quick Start

******

Run this file to confirm that AutoJs6 selected QuickJS and started the native runtime:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

The output identifies QuickJS 2026-06-04, prints Unicode text, and evaluates a BigInt expression.

******

### Compatibility and Scope

******

- Engine: official QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), with source archive SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- Platform: Android 7.0 (API 24) or later. Single-ABI APKs support `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`; the `universal` APK contains all four.
- Language: JavaScript scripts and single-file ES modules are supported, including BigInt, Unicode, Promise jobs, and module top-level await. TypeScript is not parsed.
- Request bounds: source up to 8 MiB, combined stdout and stderr up to 4 MiB, default timeout 60 seconds, default memory 64 MiB, maximum memory 256 MiB, and default stack 1 MiB.
- Runtime boundary: only bounded `console` and `print` output helpers are added by the plugin. Host automation APIs, Java objects, filesystem and network APIs, `std`, `os`, native addons, timers, and multi-file module graphs are outside version 1.0.

******

### Permissions and Integrity

******

- The Wake activity and both exported services require `org.autojs.permission.PLUGIN`; execution occurs under the plugin UID in the separate `:quickjs_runtime` process.
- Each request receives a new `JSRuntime` and `JSContext`. Memory, stack, time, source, and output limits are enforced, and cancellation reaches the QuickJS interrupt handler.
- Source arrives through a read-only `ParcelFileDescriptor`. Output uses bounded oneway callback chunks, while terminal Bundles contain only status and diagnostic summaries.
- The repository vendors byte-exact files from the pinned official archive and verifies sizes and SHA-256 digests. `quickjs-libc`, filesystem helpers, and network helpers are not compiled into the engine.

******

### Plugin Interface

******

Stable identifiers and limits for AutoJs6 host and plugin developers:

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

`QuickJsRuntimeService` is discovered by action `org.autojs.plugin.quickjs.RUNTIME` and category `quickjs`. `runScript` receives a request Bundle and a read-only source descriptor, streams bounded stdout and stderr events through the callback, and returns a terminal Bundle containing status, timing, and diagnostics. `cancelScript` is safe before or during dispatch, and `prewarmRuntime` performs a native engine readiness check.

16 KB status: the native linker is configured for 16 KB maximum page size, and the APK verifier checks each ELF PT_LOAD alignment plus uncompressed ZIP data alignment. No real 16 KB page-size device or emulator run has been completed, so end-to-end 16 KB runtime support is not claimed yet.

******

### Release History

******

#### v1.0.0

_2026/09/02_

- `Hint` The first release accepts one immutable JavaScript source snapshot and deliberately provides no AutoJs6 globals, Java bridge, filesystem, network, timers, native addons, or multi-file imports
- `Feature` Run JavaScript through the embedded official QuickJS 2026-06-04 engine selected by the `"quickjs";` directive, without forwarding execution to Rhino, Node.js, or Bun
- `Feature` Discover a protected runtime service through a versioned AIDL contract, transfer source with `ParcelFileDescriptor`, and stream bounded stdout and stderr events through oneway callbacks
- `Feature` Support scripts and single-file ES modules with BigInt, Unicode, Promise jobs, module top-level await, errors, timeout, output limits, memory and stack limits, cancellation, and prewarming
- `Feature` Ship `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`, and `universal` APKs with the same native engine contract
- `Feature` Provide Wake activation, complete PluginInfo metadata, plugin-center instructions, README, and changelog content in 10 languages
- `Improvement` Create a fresh `JSRuntime` and `JSContext` per request in the isolated `:quickjs_runtime` process and omit `quickjs-libc` plus filesystem and network helpers
- `Improvement` Pin byte-exact official source files by archive URL, archive SHA-256, upstream commit, individual file sizes, and individual SHA-256 digests
- `Improvement` Build native libraries with 16 KB page alignment and verify ELF PT_LOAD plus uncompressed APK data alignment, while recording that no real 16 KB page-size runtime test has been completed
- `Improvement` Cover runtime behavior on real Android 9, 12, 13, and 15 arm64 devices, with JVM tests, instrumentation tests, Android Lint, source verification, and APK inventory checks
- `Dependency` Embed the MIT-licensed official QuickJS 2026-06-04 source at commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)

##### For more release history

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build and Verification

******

Building requires JDK 17 or later, Python 3, Android SDK 36, NDK 28.2.13676358, and CMake 3.22.1. The standard checks verify the pinned QuickJS source, generated documentation, JVM tests, all APK variants, instrumentation packaging, Android Lint, ELF alignment, and native-library inventory.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### Localization and Docs Generation

******

Edit the JSON and Markdown templates, then run `py .python/generate_markdown.py`. Generated README, changelog, and plugin-instruction files must not be edited by hand. The `--check` mode validates language shape, version alignment, localized resources, orphan files, and generated-file drift without writing.

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

### License

******

Plugin code is licensed under the [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). The embedded official QuickJS source is licensed under MIT. See [Third-Party Notices](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md) for provenance and bundled notices.

******

### Links

******

- AutoJs6 project: https://github.com/SuperMonster003/AutoJs6
- QuickJS official site: https://bellard.org/quickjs/
- QuickJS documentation: https://bellard.org/quickjs/quickjs.html
- QuickJS source repository: https://github.com/bellard/quickjs
- Third-party notices: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md
