******

### سجل الإصدارات

******

# v1.0.2

###### 2026/09/13

* `إصلاح` استخدام تواريخ بناء باللغة الإنجليزية في بيانات المكون بغض النظر عن لغة جهاز البناء

# v1.0.1

###### 2026/09/13

* `تحسين` التحقق أثناء البناء من محاذاة صفحات 16 KB للمكتبات الأصلية ذات 64 بت, مع فحص عقد manifest وتقارير JSON
* `تحسين` توحيد تنشيط المضيف وبيانات الإضافة والوثائق المترجمة وتجميع إصدارات APK الموقعة وفق قواعد الإضافات المشتركة

# v1.0.0

###### 2026/09/02

* `تنبيه` يقبل release الأول immutable JavaScript source snapshot واحدة ولا يقدم AutoJs6 globals أو Java bridge أو filesystem أو network أو timers أو native addons أو multi-file imports
* `ميزة` يشغل JavaScript بمحرك QuickJS 2026-06-04 الرسمي المضمن والمحدد بالتوجيه `"quickjs";` أو العلامتين `*.quickjs.js` و `*.quickjs.mjs`, من دون تحويل التنفيذ إلى Rhino أو Node.js أو Bun
* `ميزة` يكتشف runtime service محمية بعقد AIDL ذي إصدار, وينقل source عبر `ParcelFileDescriptor`, ويبث أحداث stdout و stderr المحدودة عبر oneway callbacks
* `ميزة` يدعم scripts و ES modules بملف واحد مع BigInt و Unicode و Promise jobs و top-level await و errors و timeout وحدود output و memory و stack و cancellation و prewarm
* `ميزة` يوفر حزم APK من أنواع `arm64-v8a` و `armeabi-v7a` و `x86_64` و `x86` و `universal` بالعقد نفسه للمحرك الأصلي
* `ميزة` يوفر Wake activation و metadata كاملة لـ PluginInfo وتعليمات و README و changelog بعشر لغات
* `تحسين` ينشئ `JSRuntime` و `JSContext` جديدين لكل طلب في العملية المعزولة `:quickjs_runtime` ويستبعد `quickjs-libc` و filesystem helpers و network helpers
* `تحسين` يثبت source files الرسمية المطابقة byte-exact بواسطة URL و SHA-256 للأرشيف و upstream commit وحجم و SHA-256 لكل ملف
* `تحسين` يبني native libraries مع alignment يبلغ 16 KB ويتحقق من ELF PT_LOAD وuncompressed APK data alignment, وينجح في مجموعات اختبارات Binder وQuickJS الثلاث على emulator `google_apis_ps16k` بمعمارية x86_64 وAPI 37 مع `PAGE_SIZE=16384`
* `تحسين` يغطي runtime على أجهزة arm64 حقيقية بنظام Android 9 و 12 و 13 و 15, مع JVM tests و instrumentation tests و Android Lint و source verification و APK inventory
* `اعتماد` يضمن source QuickJS 2026-06-04 الرسمي بترخيص MIT من commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
