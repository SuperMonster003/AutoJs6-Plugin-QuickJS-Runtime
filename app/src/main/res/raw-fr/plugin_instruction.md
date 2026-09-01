QuickJS Runtime est un plugin Android autonome qui permet à AutoJs6 de choisir [QuickJS](https://bellard.org/quickjs/) comme moteur JavaScript indépendant. Chaque requête reçoit un runtime et un contexte natifs neufs dans le processus isolé `:quickjs_runtime`. Le code et les événements traversent un contrat Binder versionné: il s'agit donc du véritable QuickJS, et non d'un alias de Rhino, Node.js ou Bun.

Cette version intègre les sources officielles fixées de QuickJS 2026-06-04 dans une bibliothèque native et crée un runtime neuf pour chaque requête. La source est lue depuis un descripteur immuable; stdout, stderr, erreurs, annulation, limites et fin reviennent via le contrat hôte.

### Démarrage rapide

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

La sortie identifie QuickJS 2026-06-04, affiche du texte Unicode et évalue une expression BigInt.

### Compatibilité et portée

- Moteur: QuickJS officiel 2026-06-04, révision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), avec SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a` pour l'archive source.
- Plateforme: Android 7.0 (API 24) ou ultérieur. Les APK mono-ABI prennent en charge `arm64-v8a`, `armeabi-v7a`, `x86_64` et `x86`; l'APK `universal` contient les quatre.
- Langage: scripts JavaScript et modules ES mono-fichier, avec BigInt, Unicode, jobs Promise et top-level await de module. TypeScript n'est pas analysé.
- Limites: source jusqu'à 8 MiB, stdout et stderr combinés jusqu'à 4 MiB, délai par défaut 60 seconds, mémoire par défaut 64 MiB, mémoire maximale 256 MiB et stack par défaut 1 MiB.
- Frontière du runtime: le plugin ajoute seulement les helpers de sortie bornés `console` et `print`. API d'automatisation, objets Java, fichiers, réseau, `std`, `os`, addons natifs, timers et graphes multi-fichiers sont hors de la version 1.0.

Consultez le [README du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime) pour le choix ABI, les limites exactes, les propriétés de sécurité, la vérification de compilation et toute la portée de la version 1.0.
