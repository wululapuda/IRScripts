# IR Scripts Wiki

Welcome to the **IR Scripts** documentation.

IR Scripts adds JavaScript scripting to [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) rolling stock on **Minecraft 1.12.2**. Attach `.js` files to any stock JSON, control locomotives, read gauges, play sounds, and add in-game buttons — no separate Forge mod required.

---

## What you can do

- Run scripts on every tick (`LOOP`), once on spawn (`ONCE`), or from a GUI button (`BUTTON`)
- Control throttles, brakes, engines, and couplers
- Read IR gauge values (speed, boiler pressure, RPM, cargo, …)
- Play custom sounds at the stock position

Scripts run on the **server** (including single-player integrated server). Each stock instance has its own script runtime.

---

## Documentation

| Page | Description |
|------|-------------|
| **[Full API Reference](Wiki-EN)** | Complete guide — configuration, all APIs, examples |
| **[GitHub README](../README.md)** | Project overview, quick start, build instructions |

---

## 30-second example

**Stock JSON:**
```json
"scripts": [{
  "path": "mypack:scripts/my_loco.js",
  "functions": { "tick": "LOOP", "horn": "BUTTON" }
}]
```

**Script:**
```javascript
function tick() {
    if (stock.getSpeedKmh() > 60) stock.control.setTrainBrake(0.5);
}
function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

→ See **[Full API Reference](Wiki-EN)** for details.

---

## Requirements

- Minecraft **1.12.2**
- Forge **14.23.5.2864+**
- **Immersive Railroading**

---

[中文 Wiki 首页](Home-ZH)
