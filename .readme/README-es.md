<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>Ejecuta JavaScript con un motor QuickJS independiente en un proceso Android aislado</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas

******

El README.md actual admite los siguientes idiomas:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- Español [es] # actual
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### Introducción

******

QuickJS Runtime es un complemento Android autónomo que permite a AutoJs6 seleccionar [QuickJS](https://bellard.org/quickjs/) como motor JavaScript independiente. Cada solicitud recibe un runtime y un contexto nativos nuevos dentro del proceso aislado `:quickjs_runtime`. El código y los eventos cruzan un contrato Binder versionado, por lo que se ejecuta QuickJS real y no un alias de Rhino, Node.js o Bun.

******

### Instalación y uso

******

1. Usa AutoJs6 build 5279 (6.8.0) o posterior en Android 7.0 (API 24) o posterior.
2. Instala el APK de la versión que coincida con el ABI del dispositivo, o usa el APK `universal` más grande si no conoces el ABI.
3. Abre el centro de complementos de AutoJs6 y habilita QuickJS Runtime. Si Android dejó detenido el paquete recién instalado, usa la acción `Activar` del host.
4. Selecciona QuickJS con la directiva independiente `"quickjs";` al principio de un archivo `.js` o `.mjs`, o nómbralo `*.quickjs.js` para modo script o `*.quickjs.mjs` para modo ES module; después ejecútalo normalmente desde AutoJs6.

> La versión 1.0 acepta una sola instantánea inmutable de código por solicitud. No transfiere un árbol de proyecto ni resuelve imports relativos, y no expone globales de AutoJs6, puente Java, sistema de archivos, red ni módulos `std` u `os`.

******

### Inicio rápido

******

Ejecuta este archivo para confirmar que AutoJs6 seleccionó QuickJS e inició el runtime nativo:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

La salida identifica QuickJS 2026-06-04, muestra texto Unicode y evalúa una expresión BigInt.

******

### Compatibilidad y alcance

******

- Motor: QuickJS oficial 2026-06-04, revisión `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), con SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a` para el archivo de fuentes.
- Plataforma: Android 7.0 (API 24) o posterior. Los APK de un ABI admiten `arm64-v8a`, `armeabi-v7a`, `x86_64` y `x86`; el APK `universal` contiene los cuatro.
- Lenguaje: se admiten scripts JavaScript y módulos ES de un solo archivo, incluidos BigInt, Unicode, jobs de Promise y top-level await de módulo. TypeScript no se analiza.
- Límites: código de hasta 8 MiB, stdout y stderr combinados hasta 4 MiB, timeout predeterminado 60 seconds, memoria predeterminada 64 MiB, memoria máxima 256 MiB y stack predeterminado 1 MiB.
- Frontera del runtime: el complemento solo añade helpers limitados `console` y `print`. Las API de automatización, objetos Java, sistema de archivos, red, `std`, `os`, addons nativos, timers y grafos de módulos múltiples quedan fuera de la versión 1.0.

******

### Permisos e integridad

******

- La actividad Wake y los dos servicios exportados requieren `org.autojs.permission.PLUGIN`; la ejecución usa el UID del complemento en el proceso separado `:quickjs_runtime`.
- Cada solicitud recibe un `JSRuntime` y un `JSContext` nuevos. Se aplican límites de memoria, stack, tiempo, código y salida, y la cancelación llega al interrupt handler de QuickJS.
- El código llega por un `ParcelFileDescriptor` de solo lectura. La salida usa fragmentos callback oneway limitados y los Bundles terminales solo incluyen estado y resúmenes de diagnóstico.
- El repositorio conserva archivos idénticos byte a byte del archivo oficial fijado y verifica tamaños y SHA-256. El motor no compila `quickjs-libc` ni helpers de archivos o red.

******

### Interfaz del complemento

******

Identificadores y límites estables para desarrolladores del host y del complemento AutoJs6:

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

`QuickJsRuntimeService` se descubre mediante la acción `org.autojs.plugin.quickjs.RUNTIME` y la categoría `quickjs`. `runScript` recibe un Bundle de solicitud y un descriptor de código de solo lectura, transmite eventos stdout y stderr limitados por callback y devuelve un Bundle terminal con estado, tiempos y diagnósticos. `cancelScript` es seguro antes o durante el despacho y `prewarmRuntime` comprueba que el motor nativo esté listo.

Estado de 16 KB: el linker nativo usa un tamaño máximo de página de 16 KB y el verificador de APK comprueba la alineación PT_LOAD de cada ELF y de datos ZIP sin comprimir. La ejecución integral se validó en un emulador x86_64 `google_apis_ps16k` con API 37 que informó `PAGE_SIZE=16384`; las tres suites de instrumentación de Binder y QuickJS pasaron.

******

### Historial de versiones

******

#### v1.0.3

_2026/09/15_

- `Mejora` compileSdk y targetSdk suben a 37 (Android 17); el comportamiento del plugin no depende del nuevo objetivo

#### v1.0.2

_2026/09/13_

- `Corrección` Usar fechas de compilación en inglés independientemente del idioma de la máquina

#### v1.0.1

_2026/09/13_

- `Mejora` Verificación de compilación de la alineación de páginas de 16 KB en bibliotecas nativas de 64 bits, con controles del contrato manifest e informes JSON
- `Mejora` Activación del host, metadatos, documentación traducida y recopilación de APK firmados conforme a las convenciones comunes

##### Más historial de versiones

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación y verificación

******

La compilación requiere JDK 17 o posterior, Python 3, Android SDK 37, NDK 28.2.13676358 y CMake 3.22.1. Las comprobaciones validan las fuentes QuickJS fijadas, documentación generada, pruebas JVM, todos los APK, paquete de instrumentación, Android Lint, alineación ELF e inventario de bibliotecas nativas.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### Localización y generación de documentos

******

Edita los JSON y plantillas Markdown y ejecuta `py .python/generate_markdown.py`. No edites manualmente los README, changelog ni instrucciones generados. `--check` valida estructura de idiomas, versión, recursos localizados, archivos huérfanos y diferencias sin escribir.

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

### Licencia

******

El código del complemento usa [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). Las fuentes oficiales QuickJS integradas usan MIT. Consulta los [Avisos de terceros](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md) para procedencia y avisos incluidos.

******

### Enlaces

******

- Proyecto AutoJs6: https://github.com/SuperMonster003/AutoJs6
- Sitio oficial de QuickJS: https://bellard.org/quickjs/
- Documentación de QuickJS: https://bellard.org/quickjs/quickjs.html
- Repositorio fuente de QuickJS: https://github.com/bellard/quickjs
- Avisos de terceros: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
