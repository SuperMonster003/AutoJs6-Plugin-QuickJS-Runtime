QuickJS Runtime إضافة Android مستقلة تتيح لـ AutoJs6 اختيار [QuickJS](https://bellard.org/quickjs/) كمحرك JavaScript مستقل. يحصل كل طلب على runtime و context أصليين جديدين داخل العملية المعزولة `:quickjs_runtime`. ينتقل المصدر والأحداث عبر عقد Binder ذي إصدار محدد, لذلك فهذا تنفيذ QuickJS حقيقي وليس اسما مستعارا لـ Rhino أو Node.js أو Bun.

يضمن هذا release source QuickJS الرسمي المثبت 2026-06-04 كمكتبة native وينشئ runtime جديدا لكل طلب. يقرأ source من immutable descriptor; وتعود stdout و stderr و errors و cancellation و limits و completion عبر عقد host.

### بدء سريع

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

يعرض الناتج QuickJS 2026-06-04, ويطبع نص Unicode ويقيم تعبير BigInt.

### التوافق والنطاق

- المحرك: QuickJS الرسمي 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), و SHA-256 لأرشيف المصدر هو `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- المنصة: Android 7.0 (API 24) أو أحدث. تدعم حزم APK ذات ABI واحد `arm64-v8a` و `armeabi-v7a` و `x86_64` و `x86`; وتضم حزمة `universal` الأنواع الأربعة.
- اللغة: تدعم JavaScript scripts و ES modules بملف واحد, بما في ذلك BigInt و Unicode و Promise jobs و module top-level await. لا يتم تحليل TypeScript.
- الحدود: مصدر حتى 8 MiB, ومجموع stdout و stderr حتى 4 MiB, و timeout افتراضي 60 seconds, و memory افتراضية 64 MiB, و memory قصوى 256 MiB, و stack افتراضي 1 MiB.
- حد runtime: لا تضيف الإضافة إلا مساعدي إخراج محدودين `console` و `print`. ولا تدخل host automation API أو Java objects أو filesystem أو network API أو `std` أو `os` أو native addons أو timers أو multi-file module graphs في نطاق الإصدار 1.0.

راجع [README المشروع](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime) لاختيار ABI والحدود الدقيقة وخصائص الأمان والتحقق من البناء والنطاق الكامل للإصدار 1.0.
