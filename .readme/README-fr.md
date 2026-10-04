<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap-night/ic_launcher.png" />
      <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
    </picture>
  </p>

  <p>Exécute JavaScript avec un moteur QuickJS indépendant dans un processus Android isolé</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues

******

Le README.md actuel prend en charge les langues suivantes:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- Français [fr] # actuel
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### Introduction

******

QuickJS Runtime est un plugin Android autonome qui permet à AutoJs6 de choisir [QuickJS](https://bellard.org/quickjs/) comme moteur JavaScript indépendant. Chaque requête reçoit un runtime et un contexte natifs neufs dans le processus isolé `:quickjs_runtime`. Le code et les événements traversent un contrat Binder versionné: il s'agit donc du véritable QuickJS, et non d'un alias de Rhino, Node.js ou Bun.

******

### Installation et utilisation

******

1. Utilisez AutoJs6 build 5279 (6.8.0) ou ultérieur sur Android 7.0 (API 24) ou ultérieur.
2. Installez l'APK de publication correspondant à l'ABI de l'appareil, ou le plus grand APK `universal` si l'ABI est inconnue.
3. Ouvrez le centre de plugins AutoJs6 et activez QuickJS Runtime. Si Android a laissé le nouveau paquet arrêté, utilisez l'action `Activer` proposée par l'hôte.
4. Sélectionnez QuickJS avec la directive autonome `"quickjs";` au début d'un fichier `.js` ou `.mjs`, ou nommez-le `*.quickjs.js` pour le mode script ou `*.quickjs.mjs` pour le mode ES module; exécutez-le ensuite normalement depuis AutoJs6.

> La version 1.0 accepte un seul instantané de source immuable par requête. Elle ne transfère pas d'arborescence de projet ni ne résout les imports relatifs, et n'expose volontairement ni globales AutoJs6, pont Java, système de fichiers, réseau, ni modules `std` ou `os`.

******

### Démarrage rapide

******

Exécutez ce fichier pour confirmer qu'AutoJs6 a choisi QuickJS et lancé le runtime natif:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

La sortie identifie QuickJS 2026-06-04, affiche du texte Unicode et évalue une expression BigInt.

******

### Compatibilité et portée

******

- Moteur: QuickJS officiel 2026-06-04, révision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), avec SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a` pour l'archive source.
- Plateforme: Android 7.0 (API 24) ou ultérieur. Les APK mono-ABI prennent en charge `arm64-v8a`, `armeabi-v7a`, `x86_64` et `x86`; l'APK `universal` contient les quatre.
- Langage: scripts JavaScript et modules ES mono-fichier, avec BigInt, Unicode, jobs Promise et top-level await de module. TypeScript n'est pas analysé.
- Limites: source jusqu'à 8 MiB, stdout et stderr combinés jusqu'à 4 MiB, délai par défaut 60 seconds, mémoire par défaut 64 MiB, mémoire maximale 256 MiB et stack par défaut 1 MiB.
- Frontière du runtime: le plugin ajoute seulement les helpers de sortie bornés `console` et `print`. API d'automatisation, objets Java, fichiers, réseau, `std`, `os`, addons natifs, timers et graphes multi-fichiers sont hors de la version 1.0.

******

### Permissions et intégrité

******

- L'activité Wake et les deux services exportés exigent `org.autojs.permission.PLUGIN`; l'exécution utilise l'UID du plugin dans le processus séparé `:quickjs_runtime`.
- Chaque requête reçoit un nouveau `JSRuntime` et `JSContext`. Les limites de mémoire, stack, temps, source et sortie sont appliquées, et l'annulation atteint l'interrupt handler QuickJS.
- La source arrive par un `ParcelFileDescriptor` en lecture seule. La sortie utilise des fragments callback oneway bornés et les Bundles terminaux ne contiennent que l'état et des résumés de diagnostic.
- Le dépôt conserve les fichiers identiques octet par octet de l'archive officielle fixée et vérifie tailles et SHA-256. `quickjs-libc` et les helpers de fichiers ou réseau ne sont pas compilés.

******

### Interface du plugin

******

Identifiants et limites stables pour les développeurs de l'hôte et du plugin AutoJs6:

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

`QuickJsRuntimeService` est découvert par l'action `org.autojs.plugin.quickjs.RUNTIME` et la catégorie `quickjs`. `runScript` reçoit un Bundle de requête et un descripteur source en lecture seule, diffuse des événements stdout et stderr bornés via callback et renvoie un Bundle terminal avec état, durée et diagnostics. `cancelScript` est sûr avant ou pendant l'envoi et `prewarmRuntime` vérifie la disponibilité du moteur natif.

État 16 KB: le linker natif est configuré pour une taille de page maximale de 16 KB et le vérificateur APK contrôle l'alignement PT_LOAD de chaque ELF ainsi que les données ZIP non compressées. L'exécution de bout en bout a été validée sur un émulateur x86_64 `google_apis_ps16k` API 37 indiquant `PAGE_SIZE=16384`; les trois suites d'instrumentation Binder et QuickJS ont réussi.

******

### Historique des versions

******

#### v1.0.4

_2026/10/04_

- `Amélioration` Les icônes de l'application et du centre de plugins utilisent un éclair coloré sur fond transparent, en conservant les proportions, marges, dégradés et ombres internes du SVG, avec une génération reproductible dans Icon Studio

#### v1.0.3

_2026/09/19_

- `Correction` Avertissements de lecture SDK XML v4 avec AGP 9.1 et contrôles d'alignement natif des APK déclenchés par erreur lors de l'assemblage des tests unitaires JVM, avec les plugins de compilation partagés 1.8.3
- `Amélioration` compileSdk et targetSdk passent à 37 (Android 17) ; le comportement du plugin ne dépend pas de la nouvelle cible

#### v1.0.2

_2026/09/13_

- `Correction` Utiliser des dates de compilation en anglais indépendamment de la langue de la machine

##### Historique supplémentaire

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation et vérification

******

La compilation exige JDK 17 ou ultérieur, Python 3, Android SDK 37, NDK 28.2.13676358 et CMake 3.22.1. Les contrôles vérifient les sources QuickJS fixées, la documentation générée, les tests JVM, tous les APK, le paquet d'instrumentation, Android Lint, l'alignement ELF et l'inventaire natif.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### Localisation et génération des documents

******

Modifiez les JSON et modèles Markdown, puis lancez `py .python/generate_markdown.py`. Ne modifiez pas manuellement README, changelog ou instructions générés. `--check` valide la forme des langues, la version, les ressources localisées, les fichiers orphelins et les écarts sans écrire.

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

L'icône conserve le [SVG fourni par le responsable du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.python/icons/quickjs-runtime-lightning.svg) et sa composition d'origine. Ajustez-la dans Icon Studio et vérifiez la recette et les ressources générées avec `python .python/generate_icon_studio.py --check`.

******

### Licence

******

Le code du plugin utilise la [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). Les sources officielles QuickJS intégrées utilisent MIT. Consultez les [Avis tiers](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md) pour la provenance et les mentions incluses.

******

### Liens

******

- Projet AutoJs6: https://github.com/SuperMonster003/AutoJs6
- Site officiel QuickJS: https://bellard.org/quickjs/
- Documentation QuickJS: https://bellard.org/quickjs/quickjs.html
- Dépôt source QuickJS: https://github.com/bellard/quickjs
- Avis tiers: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
