[EN](Coupler-EN) | **中文**

[< 目录](Handbook-ZH)

---
# 耦合器

`stock.coupler` — 控制与查询车钩状态。

---

## API

| 方法 | 读写 | 说明 |
|------|------|------|
| `isCoupleable()` | 只读 | 是否支持耦合器 |
| `setCouplerFront(value)` | 写 | 前钩：`≥0.5` 接合 |
| `setCouplerRear(value)` | 写 | 后钩：`≥0.5` 接合 |
| `getCouplerFront()` | 只读 | 前钩状态 `0/1` |
| `getCouplerRear()` | 只读 | 后钩状态 `0/1` |
| `getCoupledFront()` | 只读 | 前方是否已连挂 `0/1` |
| `getCoupledRear()` | 只读 | 后方是否已连挂 `0/1` |
| `getSlackFront()` | 只读 | 前松弛量比例 `0~1` |
| `getSlackRear()` | 只读 | 后松弛量比例 `0~1` |

---

## 示例

```javascript
function disconnectRear() {
    if (stock.coupler.isCoupleable()) {
        stock.coupler.setCouplerRear(0);
    }
}

function checkCoupling() {
    print("前连挂=" + stock.coupler.getCoupledFront()
        + " 松弛=" + stock.coupler.getSlackFront());
}
```

> **注意：**
> 连挂状态也可以用 `stock.readout.getCoupledFront()` 读取，两者一致。