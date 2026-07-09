# 执行模式

`functions` 里每个函数都要指定**什么时候被调用**。这就是执行模式。

---

## 模式一览

| JSON 值 | 名称 | 何时调用 |
|---------|------|----------|
| `LOOP-TICK` | 每 tick 循环 | **每个游戏 tick** 调用一次（20 次/秒） |
| `LOOP-SCRIPTS` | 脚本循环 | 上一次**完全跑完**后才允许下一次；跑的过程中跳过后续 tick |
| `ONCE` | 一次性 | 实体创建后仅调用**一次** |
| `BUTTON` | 按钮 | 玩家在 GUI 里点击对应按钮时调用 |

旧版 JSON 里写 `"LOOP"` 等价于 `LOOP-TICK`。

---

## LOOP-TICK：每 tick 跑

适合：走行音、持续监控、每帧更新的逻辑。

```json
"functions": { "sounds": "LOOP-TICK" }
```

```javascript
function sounds() {
    var ratio = stock.readout.getSpeed();
    if (ratio > 0.5) {
        stock.sound.play("sounds/run.ogg", 1.0, 1.5, true);
    }
}
```

> [!WARNING]
> 函数体要轻量。每 tick 做重计算、频繁 `play` 循环音，会吃 CPU。  
> 走行音脚本建议加**状态判断**，只在音量/音调变化时才 `play`（见 [完整示例](Examples.md)）。

---

## LOOP-SCRIPTS：跑完再排队

适合：带 `utilPlay`、`time.sleep` 的**顺序逻辑**。

```json
"functions": { "sequence": "LOOP-SCRIPTS" }
```

```javascript
function sequence() {
    stock.sound.utilPlay("sounds/horn_1.ogg", 1.0);
    time.sleep(1.0);
    stock.sound.play("sounds/bell.ogg", 0.8);
}
```

`utilPlay` 和 `time.sleep` 会**暂停当前函数**，但**不冻结世界**——它们用 Rhino 协程按 tick 恢复。

上一次 `sequence()` 还没跑完时，新的 tick **不会**再启动一次，避免重入。

---

## ONCE：生成时一次

适合：初始化、欢迎语音、刷车时设默认状态。

```json
"functions": { "onSpawn": "ONCE" }
```

```javascript
function onSpawn() {
    if (stock.control.isDiesel()) {
        stock.control.setEngine(1);
    }
}
```

---

## BUTTON：玩家触发

适合：吹笛、开门、切换灯光等**手动操作**。

```json
"functions": { "playHorn": "BUTTON" }
```

显示条件（需 mod 配置开启脚本按钮）：

- 玩家正在骑乘该列车
- 打开 IR 车辆背包界面，或按 `E` 打开原版背包

按钮文字默认等于**函数名**（如 `playHorn`）。

详见 [BUTTON 按钮](Button.md)。

---

## 错误时会发生什么？

`LOOP-TICK` / `LOOP-SCRIPTS` 中若函数**抛出未捕获错误**：

- 该函数在本列车实例内被**永久禁用**
- 错误日志**只记录一次**（避免刷屏）
- **不会**崩溃服务端

`ONCE` / `BUTTON` 出错时仅本次调用失败，下次仍可触发。

---

## 怎么选？

| 你想做…… | 推荐模式 |
|----------|----------|
| 走行音、速度联动 | `LOOP-TICK` |
| 鸣笛 → 等待 → 再播别的 | `LOOP-SCRIPTS` |
| 刷车时初始化 | `ONCE` |
| 玩家手动操作 | `BUTTON` |
