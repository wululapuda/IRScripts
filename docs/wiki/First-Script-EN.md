[ZH](First-Script-ZH) | **EN**

[< Contents](Handbook-EN)

---

# First script

## Goals

Log on spawn, auto-brake above 60 km/h, horn on button.

## Script

`assets/mypack/scripts/my_train.js`:

```javascript
function onSpawn() {
    print("Spawned: " + stock.getDefinitionId());
}

function onTick() {
    var kmh = stock.getSpeedKmh();
    if (kmh > 60 && stock.control.isLocomotive()) {
        stock.control.setTrainBrake(0.5);
    }
}

function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

## JSON

```json
"scripts": [{
  "path": "mypack:scripts/my_train.js",
  "functions": {
    "onSpawn": "ONCE",
    "onTick": "LOOP-TICK",
    "playHorn": "BUTTON"
  }
}]
```

Next: [Execution modes](Script-Modes-EN)
