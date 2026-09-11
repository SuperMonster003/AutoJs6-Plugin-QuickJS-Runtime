<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>独立した Android プロセスで独立 QuickJS エンジンを使用して JavaScript を実行</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語

******

現在の README.md は次の言語に対応しています:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### 概要

******

QuickJS Runtime は AutoJs6 が [QuickJS](https://bellard.org/quickjs/) を独立 JavaScript エンジンとして選択できる Android プラグインです. 各リクエストは隔離された `:quickjs_runtime` プロセス内で新しい native runtime と context を取得します. ソースとイベントは versioned Binder contract を通るため, Rhino, Node.js, Bun の alias ではなく実際の QuickJS が実行されます.

******

### インストールと使用

******

1. Android 7.0 (API 24) 以降で AutoJs6 build 5279 (6.8.0) 以降を使用します.
2. 端末 ABI に対応する release APK をインストールします. ABI が不明な場合は容量の大きい `universal` APK を使用します.
3. AutoJs6 プラグインセンターを開いて QuickJS Runtime を有効にします. Android が新規 package を停止状態にした場合は host の `有効化` 操作を使用します.
4. `.js` または `.mjs` ファイルの先頭に単独 directive `"quickjs";` を置くか, script mode は `*.quickjs.js`, ES module mode は `*.quickjs.mjs` と命名し, AutoJs6 から通常どおり実行します.

> バージョン 1.0 はリクエストごとに 1 個の immutable source snapshot を受け取ります. project tree の転送や relative file import の解決は行わず, AutoJs6 globals, Java bridge, filesystem, network, `std`, `os` module も公開しません.

******

### クイックスタート

******

このファイルを実行して AutoJs6 が QuickJS を選択し native runtime を開始したことを確認します:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

出力は QuickJS 2026-06-04 を示し, Unicode テキストを表示して BigInt 式を評価します.

******

### 互換性と範囲

******

- エンジン: 公式 QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), source archive SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- プラットフォーム: Android 7.0 (API 24) 以降. single-ABI APK は `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` に対応し, `universal` APK は 4 種すべてを含みます.
- 言語: JavaScript script と single-file ES module に対応し, BigInt, Unicode, Promise job, module top-level await を利用できます. TypeScript は parse しません.
- 制限: source は 8 MiB まで, stdout と stderr 合計は 4 MiB まで, default timeout は 60 seconds, default memory は 64 MiB, maximum memory は 256 MiB, default stack は 1 MiB です.
- Runtime 境界: plugin が追加するのは bounded `console` と `print` output helper だけです. Host automation API, Java object, filesystem, network API, `std`, `os`, native addon, timer, multi-file module graph はバージョン 1.0 の範囲外です.

******

### 権限と整合性

******

- Wake Activity と 2 個の exported service は `org.autojs.permission.PLUGIN` を必要とします. 実行は plugin UID を使い, 別の `:quickjs_runtime` process で行われます.
- 各リクエストに新しい `JSRuntime` と `JSContext` を作成します. Memory, stack, time, source, output の制限を適用し, cancellation は QuickJS interrupt handler に届きます.
- Source は read-only `ParcelFileDescriptor` から入ります. Output は bounded oneway callback chunk を使い, terminal Bundle は status と diagnostic summary のみを持ちます.
- Repository は固定した公式 archive の byte-exact file を保持し, size と SHA-256 を検証します. `quickjs-libc`, filesystem helper, network helper は engine に compile されません.

******

### プラグインインターフェイス

******

AutoJs6 host と plugin developer 向けの安定 identifier と limit:

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

`QuickJsRuntimeService` は action `org.autojs.plugin.quickjs.RUNTIME` と category `quickjs` で検出されます. `runScript` は request Bundle と read-only source descriptor を受け取り, bounded stdout と stderr event を callback で stream し, status, timing, diagnostic を含む terminal Bundle を返します. `cancelScript` は dispatch の前後で安全に呼べ, `prewarmRuntime` は native engine readiness を確認します.

16 KB 状態: native linker は 16 KB maximum page size に設定され, APK verifier は各 ELF PT_LOAD alignment と uncompressed ZIP data alignment を確認します. `PAGE_SIZE=16384` を報告する API 37 x86_64 `google_apis_ps16k` emulator で end-to-end 実行を検証し, Binder と QuickJS の 3 件の instrumentation suite がすべて成功しました.

******

### リリース履歴

******

#### v1.0.1

_2026/09/11_

- `改善` 64 ビットのネイティブライブラリの 16 KB ページアラインメントをビルド時に検証, manifest 契約の検査と JSON レポートに対応

#### v1.0.0

_2026/09/02_

- `ヒント` 初回 release は 1 個の immutable JavaScript source snapshot を受け取り, AutoJs6 globals, Java bridge, filesystem, network, timer, native addon, multi-file import は提供しません
- `機能` `"quickjs";` directive または `*.quickjs.js` と `*.quickjs.mjs` marker で選択した埋め込み公式 QuickJS 2026-06-04 engine で JavaScript を実行し, Rhino, Node.js, Bun には転送しません
- `機能` Versioned AIDL で protected runtime service を検出し, `ParcelFileDescriptor` で source を転送し, bounded stdout と stderr event を oneway callback で stream します
- `機能` Script と single-file ES module で BigInt, Unicode, Promise job, top-level await, error, timeout, output, memory, stack limit, cancellation, prewarm をサポートします
- `機能` 同じ native engine contract を持つ `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`, `universal` APK を提供します
- `機能` Wake activation, 完全な PluginInfo metadata, 10 言語の instruction, README, changelog を提供します
- `改善` 隔離された `:quickjs_runtime` process で request ごとに新しい `JSRuntime` と `JSContext` を作成し, `quickjs-libc`, filesystem helper, network helper を除外します
- `改善` 公式 byte-exact source を archive URL, archive SHA-256, upstream commit, 各 file size, 各 SHA-256 で固定します
- `改善` Native library を 16 KB alignment で build し, ELF PT_LOAD と uncompressed APK data alignment を検証したうえで, `PAGE_SIZE=16384` の API 37 x86_64 `google_apis_ps16k` emulator における Binder と QuickJS の 3 件の instrumentation suite をすべて成功させます
- `改善` 実際の Android 9, 12, 13, 15 arm64 device で runtime を確認し, JVM test, instrumentation test, Android Lint, source verification, APK inventory check を追加します
- `依存関係` MIT license の公式 QuickJS 2026-06-04 source を commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e) から埋め込みます

##### その他のリリース履歴

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルドと検証

******

ビルドには JDK 17 以降, Python 3, Android SDK 36, NDK 28.2.13676358, CMake 3.22.1 が必要です. 標準 check は固定 QuickJS source, generated docs, JVM test, 全 APK variant, instrumentation package, Android Lint, ELF alignment, native library inventory を検証します.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### ローカライズと文書生成

******

JSON と Markdown template を編集して `py .python/generate_markdown.py` を実行します. Generated README, changelog, plugin instruction を手動編集しないでください. `--check` は書き込みなしで language shape, version alignment, localized resource, orphan file, drift を検証します.

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

### ライセンス

******

Plugin code は [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE) です. 埋め込まれた公式 QuickJS source は MIT です. Provenance と notice は [Third-Party Notices](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md) を参照してください.

******

### リンク

******

- AutoJs6 プロジェクト: https://github.com/SuperMonster003/AutoJs6
- QuickJS 公式サイト: https://bellard.org/quickjs/
- QuickJS ドキュメント: https://bellard.org/quickjs/quickjs.html
- QuickJS ソースリポジトリ: https://github.com/bellard/quickjs
- サードパーティ通知: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
