[ZH](Install-ZH) | **EN**

[< Contents](Handbook-EN)

---

# Install & dependencies

## Requirements

| Item | Version |
|------|---------|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2864+ |
| Immersive Railroading | required |
| IR Scripts | 2.0.0 (`irscripts`) |

Place `irscripts-2.0.jar` in your `mods` folder.

> **Note**
> Rhino is bundled — no JRE Nashorn required.

---

## Configuration

`config/irscripts.cfg` — script print, debug logging, show BUTTON buttons.  
Also configurable in IR config GUI → **IR Scripts** page.

JVM: `-Dirscripts.debug=true`, `-Dirscripts.scriptPrint=false`

---

## Script files

```
assets/mypack/scripts/my_train.js
```

JSON: `"path": "mypack:scripts/my_train.js"`

---

## Register in stock JSON

```json
"scripts": [{
  "path": "mypack:scripts/crh1a.js",
  "functions": {
    "onTick": "LOOP-TICK",
    "onSpawn": "ONCE",
    "openDoor": "BUTTON"
  }
}]
```

> **Warning**
> Function names must exactly match `function xxx()` in the `.js` file.
