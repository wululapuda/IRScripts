[EN](Readout-EN) | **中文**

[< 目录](Handbook-ZH)

---
# 读数 readout

`stock.readout` — 与 IR 游戏内 GUI 仪表一致的**只读**数值。

全部方法**只读**，适用于任意车辆类型（机车、动车、货车）。

---

## 常用读数

| 方法 | 说明 |
|------|------|
| `getSpeed()` | 当前速度 / 最大速度，**0.0~1.0** |
| `getLiquid()` | 液体装载百分比；不可装液体时为 `0` |
| `getCargoFill()` | 货物装载百分比 |
| `getTemperature()` | 蒸汽：温度/100；内燃：温度/150 |
| `getBoilerPressure()` | 蒸汽锅炉压力 / JSON `maxPSI` |
| `getEngineRpm()` | 内燃 RPM（相对设定有延迟） |
| `getTrainBrakeLever()` | 全列刹车杆位置 |
| `getBrakePressure()` | 整列制动压力 |

---

## 耦合与转向架

| 方法 | 说明 |
|------|------|
| `getCoupledFront()` / `getCoupledRear()` | 前/后方是否连挂 `0/1` |
| `getCouplerSlackFront()` / `getCouplerSlackRear()` | 耦合器松弛量比例 |
| `getFrontBogeyAngle()` / `getRearBogeyAngle()` | 转向架偏转角 `0~1` |
| `getFrontLocomotiveAngle()` / `getRearLocomotiveAngle()` | 机车架偏转角 `0~1` |

---

## 铃声与汽笛

| 方法 | 说明 |
|------|------|
| `getBell()` | 铃是否响 `0/1` |
| `getHorn()` / `getWhistle()` | 汽笛/喇叭是否响 `0/1` |
| `getCylinderDrain()` | 蒸汽汽缸排水阀 `0/1` |

---

## 示例

```javascript
function monitor() {
    var r = stock.readout;
    print("ratio=" + r.getSpeed()
        + " kmh=" + stock.getSpeedKmh()
        + " boiler=" + r.getBoilerPressure()
        + " rpm=" + r.getEngineRpm()
        + " horn=" + r.getHorn());
}
```

> **注意：**
> 这些读数和 [原野手册 · Animatrix](https://goldenfield192.github.io/#/Main/Markdowns/Animatrix) 里的 READOUT 名称一一对应。写动画脚本时两边概念互通。