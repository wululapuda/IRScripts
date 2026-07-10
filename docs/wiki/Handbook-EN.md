[ZH](Handbook-ZH) | **EN**

[鈫?Contents](Handbook-EN)

---
# IR Scripts Handbook

Hello! This is the **IR Scripts** user guide.

IR Scripts adds **JavaScript scripting** to [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) rolling stock. You do not need a separate Forge mod 鈥?attach `.js` files in stock JSON to control locomotives, read gauges, play sounds, drive animations, and add GUI buttons.

Before you start:

- You have played Immersive Railroading and know basic IR concepts
- You can write **JSON** and know where stock definitions live
- You know basic **JavaScript** (`var`, functions, `if` is enough)
- You know `.ogg` sound and `.anim` animation paths in resource packs
- You can read `latest.log`

> **Note**
> This guide targets **IR Scripts 2.0.0** on **Minecraft 1.12.2** (Forge). Scripts run on the **server** only; single-player uses the integrated server.

> **Tip**
> If you are building IR resource packs, also read [Golden Field's IR handbook](https://goldenfield192.github.io/) for JSON, models, and Animatrix. This handbook covers **how to write scripts**.

---

## What you can do

| Capability | Summary |
|----------|---------|
| Per-tick logic | Speed-based run sounds, auto braking, smoke |
| Spawn init | Start engine when stock is placed |
| GUI buttons | Horn, open doors on player click |
| Read gauges | Speed, boiler pressure, RPM, cargo fill鈥?|
| Write controls | Throttle, brakes, couplers, control groups |
| Custom sounds | Volume, pitch, loop, max distance |
| Animations | Play `.anim`, open doors |
| Particles | Smoke and steam |
| Track queries | Geometry, switch direction, script registry |
| Custom models | Load OBJ, transform meshes, render along curves |

---

## 30-second overview

**Stock JSON** (root level, alongside `properties`):

```json
{
  "name": "My Train",
  "max_speed_kmh": 200,
  "scripts": [{
    "path": "mypack:scripts/my_train.js",
    "functions": {
      "onTick": "LOOP-TICK",
      "onSpawn": "ONCE",
      "playHorn": "BUTTON"
    }
  }]
}
```

**Script** `assets/mypack/scripts/my_train.js`:

```javascript
function onSpawn() {
    print("Spawned: " + stock.getDefinitionId());
}

function onTick() {
    if (stock.getSpeedKmh() > 80) {
        stock.control.setTrainBrake(0.5);
    }
}

function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

---

## Recommended reading order

1. [Install & dependencies](Install-EN)
2. [First script](First-Script-EN)
3. [Execution modes](Script-Modes-EN)
4. Browse [API chapters](Stock-Basic-EN) as needed

Let's begin.