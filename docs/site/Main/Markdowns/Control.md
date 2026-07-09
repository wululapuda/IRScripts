# 机车控制

`stock.control` — 读写机车上的引擎、制动、节流、换向。

> [!NOTE]
> 所有 **setter 仅在服务端生效**。在非对应车型上调用会写警告日志并忽略，**不会抛异常**。

---

## 车型判断

| 方法 | 说明 |
|------|------|
| `getType()` | `"diesel"` / `"steam"` / `"locomotive"` / `"none"` |
| `isLocomotive()` | 是否为机车 |
| `isDiesel()` | 内燃机车 |
| `isSteam()` | 蒸汽机车 |

```javascript
if (!stock.control.isLocomotive()) {
    return; // 车厢上别设节流
}
```

---

## 引擎（内燃）

| 方法 | 说明 |
|------|------|
| `setEngine(value)` | `≥0.5` 启动，`<0.5` 熄火 |
| `getEngine()` | `0` 或 `1` |

---

## 制动

| 方法 | 说明 |
|------|------|
| `setTrainBrake(value)` | 列车制动 `0.0~1.0` |
| `getTrainBrake()` | 当前值 |
| `setIndependentBrake(value)` | 独立制动 `0.0~1.0` |
| `getIndependentBrake()` | 当前值 |

---

## 节流

| 方法 | 说明 |
|------|------|
| `setThrottle(value)` | `0.0~1.0` |
| `getThrottle()` | 当前值 |

---

## 换向器

| 车型 | setReverser | getReverser |
|------|-------------|-------------|
| 内燃 | `≤-0.5` 后退；`≥0.5` 前进；中间空档 | `-1` / `0` / `1` |
| 蒸汽 | `0.0~1.0` 线性 | `0.0~1.0` |
| 其他机车 | `-1.0~1.0` | 原始值 |

```javascript
function startDiesel() {
    if (!stock.control.isDiesel()) return;
    stock.control.setEngine(1);
    stock.control.setThrottle(0.3);
    stock.control.setReverser(1);  // 前进
}
```

---

## 自动制动示例

```javascript
function onTick() {
    if (!stock.control.isLocomotive()) return;

    var kmh = stock.getSpeedKmh();
    if (kmh > 80) {
        stock.control.setTrainBrake(
            util.clamp((kmh - 80) / 20, 0, 1)
        );
    } else if (kmh < 5) {
        stock.control.setTrainBrake(0);
    }
}
```
