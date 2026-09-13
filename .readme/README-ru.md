<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>Запускает JavaScript в независимом движке QuickJS в изолированном процессе Android</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Языки

******

Текущий README.md доступен на следующих языках:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- Русский [ru] # текущий
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### Введение

******

QuickJS Runtime является отдельным плагином Android, позволяющим AutoJs6 выбрать [QuickJS](https://bellard.org/quickjs/) как независимый движок JavaScript. Каждый запрос получает новые native runtime и context в изолированном процессе `:quickjs_runtime`. Исходный код и события передаются через версионный контракт Binder, поэтому это настоящий QuickJS, а не псевдоним Rhino, Node.js или Bun.

******

### Установка и использование

******

1. Используйте AutoJs6 build 5279 (6.8.0) или новее на Android 7.0 (API 24) или новее.
2. Установите release APK для ABI устройства. Если ABI неизвестен, используйте более крупный APK `universal`.
3. Откройте центр плагинов AutoJs6 и включите QuickJS Runtime. Если Android оставил новый пакет остановленным, используйте действие host `Активировать`.
4. Выберите QuickJS отдельной директивой `"quickjs";` в начале файла `.js` или `.mjs` либо именем `*.quickjs.js` для режима script или `*.quickjs.mjs` для режима ES module; затем запустите файл обычным способом из AutoJs6.

> Версия 1.0 принимает один неизменяемый снимок исходного кода на запрос. Она не передает дерево проекта и не разрешает относительные импорты, а также намеренно не предоставляет globals AutoJs6, Java bridge, filesystem, network и модули `std` или `os`.

******

### Быстрый старт

******

Запустите этот файл, чтобы подтвердить выбор QuickJS в AutoJs6 и запуск native runtime:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

Вывод указывает QuickJS 2026-06-04, печатает текст Unicode и вычисляет выражение BigInt.

******

### Совместимость и область

******

- Движок: официальный QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), SHA-256 архива исходников `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- Платформа: Android 7.0 (API 24) или новее. APK для одного ABI поддерживают `arm64-v8a`, `armeabi-v7a`, `x86_64` и `x86`; APK `universal` содержит все четыре.
- Язык: поддерживаются JavaScript scripts и однофайловые ES modules, включая BigInt, Unicode, Promise jobs и module top-level await. TypeScript не разбирается.
- Ограничения: исходник до 8 MiB, stdout и stderr вместе до 4 MiB, timeout по умолчанию 60 seconds, memory по умолчанию 64 MiB, максимум memory 256 MiB, stack по умолчанию 1 MiB.
- Граница runtime: плагин добавляет только ограниченные output helpers `console` и `print`. Host automation API, Java objects, filesystem, network API, `std`, `os`, native addons, timers и многофайловые module graphs не входят в версию 1.0.

******

### Разрешения и целостность

******

- Wake Activity и два экспортированных сервиса требуют `org.autojs.permission.PLUGIN`; выполнение идет с UID плагина в отдельном процессе `:quickjs_runtime`.
- Для каждого запроса создаются новые `JSRuntime` и `JSContext`. Ограничения memory, stack, времени, исходника и вывода обязательны, а отмена достигает interrupt handler QuickJS.
- Исходник поступает через read-only `ParcelFileDescriptor`. Вывод использует ограниченные oneway callback chunks, а terminal Bundles содержат только статус и краткую диагностику.
- Репозиторий хранит точные побайтовые файлы закрепленного официального архива и проверяет размеры и SHA-256. `quickjs-libc`, filesystem helpers и network helpers не компилируются.

******

### Интерфейс плагина

******

Стабильные идентификаторы и ограничения для разработчиков host и плагина AutoJs6:

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

`QuickJsRuntimeService` обнаруживается по action `org.autojs.plugin.quickjs.RUNTIME` и category `quickjs`. `runScript` принимает request Bundle и read-only descriptor исходника, передает ограниченные события stdout и stderr через callback и возвращает terminal Bundle со статусом, временем и диагностикой. `cancelScript` безопасен до и во время dispatch, а `prewarmRuntime` проверяет готовность native engine.

Статус 16 KB: native linker настроен на максимальный размер страницы 16 KB, а APK verifier проверяет PT_LOAD alignment каждого ELF и alignment несжатых ZIP данных. Сквозное выполнение проверено на x86_64 emulator `google_apis_ps16k` с API 37 и `PAGE_SIZE=16384`; все три набора instrumentation-тестов Binder и QuickJS успешно пройдены.

******

### История выпусков

******

#### v1.0.1

_2026/09/13_

- `Улучшение` Проверка выравнивания страниц 16 KB для 64-битных нативных библиотек при сборке, включая контракт manifest и отчеты JSON
- `Улучшение` Активация из хоста, метаданные, переведенная документация и сборка подписанных APK приведены к общим правилам

#### v1.0.0

_2026/09/02_

- `Подсказка` Первый release принимает один неизменяемый JavaScript source snapshot и не предоставляет AutoJs6 globals, Java bridge, filesystem, network, timers, native addons и многофайловые imports
- `Функция` Запускает JavaScript встроенным официальным движком QuickJS 2026-06-04, выбранным директивой `"quickjs";` или маркерами `*.quickjs.js` и `*.quickjs.mjs`, без передачи выполнения Rhino, Node.js или Bun
- `Функция` Обнаруживает защищенный runtime service через versioned AIDL, передает source через `ParcelFileDescriptor` и транслирует ограниченные события stdout и stderr через oneway callbacks
- `Функция` Поддерживает scripts и однофайловые ES modules с BigInt, Unicode, Promise jobs, top-level await, errors, timeout, output, memory и stack limits, cancellation и prewarm
- `Функция` Поставляет APK `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` и `universal` с единым native engine contract
- `Функция` Предоставляет Wake activation, полные metadata PluginInfo, инструкции, README и changelog на 10 языках
- `Улучшение` Создает новые `JSRuntime` и `JSContext` для каждого запроса в изолированном процессе `:quickjs_runtime` и исключает `quickjs-libc`, filesystem helpers и network helpers
- `Улучшение` Фиксирует побайтово точные официальные source files по URL, SHA-256 архива, upstream commit, размеру и SHA-256 каждого файла
- `Улучшение` Собирает native libraries с alignment 16 KB, проверяет ELF PT_LOAD и uncompressed APK data alignment и успешно проходит все три набора instrumentation-тестов Binder и QuickJS на x86_64 emulator `google_apis_ps16k` с API 37 и `PAGE_SIZE=16384`
- `Улучшение` Проверяет runtime на реальных arm64 устройствах Android 9, 12, 13 и 15, а также JVM tests, instrumentation tests, Android Lint, source verification и APK inventory
- `Зависимость` Встраивает официальные исходники QuickJS 2026-06-04 под MIT из commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)

##### Дополнительная история выпусков

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-ru.md)

******

### Сборка и проверка

******

Для сборки нужны JDK 17 или новее, Python 3, Android SDK 36, NDK 28.2.13676358 и CMake 3.22.1. Стандартные проверки охватывают закрепленные исходники QuickJS, созданные документы, JVM tests, все APK variants, instrumentation package, Android Lint, ELF alignment и перечень native libraries.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### Локализация и создание документов

******

Измените JSON и Markdown templates, затем выполните `py .python/generate_markdown.py`. Не редактируйте созданные README, changelog и инструкции вручную. Режим `--check` без записи проверяет структуру языков, версию, локализованные ресурсы, лишние файлы и drift.

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

### Лицензия

******

Код плагина использует [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). Встроенные официальные исходники QuickJS используют MIT. Происхождение и уведомления приведены в [Third-Party Notices](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md).

******

### Ссылки

******

- Проект AutoJs6: https://github.com/SuperMonster003/AutoJs6
- Официальный сайт QuickJS: https://bellard.org/quickjs/
- Документация QuickJS: https://bellard.org/quickjs/quickjs.html
- Репозиторий исходников QuickJS: https://github.com/bellard/quickjs
- Сторонние уведомления: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
