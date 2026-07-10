[ZH](Control-ZH) | **EN**

[< Contents](Handbook-EN)

---

# Locomotive control

`stock.control` — server-side setters only.

| Method | Description |
|--------|-------------|
| `isLocomotive()` / `isDiesel()` / `isSteam()` | Type checks |
| `setEngine(value)` | Diesel: ≥0.5 on |
| `setTrainBrake(value)` | 0~1 |
| `setIndependentBrake(value)` | 0~1 |
| `setThrottle(value)` | 0~1 |
| `setReverser(value)` | Diesel: -1/0/1; steam: 0~1 |

```javascript
function startDiesel() {
    if (!stock.control.isDiesel()) return;
    stock.control.setEngine(1);
    stock.control.setThrottle(0.3);
    stock.control.setReverser(1);
}
```
