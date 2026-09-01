QuickJS Runtime es un complemento Android autónomo que permite a AutoJs6 seleccionar [QuickJS](https://bellard.org/quickjs/) como motor JavaScript independiente. Cada solicitud recibe un runtime y un contexto nativos nuevos dentro del proceso aislado `:quickjs_runtime`. El código y los eventos cruzan un contrato Binder versionado, por lo que se ejecuta QuickJS real y no un alias de Rhino, Node.js o Bun.

Esta versión integra las fuentes oficiales fijadas de QuickJS 2026-06-04 como biblioteca nativa y crea un runtime nuevo para cada solicitud. El código se lee desde un descriptor inmutable; stdout, stderr, errores, cancelación, límites y finalización regresan por el contrato del host.

### Inicio rápido

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

La salida identifica QuickJS 2026-06-04, muestra texto Unicode y evalúa una expresión BigInt.

### Compatibilidad y alcance

- Motor: QuickJS oficial 2026-06-04, revisión `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), con SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a` para el archivo de fuentes.
- Plataforma: Android 7.0 (API 24) o posterior. Los APK de un ABI admiten `arm64-v8a`, `armeabi-v7a`, `x86_64` y `x86`; el APK `universal` contiene los cuatro.
- Lenguaje: se admiten scripts JavaScript y módulos ES de un solo archivo, incluidos BigInt, Unicode, jobs de Promise y top-level await de módulo. TypeScript no se analiza.
- Límites: código de hasta 8 MiB, stdout y stderr combinados hasta 4 MiB, timeout predeterminado 60 seconds, memoria predeterminada 64 MiB, memoria máxima 256 MiB y stack predeterminado 1 MiB.
- Frontera del runtime: el complemento solo añade helpers limitados `console` y `print`. Las API de automatización, objetos Java, sistema de archivos, red, `std`, `os`, addons nativos, timers y grafos de módulos múltiples quedan fuera de la versión 1.0.

Consulta el [README del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime) para elegir ABI, conocer límites exactos, propiedades de seguridad, verificación de compilación y el alcance completo de la versión 1.0.
