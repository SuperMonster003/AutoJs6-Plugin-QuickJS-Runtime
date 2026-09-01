QuickJS Runtime は AutoJs6 が [QuickJS](https://bellard.org/quickjs/) を独立 JavaScript エンジンとして選択できる Android プラグインです. 各リクエストは隔離された `:quickjs_runtime` プロセス内で新しい native runtime と context を取得します. ソースとイベントは versioned Binder contract を通るため, Rhino, Node.js, Bun の alias ではなく実際の QuickJS が実行されます.

この release は固定した公式 QuickJS 2026-06-04 source を native library に埋め込み, リクエストごとに新しい runtime を作成します. Source は immutable descriptor から読み, stdout, stderr, error, cancellation, limit, completion は host contract を通して返ります.

### クイックスタート

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

出力は QuickJS 2026-06-04 を示し, Unicode テキストを表示して BigInt 式を評価します.

### 互換性と範囲

- エンジン: 公式 QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), source archive SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- プラットフォーム: Android 7.0 (API 24) 以降. single-ABI APK は `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` に対応し, `universal` APK は 4 種すべてを含みます.
- 言語: JavaScript script と single-file ES module に対応し, BigInt, Unicode, Promise job, module top-level await を利用できます. TypeScript は parse しません.
- 制限: source は 8 MiB まで, stdout と stderr 合計は 4 MiB まで, default timeout は 60 seconds, default memory は 64 MiB, maximum memory は 256 MiB, default stack は 1 MiB です.
- Runtime 境界: plugin が追加するのは bounded `console` と `print` output helper だけです. Host automation API, Java object, filesystem, network API, `std`, `os`, native addon, timer, multi-file module graph はバージョン 1.0 の範囲外です.

ABI 選択, 正確な制限, security property, build verification, バージョン 1.0 の全範囲は [project README](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime) を参照してください.
