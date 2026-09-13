******

### リリース履歴

******

# v1.0.2

###### 2026/09/13

* `修正` ビルド環境の言語にかかわらずプラグインのビルド日付を英語に統一

# v1.0.1

###### 2026/09/13

* `改善` 64 ビットのネイティブライブラリの 16 KB ページアラインメントをビルド時に検証, manifest 契約の検査と JSON レポートに対応
* `改善` ホストからの有効化, メタデータ, 多言語文書および署名済み APK の収集を共通規約に統一

# v1.0.0

###### 2026/09/02

* `ヒント` 初回 release は 1 個の immutable JavaScript source snapshot を受け取り, AutoJs6 globals, Java bridge, filesystem, network, timer, native addon, multi-file import は提供しません
* `機能` `"quickjs";` directive または `*.quickjs.js` と `*.quickjs.mjs` marker で選択した埋め込み公式 QuickJS 2026-06-04 engine で JavaScript を実行し, Rhino, Node.js, Bun には転送しません
* `機能` Versioned AIDL で protected runtime service を検出し, `ParcelFileDescriptor` で source を転送し, bounded stdout と stderr event を oneway callback で stream します
* `機能` Script と single-file ES module で BigInt, Unicode, Promise job, top-level await, error, timeout, output, memory, stack limit, cancellation, prewarm をサポートします
* `機能` 同じ native engine contract を持つ `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`, `universal` APK を提供します
* `機能` Wake activation, 完全な PluginInfo metadata, 10 言語の instruction, README, changelog を提供します
* `改善` 隔離された `:quickjs_runtime` process で request ごとに新しい `JSRuntime` と `JSContext` を作成し, `quickjs-libc`, filesystem helper, network helper を除外します
* `改善` 公式 byte-exact source を archive URL, archive SHA-256, upstream commit, 各 file size, 各 SHA-256 で固定します
* `改善` Native library を 16 KB alignment で build し, ELF PT_LOAD と uncompressed APK data alignment を検証したうえで, `PAGE_SIZE=16384` の API 37 x86_64 `google_apis_ps16k` emulator における Binder と QuickJS の 3 件の instrumentation suite をすべて成功させます
* `改善` 実際の Android 9, 12, 13, 15 arm64 device で runtime を確認し, JVM test, instrumentation test, Android Lint, source verification, APK inventory check を追加します
* `依存関係` MIT license の公式 QuickJS 2026-06-04 source を commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e) から埋め込みます
