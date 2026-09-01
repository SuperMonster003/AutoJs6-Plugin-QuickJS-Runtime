******

### 发布历史

******

# v1.0.0

###### 2026/09/02

* `提示` 首个版本每次只接收一个不可变 JavaScript 源码快照, 并且有意不提供 AutoJs6 全局对象, Java 桥, 文件系统, 网络, 定时器, 原生扩展或多文件导入
* `功能` 通过 `"quickjs";` 指令或 `*.quickjs.js` 和 `*.quickjs.mjs` 标记选择内嵌的官方 QuickJS 2026-06-04 引擎运行 JavaScript, 不会把执行转发给 Rhino, Node.js 或 Bun
* `功能` 通过版本化 AIDL 契约发现受保护的 runtime service, 使用 `ParcelFileDescriptor` 传输源码, 并通过 oneway callback 流式返回有界 stdout 和 stderr 事件
* `功能` 支持脚本和单文件 ES module, 包括 BigInt, Unicode, Promise job, module 顶层 await, 错误, 超时, 输出限制, 内存和栈限制, 取消及预热
* `功能` 发布采用相同原生引擎契约的 `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86` 和 `universal` APK
* `功能` 提供 Wake 激活, 完整 PluginInfo 元数据, 以及 10 种语言的插件中心说明, README 和 changelog
* `改进` 在隔离的 `:quickjs_runtime` 进程中为每个请求创建全新 `JSRuntime` 和 `JSContext`, 并且不编译 `quickjs-libc` 及文件系统和网络辅助代码
* `改进` 按归档 URL, 归档 SHA-256, 上游 commit, 单文件大小和单文件 SHA-256 固定逐字节一致的官方源码
* `改进` 以 16 KB 页面对齐构建原生库, 验证 ELF PT_LOAD 与 APK 未压缩数据对齐, 并在报告 `PAGE_SIZE=16384` 的 API 37 x86_64 `google_apis_ps16k` 模拟器上通过全部 3 组 Binder 与 QuickJS 仪器测试
* `改进` 在真实 Android 9, 12, 13 和 15 arm64 设备上覆盖 runtime 行为, 并提供 JVM 测试, instrumentation 测试, Android Lint, 源码验证和 APK 清单检查
* `依赖` 内嵌采用 MIT 许可证的官方 QuickJS 2026-06-04 源码, 对应 commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e)
