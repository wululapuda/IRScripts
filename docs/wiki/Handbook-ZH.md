[EN](Handbook-EN) | **中文**

[< 目录](Handbook-ZH)

---

# IR Scripts 脚本手册

你好，这是一部 **IR Scripts** 使用百科。

IR Scripts 为 [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) 的车辆提供 **JavaScript 脚本**能力。你不需要单独写 Forge 模组，只要在车辆 JSON 里挂上 `.js` 文件，就能控制机车、读取仪表、播放音效、驱动动画，还能在 GUI 里加按钮。

如果你想跟着它学习，请先确定：

- 你玩过沉浸铁路，至少看过一些教程视频
- 你会写 **JSON**，知道 IR 车辆定义放在哪
- 你了解 **JavaScript 基础**（`var`、函数、`if` 就够起步）
- 你知道 `.ogg` 音效、`.anim` 动画在资源包里的路径规则
- 你最好会看 `latest.log`

> **注意：**
> 本教程适用 **IR Scripts 2.0.0**，游戏版本 **Minecraft 1.12.2**（Forge）。脚本在**服务端**执行；单人模式也是本地集成服务端。

> **提示：**
> 如果你正在做 IR 资源包，建议同时阅读 [原野的 IR 资源包手册](https://goldenfield192.github.io/)——那里讲 JSON、模型、Animatrix；本手册讲**脚本怎么写**。

---

## 你能用它做什么

| 能力 | 一句话 |
|------|--------|
| 每 tick 逻辑 | 按速度切走行音、自动制动、排烟 |
| 生成时初始化 | 刷车时自动启动引擎 |
| GUI 按钮 | 玩家点按钮吹笛、开门 |
| 读仪表 | 速度、锅炉压力、RPM、货物装载…… |
| 写控制量 | 节流、制动、耦合器、控制组 |
| 播自定义音效 | 带音量、音调、循环、听距 |
| 驱动动画 | 播放 `.anim`，开关车门 |
| 粒子效果 | 黑烟、蒸汽 |
| 轨道查询 | 几何、道岔方向、脚本注册 |
| 自定义模型 | OBJ 加载、沿曲线渲染 |

---

## 30 秒看一眼

**车辆 JSON**（与 `properties` 并列的根节点）：

```json
{
  "name": "My Train",
  "max_speed_kmh": 200,
  "scripts": [{
    "path": "mypack:scripts/my_train.js",
    "functions": {
      "onTick": "LOOP-TICK",
      "onSpawn": "ONCE",
      "playHorn": "BUTTON"
    }
  }]
}
```

**脚本** `assets/mypack/scripts/my_train.js`：

```javascript
function onSpawn() {
    print("生成: " + stock.getDefinitionId());
}

function onTick() {
    if (stock.getSpeedKmh() > 80) {
        stock.control.setTrainBrake(0.5);
    }
}

function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

---

## 推荐阅读顺序

1. [安装与依赖](Install-ZH)
2. [第一个脚本](First-Script-ZH)
3. [执行模式](Script-Modes-ZH)
4. 按需查阅 [API 章节](Stock-Basic-ZH)

我们开始吧。