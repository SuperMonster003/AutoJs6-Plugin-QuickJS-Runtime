******

### Historique des versions

******

# v1.0.0

###### 2026/09/02

* `Conseil` La première version accepte un instantané JavaScript immuable et ne fournit ni globales AutoJs6, pont Java, fichiers, réseau, timers, addons natifs, ni imports multi-fichiers
* `Fonctionnalité` Exécute JavaScript avec le moteur officiel intégré QuickJS 2026-06-04 choisi par la directive `"quickjs";`, sans transférer l'exécution vers Rhino, Node.js ou Bun
* `Fonctionnalité` Découvre un runtime service protégé via AIDL versionné, transfère la source avec `ParcelFileDescriptor` et diffuse des événements stdout et stderr bornés par callbacks oneway
* `Fonctionnalité` Prend en charge scripts et modules ES mono-fichier avec BigInt, Unicode, jobs Promise, top-level await, erreurs, timeout, limites de sortie, mémoire et stack, annulation et préchauffage
* `Fonctionnalité` Livre les APK `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` et `universal` avec le même contrat natif
* `Fonctionnalité` Fournit activation Wake, métadonnées PluginInfo complètes, instructions, README et changelog dans 10 langues
* `Amélioration` Crée un `JSRuntime` et un `JSContext` neufs par requête dans le processus isolé `:quickjs_runtime` et omet `quickjs-libc` ainsi que les helpers fichiers et réseau
* `Amélioration` Fixe les sources officielles identiques octet par octet par URL, SHA-256 d'archive, commit upstream, taille et SHA-256 de chaque fichier
* `Amélioration` Compile les bibliothèques natives avec alignement 16 KB et vérifie ELF PT_LOAD et données APK non compressées, tout en notant l'absence de test réel sur pages de 16 KB
* `Amélioration` Couvre le runtime sur appareils arm64 réels Android 9, 12, 13 et 15, plus tests JVM et instrumentation, Android Lint, vérification source et inventaire APK
* `Dépendance` Intègre les sources officielles QuickJS 2026-06-04 sous licence MIT au commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
