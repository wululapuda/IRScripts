[ZH](Home-ZH) | **EN**

# IR Scripts Wiki

Welcome to the **IR Scripts** documentation.

IR Scripts adds JavaScript scripting to [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) rolling stock on **Minecraft 1.12.2**. Attach `.js` files to any stock JSON to control locomotives, read gauges, play sounds, add in-game buttons, query track geometry, and render custom models — no separate Forge mod required.

---

## What you can do

- Run scripts every tick (`LOOP-TICK`), once on spawn (`ONCE`), or from a GUI button (`BUTTON`)
- Control throttles, brakes, engines, and couplers
- Read IR gauge values (speed, boiler pressure, RPM, cargo, …)
- Play custom sounds, drive animations and particles
- Query track geometry and switch routing (`track` API)
- Load OBJ models and render along curves (`model` / `math` API)

Scripts run on the **server** (including single-player integrated server). Each stock instance has its own script runtime.

---

## Documentation

| Page | Description |
|------|-------------|
| [Handbook](Handbook-EN) | Step-by-step tutorial — start here |
| [Full index](Sidebar-EN) | All pages |
| [API reference](API-Reference-EN) | Configuration, all APIs, examples |
| [math API](Math-EN) | Vectors, curves, intersections |
| [model API](Model-EN) | OBJ loading, mesh transforms, rendering |
| [track API](Track-EN) | Track geometry, switch direction, script registry |

---

## 30-second example

**Stock JSON:**
```json
"scripts": [{
  "path": "mypack:scripts/my_loco.js",
  "functions": { "onTick": "LOOP-TICK", "horn": "BUTTON" }
}]
```

**Script:**
```javascript
function onTick() {
    if (stock.getSpeedKmh() > 60) stock.control.setTrainBrake(0.5);
}
function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

→ See [First script](First-Script-EN) for details.

---

## Requirements

| Item | Version |
|------|---------|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2864+ |
| Immersive Railroading | required |
| IR Scripts | 2.0.0 (`irscripts`) |
