[ZH](Stock-Basic-ZH) | **EN**

[< Contents](Handbook-EN)

---

# stock basics

| Method | Description |
|--------|-------------|
| `getUuid()` | Instance UUID |
| `getDefinitionId()` | Definition ID |
| `getTag()` | IR tag |
| `getTickCount()` | Entity age (ticks) |
| `getSpeedKmh()` | Actual speed km/h |
| `getSpeedMps()` | Actual speed m/s |

> **Warning**
> No `getSpeed()` at root — use `readout.getSpeed()` for 0~1 ratio.

Sub-APIs: [Control](Control-EN), [Coupler](Coupler-EN), [Readout](Readout-EN), [Sound](Sound-EN), [Animation](Animation-EN), [Particle](Particle-EN), [Cg-Group](Cg-Group-EN).
