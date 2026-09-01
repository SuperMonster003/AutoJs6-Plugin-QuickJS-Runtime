******

### 發佈記錄

******

# v1.0.0

###### 2026/09/02

* `提示` 首個版本每次只接收一個不可變 JavaScript 源碼快照, 並刻意不提供 AutoJs6 全域物件, Java bridge, 檔案系統, 網絡, timer, 原生 addon 或多檔 import
* `功能` 透過 `"quickjs";` 指令或 `*.quickjs.js` 和 `*.quickjs.mjs` 標記選擇內嵌的官方 QuickJS 2026-06-04 引擎執行 JavaScript, 不會把執行轉交 Rhino, Node.js 或 Bun
* `功能` 透過版本化 AIDL 契約發現受保護 runtime service, 使用 `ParcelFileDescriptor` 傳輸源碼, 並透過 oneway callback 串流返回有界 stdout 和 stderr 事件
* `功能` 支援 script 和單檔 ES module, 包括 BigInt, Unicode, Promise job, module top-level await, error, timeout, output limit, memory 和 stack limit, cancellation 及 prewarm
* `功能` 發佈採用相同原生引擎契約的 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` 和 `universal` APK
* `功能` 提供 Wake 啟用, 完整 PluginInfo metadata, 以及 10 種語言的插件中心說明, README 和 changelog
* `改善` 在隔離的 `:quickjs_runtime` 程序中為每個請求建立全新 `JSRuntime` 和 `JSContext`, 並且不編譯 `quickjs-libc` 及檔案系統和網絡 helper
* `改善` 按 archive URL, archive SHA-256, upstream commit, 單檔大小和單檔 SHA-256 固定逐 byte 一致的官方源碼
* `改善` 以 16 KB page alignment 建置原生 library, 並驗證 ELF PT_LOAD 以及 APK 未壓縮 data alignment, 同時清楚記錄尚未完成真實 16 KB page size runtime 測試
* `改善` 在真實 Android 9, 12, 13 和 15 arm64 裝置上覆蓋 runtime 行為, 並提供 JVM test, instrumentation test, Android Lint, source verification 和 APK inventory check
* `依賴` 內嵌採用 MIT 授權的官方 QuickJS 2026-06-04 源碼, 對應 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
