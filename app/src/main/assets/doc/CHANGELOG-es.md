******

### Historial de versiones

******

# v1.0.1

###### 2026/09/11

* `Mejora` Verificación de compilación de la alineación de páginas de 16 KB en bibliotecas nativas de 64 bits, con controles del contrato manifest e informes JSON

# v1.0.0

###### 2026/09/02

* `Aviso` La primera versión acepta una instantánea inmutable de JavaScript y no ofrece globales AutoJs6, puente Java, sistema de archivos, red, timers, addons nativos ni imports de varios archivos
* `Función` Ejecuta JavaScript con el motor oficial integrado QuickJS 2026-06-04 elegido por la directiva `"quickjs";` o los marcadores `*.quickjs.js` y `*.quickjs.mjs`, sin reenviar la ejecución a Rhino, Node.js o Bun
* `Función` Descubre un runtime service protegido mediante AIDL versionado, transfiere el código con `ParcelFileDescriptor` y transmite eventos stdout y stderr limitados por callbacks oneway
* `Función` Admite scripts y módulos ES de un archivo con BigInt, Unicode, jobs Promise, top-level await, errores, timeout, límites de salida, memoria y stack, cancelación y precalentamiento
* `Función` Publica APK `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` y `universal` con el mismo contrato de motor nativo
* `Función` Incluye activación Wake, metadatos PluginInfo completos e instrucciones, README y changelog en 10 idiomas
* `Mejora` Crea `JSRuntime` y `JSContext` nuevos por solicitud en el proceso aislado `:quickjs_runtime` y omite `quickjs-libc` y helpers de archivos y red
* `Mejora` Fija fuentes oficiales idénticas byte a byte por URL, SHA-256 del archivo, commit upstream, tamaño y SHA-256 de cada archivo
* `Mejora` Compila bibliotecas nativas con alineación de 16 KB, verifica ELF PT_LOAD y datos APK sin comprimir, y supera las tres suites de instrumentación de Binder y QuickJS en un emulador x86_64 `google_apis_ps16k` con API 37 y `PAGE_SIZE=16384`
* `Mejora` Cubre el runtime en dispositivos arm64 reales Android 9, 12, 13 y 15, además de pruebas JVM e instrumentación, Android Lint, verificación de fuentes e inventario APK
* `Dependencia` Integra las fuentes oficiales QuickJS 2026-06-04 con licencia MIT del commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
