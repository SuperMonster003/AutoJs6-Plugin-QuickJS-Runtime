QuickJS Runtime является отдельным плагином Android, позволяющим AutoJs6 выбрать [QuickJS](https://bellard.org/quickjs/) как независимый движок JavaScript. Каждый запрос получает новые native runtime и context в изолированном процессе `:quickjs_runtime`. Исходный код и события передаются через версионный контракт Binder, поэтому это настоящий QuickJS, а не псевдоним Rhino, Node.js или Bun.

Этот release включает закрепленные официальные исходники QuickJS 2026-06-04 как native library и создает новый runtime для каждого запроса. Source читается из immutable descriptor; stdout, stderr, errors, cancellation, limits и completion возвращаются через host contract.

### Быстрый старт

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

Вывод указывает QuickJS 2026-06-04, печатает текст Unicode и вычисляет выражение BigInt.

### Совместимость и область

- Движок: официальный QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), SHA-256 архива исходников `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- Платформа: Android 7.0 (API 24) или новее. APK для одного ABI поддерживают `arm64-v8a`, `armeabi-v7a`, `x86_64` и `x86`; APK `universal` содержит все четыре.
- Язык: поддерживаются JavaScript scripts и однофайловые ES modules, включая BigInt, Unicode, Promise jobs и module top-level await. TypeScript не разбирается.
- Ограничения: исходник до 8 MiB, stdout и stderr вместе до 4 MiB, timeout по умолчанию 60 seconds, memory по умолчанию 64 MiB, максимум memory 256 MiB, stack по умолчанию 1 MiB.
- Граница runtime: плагин добавляет только ограниченные output helpers `console` и `print`. Host automation API, Java objects, filesystem, network API, `std`, `os`, native addons, timers и многофайловые module graphs не входят в версию 1.0.

Выбор ABI, точные ограничения, свойства безопасности, проверка сборки и полная область версии 1.0 описаны в [README проекта](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime).
