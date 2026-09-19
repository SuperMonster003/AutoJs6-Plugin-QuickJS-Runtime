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
- 繁體中文 香港 [zh-Hant-HK] # 目前
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### 簡介

******

QuickJS Runtime 是一個獨立 Android 插件, 讓 AutoJs6 可選擇 [QuickJS](https://bellard.org/quickjs/) 作為獨立 JavaScript 引擎. 每個請求都會在隔離的 `:quickjs_runtime` 程序中取得全新的原生 runtime 和 context. 源碼與事件經版本化 Binder 契約傳輸, 因此這是真正的 QuickJS 執行, 並非 Rhino, Node.js 或 Bun 的別名.

******

### 安裝和使用

******

1. 在 Android 7.0 (API 24) 或更高版本上使用 AutoJs6 build 5279 (6.8.0) 或更高版本.
2. 安裝與裝置 ABI 對應的發佈 APK. 若不確定 ABI, 可安裝較大的 `universal` APK.
3. 開啟 AutoJs6 插件中心並啟用 QuickJS Runtime. 若 Android 仍把新安裝套件保持在停止狀態, 請使用宿主顯示的 `啟用` 操作.
4. 可在 `.js` 或 `.mjs` 檔案開頭放置獨立指令 `"quickjs";`, 也可把檔案命名為 script 模式的 `*.quickjs.js` 或 ES module 模式的 `*.quickjs.mjs`; 然後從 AutoJs6 正常執行.

> 1.0 版本每次請求只接收一個不可變源碼快照. 它不會傳輸專案目錄或解析相對檔案 import, 並刻意不公開 AutoJs6 全域物件, Java bridge, 檔案系統, 網絡, `std` 或 `os` module.

******

### 快速開始

******

執行此檔案以確認 AutoJs6 已選擇 QuickJS 並啟動原生 runtime:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

輸出會標識 QuickJS 2026-06-04, 顯示 Unicode 文字並計算 BigInt 表達式.

******

### 兼容性和範圍

******

- 引擎: 官方 QuickJS 2026-06-04, 修訂版 `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), 源碼封存 SHA-256 為 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 平台: Android 7.0 (API 24) 或更高版本. 單 ABI APK 支援 `arm64-v8a`, `armeabi-v7a`, `x86_64` 和 `x86`; `universal` APK 包含全部四種 ABI.
- 語言: 支援 JavaScript script 和單檔 ES module, 包括 BigInt, Unicode, Promise job 和 module top-level await. 不解析 TypeScript.
- 請求限制: 源碼最大 8 MiB, stdout 與 stderr 合計最大 4 MiB, 預設 timeout 60 seconds, 預設記憶體 64 MiB, 最大記憶體 256 MiB, 預設 stack 1 MiB.
- Runtime 邊界: 插件只加入有界的 `console` 和 `print` 輸出 helper. 宿主自動化 API, Java 物件, 檔案系統和網絡 API, `std`, `os`, 原生 addon, timer 及多檔 module graph 不屬於 1.0 版本範圍.

******

### 權限和完整性

******

- Wake Activity 和兩個匯出 service 均要求 `org.autojs.permission.PLUGIN`; 執行使用插件 UID 並位於獨立的 `:quickjs_runtime` 程序.
- 每個請求都建立新的 `JSRuntime` 和 `JSContext`. 記憶體, stack, 時間, 源碼和輸出限制均會強制執行, 取消操作會到達 QuickJS interrupt handler.
- 源碼經唯讀 `ParcelFileDescriptor` 傳入. 輸出使用有界 oneway callback 分塊, 終止 Bundle 只包含狀態和診斷摘要.
- 倉庫保存固定官方封存中的逐 byte 一致檔案, 並驗證大小和 SHA-256. 引擎未編譯 `quickjs-libc`, 檔案系統 helper 或網絡 helper.

******

### 插件介面

******

供 AutoJs6 宿主和插件開發者使用的穩定標識和限制:

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

`QuickJsRuntimeService` 經 action `org.autojs.plugin.quickjs.RUNTIME` 和 category `quickjs` 被發現. `runScript` 接收請求 Bundle 和唯讀源碼 descriptor, 經 callback 串流返回有界 stdout 與 stderr 事件, 並返回包含狀態, 時間和診斷的終止 Bundle. `cancelScript` 可在調度前或執行期間安全呼叫, `prewarmRuntime` 會執行原生引擎就緒檢查.

16 KB 狀態: 原生 linker 已設定 16 KB 最大 page size, APK 驗證器會檢查每個 ELF PT_LOAD 對齊以及未壓縮 ZIP data 對齊. 已在 API 37 x86_64 `google_apis_ps16k` emulator 上完成端到端執行驗證 (`PAGE_SIZE=16384`), 3 組 Binder 與 QuickJS instrumentation 測試全部通過.

******

### 發佈記錄

******

#### v1.0.3

_2026/09/19_

- `修正` AGP 9.1 構建時的 SDK XML v4 解析警告及 JVM 單元測試組裝任務誤觸發 APK 原生程式庫對齊檢查的問題 (共用構建外掛 1.8.3)
- `改善` 將 compileSdk 與 targetSdk 提升到 37 (Android 17), 插件行為不受新目標版本影響

#### v1.0.2

_2026/09/13_

- `修正` 插件中繼資料的建置日期固定使用英文, 不受建置機器語言影響

#### v1.0.1

_2026/09/13_

- `改善` 建置階段校驗 64 位原生程式庫的 16 KB 頁面大小對齊, 檢查 manifest 契約並輸出 JSON 報告
- `改善` 宿主啟用, 外掛中繼資料, 多語言文件與簽章發佈彙整遵循統一外掛規範

##### 更多發佈記錄

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-HK.md)

******

### 建置和驗證

******

建置需要 JDK 17 或更高版本, Python 3, Android SDK 37, NDK 28.2.13676358 和 CMake 3.22.1. 標準檢查會驗證固定 QuickJS 源碼, 產生文件, JVM 測試, 全部 APK variant, instrumentation 套件, Android Lint, ELF 對齊和原生 library 清單.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### 本地化和文件產生

******

編輯 JSON 和 Markdown template 後執行 `py .python/generate_markdown.py`. 不要手動編輯產生的 README, changelog 或插件說明檔案. `--check` 模式會在不寫入檔案下驗證語言結構, 版本一致性, 本地化資源, 孤立檔案和產生檔案漂移.

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

插件程式碼採用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). 內嵌的官方 QuickJS 源碼採用 MIT 授權. 來源和隨附聲明請參閱 [第三方聲明](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md).

******

### 連結

******

- AutoJs6 專案: https://github.com/SuperMonster003/AutoJs6
- QuickJS 官方網站: https://bellard.org/quickjs/
- QuickJS 文件: https://bellard.org/quickjs/quickjs.html
- QuickJS 源碼倉庫: https://github.com/bellard/quickjs
- 第三方聲明: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
