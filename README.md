# 海鸥工具箱 (Seagull Toolbox)

一个纯本地的安卓小工具合集。**零权限、零网络、零广告、零账号。**

- 包名：`com.seagull.toolbox`
- 最低版本：Android 7.0 (API 24)
- Manifest 里**没有任何 `<uses-permission>`**

## 工具清单

| 组 | 工具 | 说明 |
|---|---|---|
| 测量 | **尺子** | 屏幕物理尺子。xdpi 不准，所以带**银行卡校准**（85.60mm 国际标准） |
| 测量 | **水平仪** | 加速度计气泡水平仪，实时倾角 ±10° |
| 图像 | **取色器** | 相册选图 → 点击取色 → HEX / RGB / HSB / Android 色值 |
| 图像 | **二维码** | 生成文本 / 网址 / WiFi 二维码（ZXing） |
| 文本 | **JSON** | 格式化（2/4 空格）/ 压缩 / 语法校验 |
| 文本 | **哈希** | MD5 / SHA-1 / SHA-256 / SHA-512，大小写可切 |
| 计算 | **时间戳** | 时间戳 ↔ 日期互转，10/13 位自动识别，实时刷新 |
| 计算 | **单位换算** | 长度 / 重量 / 温度 / 面积 / 体积 / 数据 / 速度 / 时间 |
| 计算 | **动态令牌** | TOTP 2FA（HMAC-SHA1 / 6 位 / 30 秒），兼容各类验证器 |
| 计算 | **密码生成** | SecureRandom 强密码，带熵评估与强度分级 |
| 设备 | **设备信息** | 硬件 / 系统 / 内存 / 存储 / 显示 / 传感器全览 |
| 设备 | **坏点检测** | 全屏 9 色轮播，查坏点亮点暗斑 |

## 加新工具只要两步

**第一步**：在 `app/src/main/java/com/seagull/toolbox/tools/` 写一个 Composable：

```kotlin
@Composable
fun MyTool() {
    Column {
        // 你的界面，直接用 ui/Widgets.kt 里的 ToolInput / ResultBox / Chip / KV
    }
}
```

**第二步**：在 `core/Tools.kt` 的 `ALL` 列表里加一行：

```kotlin
Tool("mytool", "我的工具", "我", Group.TEXT, "一句话描述") { MyTool() },
```

首页网格、分组标题、搜索索引、返回栈**全部自动生效**，不需要改任何其他地方。

## 设计约定

- **图标用字符不用 drawable** —— `Tool.badge` 一个字符，配色跟随分组主题色。省掉一堆图标资源，也没有多语言问题。
- **零权限** —— 目前所有工具都不需要权限。取色器用 SAF 选图（不需要存储权限），坏点检测用全屏 Dialog（不需要悬浮窗权限）。将来加需要权限的工具时，单独在 Manifest 里声明。
- **统一外壳** —— `ToolPage` 提供标题栏 + 返回 + 滚动容器，工具只写内容。需要全屏的工具（坏点检测）自己弹 Dialog 绕过外壳。

## 构建

推送到 `main` 或打 `v*` tag 即触发 GitHub Actions（复用 `Code-xy/video_hex` 的工作流）。

```
APK    https://github.com/Code-xy/seagull_toolbox/raw/ci-logs/.ci/app-release.apk
日志   https://github.com/Code-xy/seagull_toolbox/raw/ci-logs/.ci/build.log
```

### 签名

仓库内置 `signing/dev-release.jks`（alias `toolbox`），保证每个包都能覆盖升级。

```
SHA256: BA:55:B9:3A:E0:09:4B:87:6D:8A:59:E8:56:86:91:EC:16:37:FD:4E:34:12:C6:93:19:E0:05:59:7A:DC:47:07
```

优先级：CI secrets (`ANDROID_KEYSTORE_PATH`) > 仓库内置 jks > debug。

## 目录

```
app/src/main/java/com/seagull/toolbox/
├── MainActivity.kt            入口 + 简单返回栈
├── core/
│   ├── Tool.kt                Tool / Group 数据模型
│   ├── Tools.kt               ★ 工具注册表（加工具改这里）
│   └── Util.kt                剪贴板 / 格式化 / 字节换算
├── ui/
│   ├── Theme.kt               Material3 青绿主题
│   ├── HomeScreen.kt          分组网格首页 + 搜索
│   ├── ToolPage.kt            统一工具外壳
│   └── Widgets.kt             ToolInput / ResultBox / Chip / KV
└── tools/                     12 个工具，一个文件一个
```
