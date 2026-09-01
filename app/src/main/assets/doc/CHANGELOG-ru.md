******

### История выпусков

******

# v1.0.0

###### 2026/09/02

* `Подсказка` Первый release принимает один неизменяемый JavaScript source snapshot и не предоставляет AutoJs6 globals, Java bridge, filesystem, network, timers, native addons и многофайловые imports
* `Функция` Запускает JavaScript встроенным официальным движком QuickJS 2026-06-04, выбранным директивой `"quickjs";`, без передачи выполнения Rhino, Node.js или Bun
* `Функция` Обнаруживает защищенный runtime service через versioned AIDL, передает source через `ParcelFileDescriptor` и транслирует ограниченные события stdout и stderr через oneway callbacks
* `Функция` Поддерживает scripts и однофайловые ES modules с BigInt, Unicode, Promise jobs, top-level await, errors, timeout, output, memory и stack limits, cancellation и prewarm
* `Функция` Поставляет APK `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` и `universal` с единым native engine contract
* `Функция` Предоставляет Wake activation, полные metadata PluginInfo, инструкции, README и changelog на 10 языках
* `Улучшение` Создает новые `JSRuntime` и `JSContext` для каждого запроса в изолированном процессе `:quickjs_runtime` и исключает `quickjs-libc`, filesystem helpers и network helpers
* `Улучшение` Фиксирует побайтово точные официальные source files по URL, SHA-256 архива, upstream commit, размеру и SHA-256 каждого файла
* `Улучшение` Собирает native libraries с alignment 16 KB и проверяет ELF PT_LOAD и uncompressed APK data alignment, отмечая отсутствие реального теста с размером страниц 16 KB
* `Улучшение` Проверяет runtime на реальных arm64 устройствах Android 9, 12, 13 и 15, а также JVM tests, instrumentation tests, Android Lint, source verification и APK inventory
* `Зависимость` Встраивает официальные исходники QuickJS 2026-06-04 под MIT из commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
