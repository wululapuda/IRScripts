[EN](Home-EN) | **中文**

# IR Scripts Wiki（中文）

欢迎查阅 **IR Scripts** 文档。

IR Scripts 为 **Minecraft 1.12.2** 的 [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) 车辆提供 JavaScript 脚本支持。在车辆 JSON 中挂载 `.js` 文件即可控制机车、读取仪表、播放音效、添加游戏内按钮，无需单独编写 Forge 模组。

---

## 能做什么

- 每 tick 运行（`LOOP-TICK`）、生成时运行一次（`ONCE`）、或通过 GUI 按钮触发（`BUTTON`）
- 控制节流、制动、引擎、耦合器
- 读取 IR 仪表数值（速度、锅炉压力、RPM、货物等）
- 在列车位置播放自定义音效、驱动动画与粒子
- 查询轨道几何与道岔方向（`track` API）
- 加载 OBJ 模型、沿曲线渲染（`model` / `math` API）

脚本在**服务端**执行（单人模式为本地集成服务端）。每个列车实例拥有独立的脚本运行时。

---

## 文档导航

| 页面 | 说明 |
|------|------|
| [脚本手册](Handbook-ZH) | 分章教程，推荐从这里开始 |
| [完整导航](Sidebar-ZH) | 全部页面索引 |
| [API 速查](API-Reference-ZH) | 配置说明、全部 API、示例代码 |
| [math API](Math-ZH) | 向量、曲线、交点计算 |
| [model API](Model-ZH) | OBJ 加载、网格变换、渲染 |
| [track API](Track-ZH) | 轨道几何、道岔方向、脚本注册 |

---

## 30 秒示例

**车辆 JSON：**
```json
"scripts": [{
  "path": "mypack:scripts/my_loco.js",
  "functions": { "onTick": "LOOP-TICK", "horn": "BUTTON" }
}]
```

**脚本：**
```javascript
function onTick() {
    if (stock.getSpeedKmh() > 60) stock.control.setTrainBrake(0.5);
}
function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

→ 详细说明见 [第一个脚本](First-Script-ZH)。

---

## 依赖

| 项目 | 版本 |
|------|------|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2864+ |
| Immersive Railroading | 需要 |
| IR Scripts | 2.0.0（`irscripts`） |