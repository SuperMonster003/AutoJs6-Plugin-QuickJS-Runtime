QuickJS Runtime is a standalone Android plugin that lets AutoJs6 select [QuickJS](https://bellard.org/quickjs/) as an independent JavaScript engine. Every request gets a fresh native runtime and context inside the isolated `:quickjs_runtime` process. Source and events cross a versioned Binder contract, so this is real QuickJS execution and not an alias for Rhino, Node.js, or Bun.

This release embeds the pinned official QuickJS 2026-06-04 source as a native library and creates a fresh runtime for every request. Source is read from an immutable descriptor; stdout, stderr, errors, cancellation, limits, and completion return through the host contract.

### Quick Start

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

The output identifies QuickJS 2026-06-04, prints Unicode text, and evaluates a BigInt expression.

### Compatibility and Scope

- Engine: official QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), with source archive SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- Platform: Android 7.0 (API 24) or later. Single-ABI APKs support `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`; the `universal` APK contains all four.
- Language: JavaScript scripts and single-file ES modules are supported, including BigInt, Unicode, Promise jobs, and module top-level await. TypeScript is not parsed.
- Request bounds: source up to 8 MiB, combined stdout and stderr up to 4 MiB, default timeout 60 seconds, default memory 64 MiB, maximum memory 256 MiB, and default stack 1 MiB.
- Runtime boundary: only bounded `console` and `print` output helpers are added by the plugin. Host automation APIs, Java objects, filesystem and network APIs, `std`, `os`, native addons, timers, and multi-file module graphs are outside version 1.0.

See the [project README](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime) for ABI selection, exact limits, security properties, build verification, and the complete version 1.0 scope.
