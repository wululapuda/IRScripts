[EN](Particle-EN) | **中文**

[< 目录](Handbook-ZH)

---
# 烟雾与蒸汽

`stock.particle` — 在**列车模型坐标系**生成 IR 原生烟雾/蒸汽粒子。

坐标轴与 IR 模型一致，自动按轨距缩放。脚本在服务端调用，粒子在客户端渲染。

需在 IR 图形设置中开启粒子（`particlesEnabled`）。

---

## smoke — 黑烟

```javascript
stock.particle.smoke(start, offset, speed, time, concentration[, texture])
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `start` | `[x, y, z]` | 起始位置（模型坐标） |
| `offset` | `[x, y, z]` | 相对偏移（与 start 相加） |
| `speed` | number | 粒子运动速度 |
| `time` | number | 持续时间（**秒**） |
| `concentration` | number | 浓度 `0.0~1.0` |
| `texture` | string | 可选贴图路径 |

---

## steam — 蒸汽

```javascript
stock.particle.steam(start, offset, speed, time[, texture])
```

与 `smoke` 相同，但无 `concentration`，为浅色蒸汽。

未指定 `texture` 时使用车辆 JSON 的 `smokeParticleTexture` / `steamParticleTexture` 默认值。

---

## 坐标怎么理解

`start` 是效果锚点在模型上的位置；`offset` 是在此基础上的额外位移。  
最终发射点 = `start + offset`。

这和以前 `(x, y, z, …)` 单点写法不同——现在**显式区分**「锚点」和「偏移」，方便做多个烟囱、随速度漂移等效果。

```javascript
// 烟囱根部在 (0, 4.2, 0.5)，再向上偏 0.2
stock.particle.smoke([0, 4.2, 0.5], [0, 0.2, 0], 0.4, 3.0, 0.8);

// 自定义贴图
stock.particle.smoke(
    [0, 4.2, 0.5], [0, 0, 0],
    0.4, 2.0, 0.6,
    "mypack:textures/diesel_smoke.png"
);

// 蒸汽
stock.particle.steam([0, 3.8, -1.0], [0, 0.3, 0], 0.5, 2.5);
```

---

## 内燃机排烟示例

```javascript
function onTick() {
    if (!stock.control.isDiesel() || stock.control.getEngine() < 0.5) {
        return;
    }
    var throttle = stock.control.getThrottle();
    if (throttle > 0.3 && stock.getTickCount() % 4 === 0) {
        stock.particle.smoke(
            [0, 4.0, -2.0], [0, 0, 0],
            0.3 + throttle * 0.5,
            0.2,
            throttle
        );
    }
}
```

> **注意：**
> `time` 是秒，但效果持续期间**每 tick** 都会生成粒子，与 IR 原生排烟逻辑一致。别在 `LOOP-TICK` 里每 tick 都调很长 `time`，除非你真的想要浓密烟雾。