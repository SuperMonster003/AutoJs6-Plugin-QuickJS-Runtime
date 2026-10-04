<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap-night/ic_launcher.png" />
      <img src="https://raw.githubusercontent.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/master/app/src/main/res/mipmap/ic_launcher.png" alt="QuickJS Runtime" border="0" width="128" />
    </picture>
  </p>

  <p>在独立 Android 进程中使用独立 QuickJS 引擎运行 JavaScript</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/releases"><img alt="GitHub release" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言

******

当前 README.md 支持以下语言:

- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-en.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ar.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-es.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-fr.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-ru.md)
- 简体中文 [zh-Hans] # 当前
- [繁體中文 香港 [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 台灣 [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.readme/README-zh-Hant-TW.md)

******

### 简介

******

QuickJS Runtime 是一个独立 Android 插件, 使 AutoJs6 可以选择 [QuickJS](https://bellard.org/quickjs/) 作为独立 JavaScript 引擎. 每个请求都会在隔离的 `:quickjs_runtime` 进程中获得全新的原生运行时和上下文. 源码与事件通过版本化 Binder 契约传输, 因此这里运行的是真正的 QuickJS, 不是 Rhino, Node.js 或 Bun 的别名.

******

### 安装和使用

******

1. 在 Android 7.0 (API 24) 或更高版本上使用 AutoJs6 build 5279 (6.8.0) 或更高版本.
2. 安装与设备 ABI 对应的发布 APK. 如果不确定 ABI, 可安装体积更大的 `universal` APK.
3. 打开 AutoJs6 插件中心并启用 QuickJS Runtime. 如果 Android 仍将新安装的包保持在停止状态, 请使用宿主显示的 `激活` 操作.
4. 可在 `.js` 或 `.mjs` 文件开头放置独立指令 `"quickjs";`, 也可把文件命名为脚本模式的 `*.quickjs.js` 或 ES module 模式的 `*.quickjs.mjs`; 然后从 AutoJs6 正常运行.

> 1.0 版本每次请求只接收一个不可变源码快照. 它不会传输项目目录或解析相对文件导入, 并且有意不暴露 AutoJs6 全局对象, Java 桥, 文件系统, 网络, `std` 或 `os` 模块.

******

### 快速开始

******

运行此文件以确认 AutoJs6 已选择 QuickJS 并启动原生运行时:

```javascript
"quickjs";

console.log("QuickJS 2026-06-04");
console.log("Unicode", "\u4e2d\u6587", 2n ** 63n);
```

输出会标识 QuickJS 2026-06-04, 打印 Unicode 文本并计算 BigInt 表达式.

******

### 兼容性和范围

******

- 引擎: 官方 QuickJS 2026-06-04, 修订版 `2026-06-04+3d5e064e9d`, commit [`3d5e064e9dd67c70f7962836505a7fa067bf0a4e`](https://github.com/bellard/quickjs/commit/3d5e064e9dd67c70f7962836505a7fa067bf0a4e), 源码归档 SHA-256 为 `b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a`.
- 平台: Android 7.0 (API 24) 或更高版本. 单 ABI APK 支持 `arm64-v8a`, `armeabi-v7a`, `x86_64` 和 `x86`; `universal` APK 包含全部四种 ABI.
- 语言: 支持 JavaScript 脚本和单文件 ES module, 包括 BigInt, Unicode, Promise job 和 module 顶层 await. 不解析 TypeScript.
- 请求限制: 源码最大 8 MiB, stdout 与 stderr 合计最大 4 MiB, 默认超时 60 seconds, 默认内存 64 MiB, 最大内存 256 MiB, 默认栈 1 MiB.
- 运行时边界: 插件只增加有界的 `console` 和 `print` 输出辅助对象. 宿主自动化 API, Java 对象, 文件系统和网络 API, `std`, `os`, 原生扩展, 定时器及多文件模块图不属于 1.0 版本范围.

******

### 权限和完整性

******

- Wake Activity 和两个导出服务均要求 `org.autojs.permission.PLUGIN`; 执行使用插件 UID 并位于独立的 `:quickjs_runtime` 进程.
- 每个请求都会创建新的 `JSRuntime` 和 `JSContext`. 内存, 栈, 时间, 源码和输出限制均会强制执行, 取消操作会到达 QuickJS 中断处理器.
- 源码通过只读 `ParcelFileDescriptor` 进入. 输出使用有界 oneway 回调分块, 终止 Bundle 只包含状态和诊断摘要.
- 仓库保存固定官方归档中的逐字节一致文件, 并验证大小和 SHA-256. 引擎没有编译 `quickjs-libc`, 文件系统辅助代码或网络辅助代码.

******

### 插件接口

******

供 AutoJs6 宿主和插件开发者使用的稳定标识和限制:

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

`QuickJsRuntimeService` 通过 action `org.autojs.plugin.quickjs.RUNTIME` 和 category `quickjs` 被发现. `runScript` 接收请求 Bundle 和只读源码描述符, 通过回调流式返回有界 stdout 与 stderr 事件, 并返回包含状态, 时间和诊断的终止 Bundle. `cancelScript` 可在调度前或执行中安全调用, `prewarmRuntime` 会执行原生引擎就绪检查.

16 KB 状态: 原生链接器已配置 16 KB 最大页面大小, APK 校验器会检查每个 ELF PT_LOAD 对齐以及未压缩 ZIP 数据对齐. 已在 API 37 x86_64 `google_apis_ps16k` 模拟器上完成端到端运行验证 (`PAGE_SIZE=16384`), 3 组 Binder 与 QuickJS 仪器测试全部通过.

******

### 发布历史

******

#### v1.0.4

_2026/10/04_

- `改进` 应用与插件中心图标采用透明彩色闪电, 保留 SVG 原稿的比例, 留白, 渐变和内部阴影, 并支持通过 Icon Studio 重建和调整
- `改进` 插件中心图标采用统一工作台调整后的尺寸, 位置, 亮暗图稿与圆形底色, 保留可重建原稿和参数

#### v1.0.3

_2026/09/19_

- `修复` AGP 9.1 构建时的 SDK XML v4 解析警告, 以及 JVM 单元测试误触发 APK 原生库对齐检查的问题 (共享构建插件 1.8.3)
- `改进` compileSdk/targetSdk 升级至 37 (Android 17)

#### v1.0.2

_2026/09/13_

- `修复` 插件信息中的构建日期受构建环境语言影响, 未统一使用英文格式的问题

##### 更多发布历史

- [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建和验证

******

构建需要 JDK 17 或更高版本, Python 3, Android SDK 37, NDK 28.2.13676358 和 CMake 3.22.1. 标准检查会验证固定 QuickJS 源码, 生成文档, JVM 测试, 全部 APK 变体, instrumentation 包, Android Lint, ELF 对齐和原生库清单.

```powershell
py -B -m unittest discover -s tools\quickjs\tests -p "test_*.py"
py .python\generate_markdown.py --check
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
py tools\quickjs\verify_apk_variants.py app\build\outputs\apk debug
```

******

### 本地化和文档生成

******

编辑 JSON 和 Markdown 模板后运行 `py .python/generate_markdown.py`. 不要手动编辑生成的 README, changelog 或插件说明文件. `--check` 模式会在不写入文件的情况下验证语言结构, 版本一致性, 本地化资源, 孤立文件和生成文件漂移.

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

图标保留维护者提供的 [SVG 原稿](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/.python/icons/quickjs-runtime-lightning.svg)及原始构图. 可通过 Icon Studio 调整, 使用 `python .python/generate_icon_studio.py --check` 复核配方与生成资源的一致性.

******

### 许可证

******

插件代码采用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/LICENSE). 内嵌的官方 QuickJS 源码采用 MIT 许可证. 来源和随附声明请参阅 [第三方声明](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md).

******

### 链接

******

- AutoJs6 项目: https://github.com/SuperMonster003/AutoJs6
- QuickJS 官方网站: https://bellard.org/quickjs/
- QuickJS 文档: https://bellard.org/quickjs/quickjs.html
- QuickJS 源码仓库: https://github.com/bellard/quickjs
- 第三方声明: https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/THIRD_PARTY_NOTICES.md


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-QuickJS-Runtime/blob/master/docs/16kb.md)
