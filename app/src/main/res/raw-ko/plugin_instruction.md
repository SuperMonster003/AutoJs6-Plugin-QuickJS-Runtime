QuickJS Runtime은 AutoJs6에서 [QuickJS](https://bellard.org/quickjs/)를 독립 JavaScript 엔진으로 선택할 수 있게 하는 독립 Android 플러그인입니다. 각 요청은 격리된 `:quickjs_runtime` 프로세스에서 새 native runtime과 context를 받습니다. 소스와 이벤트는 버전이 지정된 Binder contract를 통과하므로 Rhino, Node.js, Bun의 alias가 아닌 실제 QuickJS 실행입니다.

이 release는 고정된 공식 QuickJS 2026-06-04 source를 native library에 포함하고 요청마다 새 runtime을 만듭니다. Source는 immutable descriptor에서 읽으며 stdout, stderr, error, cancellation, limit, completion은 host contract로 반환됩니다.

### 빠른 시작

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

출력은 QuickJS 2026-06-04를 표시하고 Unicode 텍스트와 BigInt 식 결과를 출력합니다.

### 호환성과 범위

- 엔진: 공식 QuickJS 2026-06-04, revision `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), source archive SHA-256 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 플랫폼: Android 7.0 (API 24) 이상. 단일 ABI APK는 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`을 지원하며 `universal` APK는 네 가지 모두를 포함합니다.
- 언어: JavaScript script와 단일 파일 ES module을 지원하며 BigInt, Unicode, Promise job, module top-level await를 포함합니다. TypeScript는 parse하지 않습니다.
- 요청 제한: 소스 8 MiB, stdout과 stderr 합계 4 MiB, 기본 timeout 60 seconds, 기본 memory 64 MiB, 최대 memory 256 MiB, 기본 stack 1 MiB.
- Runtime 경계: 플러그인은 제한된 `console`과 `print` 출력 helper만 추가합니다. Host automation API, Java object, filesystem, network API, `std`, `os`, native addon, timer, 다중 파일 module graph는 버전 1.0 범위 밖입니다.

ABI 선택, 정확한 제한, security property, build verification, 버전 1.0의 전체 범위는 [project README](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime)를 참조하십시오.
