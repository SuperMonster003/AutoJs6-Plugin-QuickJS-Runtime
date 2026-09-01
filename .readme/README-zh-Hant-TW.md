<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>在獨立 Android 程序中使用獨立 QuickJS 引擎執行 JavaScript</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言

******

目前 README.md 支援以下語言:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- 繁體中文 台灣 [zh-Hant-TW] # 目前

******

### 簡介

******

QuickJS Runtime 是獨立 Android 外掛, 讓 AutoJs6 可選擇 [QuickJS](https://bellard.org/quickjs/) 作為獨立 JavaScript 引擎. 每個請求都會在隔離的 `:quickjs_runtime` 程序中取得全新的原生執行環境和 context. 原始碼與事件經版本化 Binder 契約傳輸, 因此這是真正的 QuickJS 執行, 並非 Rhino, Node.js 或 Bun 的別名.

******

### 安裝和使用

******

1. 在 Android 7.0 (API 24) 或更新版本上使用 AutoJs6 build 5279 (6.8.0) 或更新版本.
2. 安裝與裝置 ABI 對應的發行 APK. 若不確定 ABI, 可安裝較大的 `universal` APK.
3. 開啟 AutoJs6 外掛中心並啟用 QuickJS Runtime. 若 Android 仍讓新安裝套件保持停止狀態, 請使用宿主顯示的 `啟用` 操作.
4. 可在 `.js` 或 `.mjs` 檔案開頭放置獨立指令 `"quickjs";`, 也可將檔案命名為 script 模式的 `*.quickjs.js` 或 ES module 模式的 `*.quickjs.mjs`; 然後從 AutoJs6 正常執行.

> 1.0 版每次請求只接收一個不可變原始碼快照. 它不會傳輸專案目錄或解析相對檔案 import, 並刻意不公開 AutoJs6 全域物件, Java bridge, 檔案系統, 網路, `std` 或 `os` module.

******

### 快速開始

******

執行此檔案以確認 AutoJs6 已選擇 QuickJS 並啟動原生執行環境:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

輸出會識別 QuickJS 2026-06-04, 顯示 Unicode 文字並計算 BigInt 運算式.

******

### 相容性和範圍

******

- 引擎: 官方 QuickJS 2026-06-04, 修訂版 `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), 原始碼封存 SHA-256 為 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 平台: Android 7.0 (API 24) 或更新版本. 單 ABI APK 支援 `arm64-v8a`, `armeabi-v7a`, `x86_64` 和 `x86`; `universal` APK 包含全部四種 ABI.
- 語言: 支援 JavaScript script 和單檔 ES module, 包括 BigInt, Unicode, Promise job 和 module top-level await. 不解析 TypeScript.
- 請求限制: 原始碼最大 8 MiB, stdout 與 stderr 合計最大 4 MiB, 預設 timeout 60 seconds, 預設記憶體 64 MiB, 最大記憶體 256 MiB, 預設 stack 1 MiB.
- 執行環境邊界: 外掛只加入有界的 `console` 和 `print` 輸出 helper. 宿主自動化 API, Java 物件, 檔案系統和網路 API, `std`, `os`, 原生 addon, timer 及多檔 module graph 不屬於 1.0 版範圍.

******

### 權限和完整性

******

- Wake Activity 和兩個匯出 service 均要求 `org.autojs.permission.PLUGIN`; 執行使用外掛 UID 並位於獨立的 `:quickjs_runtime` 程序.
- 每個請求都建立新的 `JSRuntime` 和 `JSContext`. 記憶體, stack, 時間, 原始碼和輸出限制均會強制執行, 取消操作會到達 QuickJS interrupt handler.
- 原始碼經唯讀 `ParcelFileDescriptor` 傳入. 輸出使用有界 oneway callback 分塊, 終止 Bundle 只包含狀態和診斷摘要.
- 儲存庫保存固定官方封存中的逐 byte 一致檔案, 並驗證大小和 SHA-256. 引擎未編譯 `quickjs-libc`, 檔案系統 helper 或網路 helper.

******

### 外掛介面

******

供 AutoJs6 宿主和外掛開發者使用的穩定識別碼和限制:

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

`QuickJsRuntimeService` 經 action `org.autojs.plugin.quickjs.RUNTIME` 和 category `quickjs` 被發現. `runScript` 接收請求 Bundle 和唯讀原始碼 descriptor, 經 callback 串流回傳有界 stdout 與 stderr 事件, 並回傳包含狀態, 時間和診斷的終止 Bundle. `cancelScript` 可在排程前或執行期間安全呼叫, `prewarmRuntime` 會執行原生引擎就緒檢查.

16 KB 狀態: 原生 linker 已設定 16 KB 最大 page size, APK 驗證器會檢查每個 ELF PT_LOAD 對齊以及未壓縮 ZIP data 對齊. 已在 API 37 x86_64 `google_apis_ps16k` emulator 上完成端到端執行驗證 (`PAGE_SIZE=16384`), 3 組 Binder 與 QuickJS instrumentation 測試全部通過.

******

### 發行記錄

******

#### v1.0.0

_2026/09/02_

- `提示` 首個版本每次只接收一個不可變 JavaScript 原始碼快照, 並刻意不提供 AutoJs6 全域物件, Java bridge, 檔案系統, 網路, timer, 原生 addon 或多檔 import
- `功能` 透過 `"quickjs";` 指令或 `*.quickjs.js` 和 `*.quickjs.mjs` 標記選擇內嵌的官方 QuickJS 2026-06-04 引擎執行 JavaScript, 不會把執行轉交 Rhino, Node.js 或 Bun
- `功能` 透過版本化 AIDL 契約發現受保護 runtime service, 使用 `ParcelFileDescriptor` 傳輸原始碼, 並透過 oneway callback 串流回傳有界 stdout 和 stderr 事件
- `功能` 支援 script 和單檔 ES module, 包括 BigInt, Unicode, Promise job, module top-level await, error, timeout, output limit, memory 和 stack limit, cancellation 及 prewarm
- `功能` 發行採用相同原生引擎契約的 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` 和 `universal` APK
- `功能` 提供 Wake 啟用, 完整 PluginInfo metadata, 以及 10 種語言的外掛中心說明, README 和 changelog
- `改善` 在隔離的 `:quickjs_runtime` 程序中為每個請求建立全新 `JSRuntime` 和 `JSContext`, 並且不編譯 `quickjs-libc` 及檔案系統和網路 helper
- `改善` 按 archive URL, archive SHA-256, upstream commit, 單檔大小和單檔 SHA-256 固定逐 byte 一致的官方原始碼
- `改善` 以 16 KB page alignment 建置原生 library, 驗證 ELF PT_LOAD 與 APK 未壓縮 data alignment, 並在回報 `PAGE_SIZE=16384` 的 API 37 x86_64 `google_apis_ps16k` emulator 上通過全部 3 組 Binder 與 QuickJS instrumentation 測試
- `改善` 在真實 Android 9, 12, 13 和 15 arm64 裝置上涵蓋 runtime 行為, 並提供 JVM test, instrumentation test, Android Lint, source verification 和 APK inventory check
- `相依項目` 內嵌採用 MIT 授權的官方 QuickJS 2026-06-04 原始碼, 對應 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)

##### 更多發行記錄

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

******

### 建置和驗證

******

建置需要 JDK 17 或更新版本, Python 3, Android SDK 36, NDK 28.2.13676358 和 CMake 3.22.1. 標準檢查會驗證固定 QuickJS 原始碼, 產生文件, JVM 測試, 全部 APK variant, instrumentation 套件, Android Lint, ELF 對齊和原生 library 清單.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### 本地化和文件產生

******

編輯 JSON 和 Markdown template 後執行 `py .python/generate_markdown.py`. 不要手動編輯產生的 README, changelog 或外掛說明檔案. `--check` 模式會在不寫入檔案下驗證語言結構, 版本一致性, 本地化資源, 孤立檔案和產生檔案漂移.

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

### 授權條款

******

外掛程式碼採用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). 內嵌的官方 QuickJS 原始碼採用 MIT 授權. 來源和隨附聲明請參閱 [第三方聲明](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md).

******

### 連結

******

- AutoJs6 專案: https://github.com/SuperMonster003/AutoJs6
- QuickJS 官方網站: https://bellard.org/quickjs/
- QuickJS 文件: https://bellard.org/quickjs/quickjs.html
- QuickJS 原始碼儲存庫: https://github.com/bellard/quickjs
- 第三方聲明: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md
