# 安装与依赖

## 依赖一览

| 项目 | 版本 |
|------|------|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2864+ |
| Immersive Railroading | 需要 |
| UniversalModCore | IR 自带依赖 |
| TrackAPI | IR 自带依赖 |
| IR Scripts | 2.0.0（`irscripts`） |

把 `irscripts-2.0.jar` 放进 `mods` 文件夹即可。

> [!NOTE]
> Rhino 引擎已内置于 mod，**不依赖** JRE 自带的 Nashorn。

---

## 模组配置

配置文件：`config/irscripts.cfg`

| 选项 | 说明 |
|------|------|
| 脚本 `print()` 输出 | 是否写入服务端日志 |
| 调试日志 | 详细 debug 信息 |
| 显示脚本按钮 | 是否绘制 BUTTON 模式按钮 |

也可以在游戏里打开 **IR 配置界面**（默认小键盘 `/`），找到 **IR Scripts** 分类页修改。

JVM 参数可覆盖部分选项：

```
-Dirscripts.debug=true
-Dirscripts.scriptPrint=false
```

---

## 脚本文件放哪

脚本放在**你的资源包**里，路径与 JSON 中 `path` 对应：

```
你的资源包.zip
└── assets/
    └── mypack/
        ├── scripts/
        │   └── my_train.js      ← 脚本
        └── sounds/
            └── horn_1.ogg       ← 音效（可选）
```

JSON 里写：

```json
"path": "mypack:scripts/my_train.js"
```

域 `mypack` 对应 `assets/mypack/`。

---

## 在车辆 JSON 里注册

在车辆定义 JSON 的**根节点**（与 `properties` 并列）添加 `scripts`：

```json
{
  "name": "CRH1A",
  "max_speed_kmh": 250,
  "scripts": [{
    "path": "mypack:scripts/crh1a.js",
    "functions": {
      "onTick": "LOOP-TICK",
      "onSpawn": "ONCE",
      "openDoor": "BUTTON"
    }
  }]
}
```

| 字段 | 说明 |
|------|------|
| `path` | 脚本资源路径（IR Identifier 格式） |
| `functions` | 键 = JS 函数名，值 = 执行模式 |

一辆车可以挂**多个** `scripts` 条目，每个条目指向不同 `.js` 文件。

> [!WARNING]
> 函数名必须和 `.js` 里定义的 `function xxx()` **完全一致**，区分大小写。
