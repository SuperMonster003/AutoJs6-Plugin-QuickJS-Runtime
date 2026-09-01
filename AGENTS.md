# AutoJs6 Plugin QuickJS Runtime Repository Guide

This repository contains the standalone QuickJS Runtime plugin for AutoJs6. Keep the implementation, protocol, native source provenance, resources, tests, and documentation aligned with the facts below.

## Repository identity

- Project directory: `AutoJs6-Plugin-QuickJS-Runtime`
- Gradle root name: `autojs6-plugin-quickjs-runtime`
- Application name: `QuickJS Runtime`, always English and non-translatable
- Application ID: `io.github.supermonster003.autojs6.plugin.quickjs.runtime`
- Plugin ID: `quickjs-runtime`
- Engine: `quickjs`
- Variant: `quickjs-2026-06-04`
- Runtime service action: `org.autojs.plugin.quickjs.RUNTIME`
- Runtime service category: `quickjs`
- Capability API: `quickjs-runtime-api`
- Minimum host build: `5279`

Do not add aliases for unpublished identities. Update the AutoJs6 host and the shared contract together if an identity or protocol value changes.

## Session and Git safety

- At the start and end of work, inspect `git status --short`, the current branch, recent commits, and any more specific `AGENTS.md` files.
- Treat pre-existing changes as user work. Never reset, overwrite, or reorganize unrelated changes.
- Do not use destructive Git commands without explicit authorization.
- Finish scoped work with logical Conventional Commits unless the user explicitly requests otherwise.
- Before committing, inspect `git diff --check`, `git diff --cached`, and `git status --short`; never commit signing material, local SDK paths, APKs, caches, or debug output.
- `VERSION_BUILD` must equal `git rev-list --count HEAD`. Set it to the next commit count before every commit.

## QuickJS provenance and native build

- The embedded engine is the official QuickJS `2026-06-04` release at commit `3d5e064e9dd67c70f7962836505a7fa067bf0a4e`.
- The official archive is `https://bellard.org/quickjs/quickjs-2026-06-04.tar.xz`, size `621500`, SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- Vendored QuickJS files remain under their MIT license. Keep the upstream license and a complete `THIRD_PARTY_NOTICES.md` entry.
- Supported ABIs are exactly `arm64-v8a`, `armeabi-v7a`, `x86_64`, and `x86`. Keep ABI splits, Universal APK packaging, `PluginInfo`, tests, and release naming synchronized.
- Keep NDK and linker settings compatible with 16 KB page-size devices. Do not claim real-device 16 KB validation unless it was actually run.
- Upgrades must update the archive lock, vendored sources, variant, shared contract, tests, documentation, changelog, and third-party notices together.

## Execution boundary

- QuickJS is an independent engine, not a Rhino, Node.js, or Bun alias. AutoJs6 routes the standalone `"quickjs";` directive and `.quickjs.js` or `.quickjs.mjs` names to this provider.
- Protocol v1 accepts one immutable JavaScript source snapshot through `ParcelFileDescriptor`; it supports script and ES module evaluation but no multi-file module graph.
- The runtime does not expose Rhino objects, AutoJs6 globals, Android automation APIs, Java reflection, filesystem access, networking, `std`, or `os` modules.
- Output consists only of bounded `console` and `print` events. Terminal Bundles carry status summaries, never the complete output stream.
- Enforce source, name, timeout, memory, stack, output, callback-chunk, and execution-ID limits from `QuickJsRuntimeContract` at every boundary.
- Cancellation and timeouts use the QuickJS interrupt handler. Every execution owns a fresh runtime and context and must release both on every path.
- The separate Android service process limits lifecycle and crash impact but is not a security sandbox. Treat scripts as trusted code under the plugin UID.

## Binder, activation, and metadata

- Use the controlled `common-plugin-api.aar` and the repository `quickjs-runtime-api` module. Do not duplicate `PluginInfo`, AIDL, or contract constants elsewhere.
- Preserve AIDL transaction order; append compatible methods only at the end and negotiate with the protocol version or capabilities.
- Keep `QuickJsRuntimeService` in `:quickjs_runtime`, and protect every exported plugin component with `org.autojs.permission.PLUGIN`.
- Preserve the no-display Wake activity, `org.autojs.plugin.WAKE_ACTIVITY` metadata, `org.autojs.plugin.action.WAKE` action, and default category.
- `PluginInfo` must report installed package version, localized description and instruction, exact identity fields, actually packaged ABIs, required host build, protocol version, runtime version, source transport, execution model, and truthful optional features.

## Gradle, signing, and packaging

- Use `io.github.supermonster003.autojs6-platform-versions` 1.6.0 from online repositories. Do not add `mavenLocal()` or sibling-project paths.
- Read SDK, JDK, version code, and version name through the repository conventions and `version.properties`.
- Keep AIDL and generated resource support enabled. Keep the English application name and stable identity values non-translatable.
- Keep signing resolution intact. `appendDigestToReleasedFiles` must reject missing signing, require exactly four ABI APKs plus Universal, and append CRC32 to stable release names.
- Never commit `sign.properties`, keystores, `local.properties`, build output, APKs, or release artifacts.

## Localization and generated documentation

- Maintain default English plus explicit English, Arabic, Spanish, French, Japanese, Korean, Russian, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese resources.
- Default and explicit English strings must match. Sort names, use ASCII punctuation, keep `plugin_description` without terminal punctuation, and place non-translatable strings outside locale files.
- Treat `.readme/lang_*.json`, `.readme/common.json`, `.changelog/lang_*.json`, and templates as sources of truth. Do not hand-edit generated README, instruction, or changelog files.
- After documentation changes, run the generator, its check mode, and its Python unit tests.

## Tests and verification

- Unit tests cover metadata mapping, request validation, Unicode, limits, source mode, capability negotiation, output accounting, archive provenance, and generator behavior.
- Instrumentation covers discovery, explicit binding and descriptor, Wake and permissions, `getInfo`, runtime prewarm, real JavaScript execution, modules, stdout and stderr, exceptions, invalid requests, output limits, timeout, cancellation, repeated binding, and process restart.
- Verify each APK contains only its expected ABI library and that `PluginInfo.supportedAbis` matches it.
- Standard checks are:

```powershell
py .python/generate_markdown.py --check
py -B -m unittest discover -s .python -p "test_*.py"
py -B -m unittest discover -s tools/quickjs/tests -p "test_*.py"
.\gradlew.bat :plugin-api:quickjs-runtime-api:testDebugUnitTest :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
.\gradlew.bat :app:lintDebug
```

Run instrumentation on available devices or emulators. Report device, API, ABI, page size, and exact scope for every real-device claim; explicitly list checks that were not run.
