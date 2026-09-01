QuickJS Runtime 是一個獨立 Android 插件, 讓 AutoJs6 可選擇 [QuickJS](https://bellard.org/quickjs/) 作為獨立 JavaScript 引擎. 每個請求都會在隔離的 `:quickjs_runtime` 程序中取得全新的原生 runtime 和 context. 源碼與事件經版本化 Binder 契約傳輸, 因此這是真正的 QuickJS 執行, 並非 Rhino, Node.js 或 Bun 的別名.

此版本把固定的官方 QuickJS 2026-06-04 源碼嵌入原生 library, 並為每個請求建立全新 runtime. 源碼從不可變 descriptor 讀取; stdout, stderr, error, cancellation, limit 和 completion 經宿主契約返回.

### 快速開始

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

輸出會標識 QuickJS 2026-06-04, 顯示 Unicode 文字並計算 BigInt 表達式.

### 兼容性和範圍

- 引擎: 官方 QuickJS 2026-06-04, 修訂版 `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), 源碼封存 SHA-256 為 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 平台: Android 7.0 (API 24) 或更高版本. 單 ABI APK 支援 `arm64-v8a`, `armeabi-v7a`, `x86_64` 和 `x86`; `universal` APK 包含全部四種 ABI.
- 語言: 支援 JavaScript script 和單檔 ES module, 包括 BigInt, Unicode, Promise job 和 module top-level await. 不解析 TypeScript.
- 請求限制: 源碼最大 8 MiB, stdout 與 stderr 合計最大 4 MiB, 預設 timeout 60 seconds, 預設記憶體 64 MiB, 最大記憶體 256 MiB, 預設 stack 1 MiB.
- Runtime 邊界: 插件只加入有界的 `console` 和 `print` 輸出 helper. 宿主自動化 API, Java 物件, 檔案系統和網絡 API, `std`, `os`, 原生 addon, timer 及多檔 module graph 不屬於 1.0 版本範圍.

ABI 選擇, 精確限制, 安全屬性, 建置驗證和完整 1.0 版本範圍請參閱 [專案 README](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime).
