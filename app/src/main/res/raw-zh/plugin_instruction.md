QuickJS Runtime 是一个独立 Android 插件, 使 AutoJs6 可以选择 [QuickJS](https://bellard.org/quickjs/) 作为独立 JavaScript 引擎. 每个请求都会在隔离的 `:quickjs_runtime` 进程中获得全新的原生运行时和上下文. 源码与事件通过版本化 Binder 契约传输, 因此这里运行的是真正的 QuickJS, 不是 Rhino, Node.js 或 Bun 的别名.

此版本把固定的官方 QuickJS 2026-06-04 源码嵌入原生库, 并为每个请求创建全新运行时. 源码从不可变描述符读取; stdout, stderr, 错误, 取消, 限制和完成状态通过宿主契约返回.

### 快速开始

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

输出会标识 QuickJS 2026-06-04, 打印 Unicode 文本并计算 BigInt 表达式.

### 兼容性和范围

- 引擎: 官方 QuickJS 2026-06-04, 修订版 `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), 源码归档 SHA-256 为 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 平台: Android 7.0 (API 24) 或更高版本. 单 ABI APK 支持 `arm64-v8a`, `armeabi-v7a`, `x86_64` 和 `x86`; `universal` APK 包含全部四种 ABI.
- 语言: 支持 JavaScript 脚本和单文件 ES module, 包括 BigInt, Unicode, Promise job 和 module 顶层 await. 不解析 TypeScript.
- 请求限制: 源码最大 8 MiB, stdout 与 stderr 合计最大 4 MiB, 默认超时 60 seconds, 默认内存 64 MiB, 最大内存 256 MiB, 默认栈 1 MiB.
- 运行时边界: 插件只增加有界的 `console` 和 `print` 输出辅助对象. 宿主自动化 API, Java 对象, 文件系统和网络 API, `std`, `os`, 原生扩展, 定时器及多文件模块图不属于 1.0 版本范围.

ABI 选择, 精确限制, 安全属性, 构建验证和完整 1.0 版本范围请参阅 [项目 README](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime).
