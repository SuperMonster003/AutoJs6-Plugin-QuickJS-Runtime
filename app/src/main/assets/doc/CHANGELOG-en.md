******

### Release History

******

# v1.0.3

###### 2026/09/15

* `Improvement` Raise compileSdk and targetSdk to 37 (Android 17); the plugin's behavior does not depend on the new target

# v1.0.2

###### 2026/09/13

* `Fix` Use English build dates in plugin metadata regardless of the build machine locale

# v1.0.1

###### 2026/09/13

* `Improvement` Build verification of 16 KB page alignment for 64-bit native libraries, including manifest contract checks and JSON reports
* `Improvement` Host activation, plugin metadata, localized documentation and signed release collection follow the common plugin conventions

# v1.0.0

###### 2026/09/02

* `Hint` The first release accepts one immutable JavaScript source snapshot and deliberately provides no AutoJs6 globals, Java bridge, filesystem, network, timers, native addons, or multi-file imports
* `Feature` Run JavaScript through the embedded official QuickJS 2026-06-04 engine selected by the `"quickjs";` directive or `*.quickjs.js` and `*.quickjs.mjs` markers, without forwarding execution to Rhino, Node.js, or Bun
* `Feature` Discover a protected runtime service through a versioned AIDL contract, transfer source with `ParcelFileDescriptor`, and stream bounded stdout and stderr events through oneway callbacks
* `Feature` Support scripts and single-file ES modules with BigInt, Unicode, Promise jobs, module top-level await, errors, timeout, output limits, memory and stack limits, cancellation, and prewarming
* `Feature` Ship `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`, and `universal` APKs with the same native engine contract
* `Feature` Provide Wake activation, complete PluginInfo metadata, plugin-center instructions, README, and changelog content in 10 languages
* `Improvement` Create a fresh `JSRuntime` and `JSContext` per request in the isolated `:quickjs_runtime` process and omit `quickjs-libc` plus filesystem and network helpers
* `Improvement` Pin byte-exact official source files by archive URL, archive SHA-256, upstream commit, individual file sizes, and individual SHA-256 digests
* `Improvement` Build native libraries with 16 KB page alignment, verify ELF PT_LOAD and uncompressed APK data alignment, and pass all three Binder and QuickJS instrumentation suites on an API 37 x86_64 `google_apis_ps16k` emulator reporting `PAGE_SIZE=16384`
* `Improvement` Cover runtime behavior on real Android 9, 12, 13, and 15 arm64 devices, with JVM tests, instrumentation tests, Android Lint, source verification, and APK inventory checks
* `Dependency` Embed the MIT-licensed official QuickJS 2026-06-04 source at commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
