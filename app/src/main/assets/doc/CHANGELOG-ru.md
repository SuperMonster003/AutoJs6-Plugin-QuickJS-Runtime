******

### История выпусков

******

# v1.0.4

###### 2026/10/04

* `Улучшение` Значки приложения и центра плагинов используют цветную молнию с прозрачным фоном, сохраняя пропорции, отступы, градиенты и внутренние тени SVG, с воспроизводимой генерацией и настройкой в Icon Studio
* `Улучшение` Значки центра плагинов используют размеры, положение, светлые и тёмные изображения и круглые фоны, настроенные в Icon Studio, сохраняя исходники и параметры для воспроизведения

# v1.0.3

###### 2026/09/19

* `Исправление` Предупреждения чтения SDK XML v4 с AGP 9.1 и ошибочный запуск проверки выравнивания нативных библиотек APK при сборке модульных тестов JVM, устраненные общими плагинами сборки 1.8.3
* `Улучшение` Подняты compileSdk и targetSdk до 37 (Android 17); поведение плагина не зависит от нового целевого уровня

# v1.0.2

###### 2026/09/13

* `Исправление` Дата сборки в метаданных всегда записывается на английском независимо от языка системы

# v1.0.1

###### 2026/09/13

* `Улучшение` Проверка выравнивания страниц 16 KB для 64-битных нативных библиотек при сборке, включая контракт manifest и отчеты JSON
* `Улучшение` Активация из хоста, метаданные, переведенная документация и сборка подписанных APK приведены к общим правилам

# v1.0.0

###### 2026/09/02

* `Подсказка` Первый release принимает один неизменяемый JavaScript source snapshot и не предоставляет AutoJs6 globals, Java bridge, filesystem, network, timers, native addons и многофайловые imports
* `Функция` Запускает JavaScript встроенным официальным движком QuickJS 2026-06-04, выбранным директивой `"quickjs";` или маркерами `*.quickjs.js` и `*.quickjs.mjs`, без передачи выполнения Rhino, Node.js или Bun
* `Функция` Обнаруживает защищенный runtime service через versioned AIDL, передает source через `ParcelFileDescriptor` и транслирует ограниченные события stdout и stderr через oneway callbacks
* `Функция` Поддерживает scripts и однофайловые ES modules с BigInt, Unicode, Promise jobs, top-level await, errors, timeout, output, memory и stack limits, cancellation и prewarm
* `Функция` Поставляет APK `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` и `universal` с единым native engine contract
* `Функция` Предоставляет Wake activation, полные metadata PluginInfo, инструкции, README и changelog на 10 языках
* `Улучшение` Создает новые `JSRuntime` и `JSContext` для каждого запроса в изолированном процессе `:quickjs_runtime` и исключает `quickjs-libc`, filesystem helpers и network helpers
* `Улучшение` Фиксирует побайтово точные официальные source files по URL, SHA-256 архива, upstream commit, размеру и SHA-256 каждого файла
* `Улучшение` Собирает native libraries с alignment 16 KB, проверяет ELF PT_LOAD и uncompressed APK data alignment и успешно проходит все три набора instrumentation-тестов Binder и QuickJS на x86_64 emulator `google_apis_ps16k` с API 37 и `PAGE_SIZE=16384`
* `Улучшение` Проверяет runtime на реальных arm64 устройствах Android 9, 12, 13 и 15, а также JVM tests, instrumentation tests, Android Lint, source verification и APK inventory
* `Зависимость` Встраивает официальные исходники QuickJS 2026-06-04 под MIT из commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
