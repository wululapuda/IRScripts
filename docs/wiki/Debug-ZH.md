[EN](Debug-EN) | **中文**

[< 目录](Handbook-ZH)

---
# 调试与排错

脚本出问题，**先看日志**。

---

## 日志在哪

整合包：`versions/<版本名>/logs/latest.log`

---

## 关键字

| 前缀 | 含义 |
|------|------|
| `[Bootstrap]` | 扫描车辆 JSON、发现 `scripts` 块 |
| `[Registry]` | 注册函数与执行模式 |
| `[Runtime]` | 创建/销毁脚本实例 |
| `[Engine]` | Rhino 引擎初始化 |
| `[Script\|...\|...]` | 你的 `print()` 或脚本错误 |

示例：

```
[Script|a1b2c3d4|mypack:scripts/my_train.js] 速度 85.2 km/h
```

---

## JVM 参数

| 参数 | 说明 |
|------|------|
| `-Dirscripts.debug=true` | 详细调试日志 |
| `-Dirscripts.scriptPrint=false` | 关闭 print |
| `-Dirscripts.loopErrorCooldownMs=5000` | LOOP 错误重复日志间隔 |

---

## 常见症状

### print 没有任何输出

- `config/irscripts.cfg` 里关闭了脚本 print
- 函数模式不对（`ONCE` 只跑一次，`BUTTON` 要点击）
- JSON `path` 或函数名拼写错误，Bootstrap 没注册上

### LOOP 函数跑几次就停了

函数内部**抛错**后会被永久禁用。搜 `latest.log` 里的 `[Script|` 和 `Error`。

### 音效没声音

- 路径/域不对：用 `print` 确认 `stock.getDefinitionId()`
- 文件不在 `assets/<域>/sounds/` 下
- 听距太远：加第 5 个参数 `maxDistance`
- 单人游戏也要服务端加载资源包

### utilPlay / sleep 后世界卡住

2.0 已改为协程调度，**不应**再卡世界。若仍卡住，检查 mod 版本是否最新，并贴 `latest.log`。

### BUTTON 没显示

见 [BUTTON 按钮](Button-ZH) 和 [常见问题](Problems-ZH)。

---

## 排错流程

```
1. 确认 irscripts jar 在 mods 里、版本 2.0
2. 确认 JSON scripts 块格式正确
3. 重启 / 重载 IR 定义
4. 搜 latest.log：[Bootstrap] [Registry] [Script
5. 加 print 定位逻辑分支
6. 仍不行 → 带日志反馈
```