<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
  </p>

  <p>يشغل JavaScript بمحرك QuickJS مستقل داخل عملية Android معزولة</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### اللغات

******

يدعم README.md الحالي اللغات التالية:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- العربية [ar] # الحالية
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### مقدمة

******

QuickJS Runtime إضافة Android مستقلة تتيح لـ AutoJs6 اختيار [QuickJS](https://bellard.org/quickjs/) كمحرك JavaScript مستقل. يحصل كل طلب على runtime و context أصليين جديدين داخل العملية المعزولة `:quickjs_runtime`. ينتقل المصدر والأحداث عبر عقد Binder ذي إصدار محدد, لذلك فهذا تنفيذ QuickJS حقيقي وليس اسما مستعارا لـ Rhino أو Node.js أو Bun.

******

### التثبيت والاستخدام

******

1. استخدم AutoJs6 build 5279 (6.8.0) أو أحدث على Android 7.0 (API 24) أو أحدث.
2. ثبت APK الإصدار المطابق لـ ABI الجهاز, أو استخدم APK الأكبر `universal` إذا كان ABI غير معروف.
3. افتح مركز إضافات AutoJs6 وفعل QuickJS Runtime. إذا ترك Android الحزمة الجديدة متوقفة فاستخدم إجراء host باسم `تفعيل`.
4. حدد QuickJS بوضع التوجيه المستقل `"quickjs";` في بداية ملف `.js` أو `.mjs`, أو سم الملف `*.quickjs.js` لوضع script أو `*.quickjs.mjs` لوضع ES module, ثم شغله بصورة عادية من AutoJs6.

> يقبل الإصدار 1.0 لقطة مصدر ثابتة واحدة لكل طلب. لا ينقل شجرة مشروع ولا يحل imports نسبية, ولا يعرض عمدا globals الخاصة بـ AutoJs6 أو Java bridge أو filesystem أو network أو وحدتي `std` و `os`.

******

### بدء سريع

******

شغل هذا الملف للتأكد من أن AutoJs6 اختار QuickJS وبدأ native runtime:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

يعرض الناتج QuickJS 2026-06-04, ويطبع نص Unicode ويقيم تعبير BigInt.

******

### التوافق والنطاق

******

- المحرك: QuickJS الرسمي 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), و SHA-256 لأرشيف المصدر هو `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- المنصة: Android 7.0 (API 24) أو أحدث. تدعم حزم APK ذات ABI واحد `arm64-v8a` و `armeabi-v7a` و `x86_64` و `x86`; وتضم حزمة `universal` الأنواع الأربعة.
- اللغة: تدعم JavaScript scripts و ES modules بملف واحد, بما في ذلك BigInt و Unicode و Promise jobs و module top-level await. لا يتم تحليل TypeScript.
- الحدود: مصدر حتى 8 MiB, ومجموع stdout و stderr حتى 4 MiB, و timeout افتراضي 60 seconds, و memory افتراضية 64 MiB, و memory قصوى 256 MiB, و stack افتراضي 1 MiB.
- حد runtime: لا تضيف الإضافة إلا مساعدي إخراج محدودين `console` و `print`. ولا تدخل host automation API أو Java objects أو filesystem أو network API أو `std` أو `os` أو native addons أو timers أو multi-file module graphs في نطاق الإصدار 1.0.

******

### الأذونات والتكامل

******

- يتطلب Wake Activity والخدمتان المصدرتان `org.autojs.permission.PLUGIN`; ويتم التنفيذ بهوية UID للإضافة في العملية المنفصلة `:quickjs_runtime`.
- ينشأ `JSRuntime` و `JSContext` جديدان لكل طلب. تطبق حدود memory و stack والوقت والمصدر والناتج, ويصل الإلغاء إلى interrupt handler في QuickJS.
- يصل المصدر عبر `ParcelFileDescriptor` للقراءة فقط. يستخدم الناتج bounded oneway callback chunks, ولا تحمل terminal Bundles إلا الحالة وملخصات التشخيص.
- يحتفظ المستودع بملفات مطابقة byte-exact من الأرشيف الرسمي المثبت ويتحقق من الأحجام و SHA-256. لا يتم بناء `quickjs-libc` أو filesystem helpers أو network helpers ضمن المحرك.

******

### واجهة الإضافة

******

معرفات وحدود ثابتة لمطوري host وإضافة AutoJs6:

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

يكتشف `QuickJsRuntimeService` عبر action `org.autojs.plugin.quickjs.RUNTIME` و category `quickjs`. يستقبل `runScript` request Bundle و source descriptor للقراءة فقط, ويرسل أحداث stdout و stderr المحدودة عبر callback, ثم يعيد terminal Bundle فيه الحالة والتوقيت والتشخيص. يكون `cancelScript` آمنا قبل dispatch وأثناءه, ويتحقق `prewarmRuntime` من جاهزية native engine.

حالة 16 KB: يضبط native linker على maximum page size يبلغ 16 KB, ويتحقق APK verifier من alignment لكل ELF PT_LOAD ومن بيانات ZIP غير المضغوطة. اكتمل التحقق من التنفيذ طرفا إلى طرف على emulator `google_apis_ps16k` بمعمارية x86_64 وAPI 37 أبلغ عن `PAGE_SIZE=16384`; وقد نجحت مجموعات اختبارات Binder وQuickJS الثلاث جميعها.

******

### سجل الإصدارات

******

#### v1.0.3

_2026/09/15_

- `تحسين` رفع compileSdk و targetSdk إلى 37 (Android 17)؛ لا يعتمد سلوك المكون الإضافي على الهدف الجديد

#### v1.0.2

_2026/09/13_

- `إصلاح` استخدام تواريخ بناء باللغة الإنجليزية في بيانات المكون بغض النظر عن لغة جهاز البناء

#### v1.0.1

_2026/09/13_

- `تحسين` التحقق أثناء البناء من محاذاة صفحات 16 KB للمكتبات الأصلية ذات 64 بت, مع فحص عقد manifest وتقارير JSON
- `تحسين` توحيد تنشيط المضيف وبيانات الإضافة والوثائق المترجمة وتجميع إصدارات APK الموقعة وفق قواعد الإضافات المشتركة

##### لمزيد من سجل الإصدارات

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-ar.md)

******

### البناء والتحقق

******

يتطلب البناء JDK 17 أو أحدث, و Python 3, و Android SDK 37, و NDK 28.2.13676358, و CMake 3.22.1. تتحقق الفحوص القياسية من source QuickJS المثبت, والوثائق المولدة, و JVM tests, وكل APK variants, وحزمة instrumentation, و Android Lint, و ELF alignment, وجرد native libraries.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### الترجمة وتوليد الوثائق

******

عدل JSON و Markdown templates ثم شغل `py .python/generate_markdown.py`. لا تعدل README أو changelog أو تعليمات الإضافة المولدة يدويا. يتحقق `--check` من بنية اللغات وتطابق الإصدار والموارد المترجمة والملفات اليتيمة و drift دون كتابة.

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

### الترخيص

******

يستخدم كود الإضافة [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). ويستخدم source QuickJS الرسمي المضمن ترخيص MIT. راجع [إشعارات الطرف الثالث](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md) للمصدر والإشعارات المضمنة.

******

### الروابط

******

- مشروع AutoJs6: https://github.com/SuperMonster003/AutoJs6
- موقع QuickJS الرسمي: https://bellard.org/quickjs/
- وثائق QuickJS: https://bellard.org/quickjs/quickjs.html
- مستودع source الخاص بـ QuickJS: https://github.com/bellard/quickjs
- إشعارات الطرف الثالث: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
