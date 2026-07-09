# stock 基础 API

`stock` 根对象上的方法与属性。

---

## 身份与状态

| 方法 | 返回 | 说明 |
|------|------|------|
| `getUuid()` | string | 实例 UUID |
| `getDefinitionId()` | string | 定义 ID，如 `rolling_stock/locomotives/foo.json` |
| `getTag()` | string | IR 车辆 tag |
| `getTickCount()` | number | 实体存活 tick 数 |

---

## 速度：两种读法

> [!WARNING]
> 根级 **没有** `getSpeed()`。别和 `readout.getSpeed()` 搞混。

| 方法 | 单位 | 说明 |
|------|------|------|
| `getSpeedKmh()` | km/h | **实际速度** |
| `getSpeedMps()` | m/s | **实际速度** |
| `readout.getSpeed()` | 0~1 | 当前速度 / 最大速度 |

```javascript
var kmh = stock.getSpeedKmh();           // 120.5
var ratio = stock.readout.getSpeed();    // 0.6
```

写走行音时两种都行：

```javascript
// 方式 A：用 km/h 和 JSON 里的 max_speed_kmh
var ratio = stock.getSpeedKmh() / 200.0;

// 方式 B：直接用 readout（已按 IR 规则缩放）
var ratio = stock.readout.getSpeed();
```

---

## 子 API 入口

| 属性 | 章节 |
|------|------|
| `stock.control` | [机车控制](Control.md) |
| `stock.coupler` | [耦合器](Coupler.md) |
| `stock.readout` | [读数](Readout.md) |
| `stock.sound` | [音效](Sound.md) |
| `stock.animation` | [动画](Animation.md) |
| `stock.particle` | [烟雾与蒸汽](Particle.md) |
| `stock.cg_group` | [控制组](CgGroup.md) |

---

## 高级：getStock()

返回原始 IR `EntityRollingStock` Java 对象。一般不需要；只有你要调 IR 内部 API 时才用。
