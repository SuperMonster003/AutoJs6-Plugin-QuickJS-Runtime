******

### 發佈記錄

******

# v1.0.4

###### 2026/10/05

* `改善` 應用程式與外掛程式中心圖示採用透明彩色閃電, 保留 SVG 原稿的比例, 留白, 漸變和內部陰影, 並支援透過 Icon Studio 重建和調整
* `改善` 外掛程式中心圖示採用統一工作台調整後的尺寸, 位置, 明暗圖稿與圓形底色, 保留可重建原稿和參數
* `改善` Android 系統應用程式資訊圖示與圖示工作台共用圖稿及明暗底色, 保留外掛程式中心透明圖稿與現有啟動器選項

# v1.0.3

###### 2026/09/19

* `修正` AGP 9.1 構建時的 SDK XML v4 解析警告及 JVM 單元測試組裝任務誤觸發 APK 原生程式庫對齊檢查的問題 (共用構建外掛 1.8.3)
* `改善` 將 compileSdk 與 targetSdk 提升到 37 (Android 17), 插件行為不受新目標版本影響

# v1.0.2

###### 2026/09/13

* `修正` 插件中繼資料的建置日期固定使用英文, 不受建置機器語言影響

# v1.0.1

###### 2026/09/13

* `改善` 建置階段校驗 64 位原生程式庫的 16 KB 頁面大小對齊, 檢查 manifest 契約並輸出 JSON 報告
* `改善` 宿主啟用, 外掛中繼資料, 多語言文件與簽章發佈彙整遵循統一外掛規範

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
* `改善` 以 16 KB page alignment 建置原生 library, 驗證 ELF PT_LOAD 與 APK 未壓縮 data alignment, 並在報告 `PAGE_SIZE=16384` 的 API 37 x86_64 `google_apis_ps16k` emulator 上通過全部 3 組 Binder 與 QuickJS instrumentation 測試
* `改善` 在真實 Android 9, 12, 13 和 15 arm64 裝置上覆蓋 runtime 行為, 並提供 JVM test, instrumentation test, Android Lint, source verification 和 APK inventory check
* `依賴` 內嵌採用 MIT 授權的官方 QuickJS 2026-06-04 源碼, 對應 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
