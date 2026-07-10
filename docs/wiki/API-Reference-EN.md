[ZH](API-Reference-ZH) | **EN**

[< Contents](Handbook-EN) · [Index](Sidebar-EN)

---

# IR Scripts API Reference (English)

> **Mod ID:** `irscripts` · **Version:** 2.0.0 · **Minecraft:** 1.12.2  
> **Engine:** Mozilla Rhino (bundled) · **Execution:** server-side only

For tutorials see [Handbook-EN](Handbook-EN). For extended APIs: [Math-EN](Math-EN), [Model-EN](Model-EN), [Track-EN](Track-EN).

---

## Quick start

```json
"scripts": [{
  "path": "mypack:scripts/my_train.js",
  "functions": {
    "onTick": "LOOP-TICK",
    "onSpawn": "ONCE",
    "playHorn": "BUTTON"
  }
}]
```

| Mode | When |
|------|------|
| `LOOP-TICK` | Every tick |
| `LOOP-SCRIPTS` | After previous run completes |
| `ONCE` | Once on spawn |
| `BUTTON` | GUI button click |

---

## Globals

| Name | Description |
|------|-------------|
| `stock` | Rolling stock APIs |
| `math` | Vectors, curves |
| `model` | OBJ, mesh, render |
| `track` | Track geometry, switch direction |
| `print(msg)` | Log output |
| `time` / `util` / `random` | Stdlib |

---

## stock (root)

`getUuid`, `getDefinitionId`, `getTag`, `getTickCount`, `getSpeedKmh`, `getSpeedMps`  
Sub-objects: `control`, `coupler`, `readout`, `sound`, `animation`, `particle`, `cg_group`

> No root `getSpeed()` — use `readout.getSpeed()` for 0~1 ratio.

---

## stock.control

`isLocomotive`, `isDiesel`, `isSteam`, `setEngine`, `setTrainBrake`, `setIndependentBrake`, `setThrottle`, `setReverser` + getters.  
Setters server-only; values 0~1 (switches 0/1).

---

## stock.coupler

`isCoupleable`, `setCouplerFront/Rear`, `getCoupledFront/Rear`, `getSlackFront/Rear`

---

## stock.readout (read-only)

`getSpeed`, `getLiquid`, `getCargoFill`, `getTemperature`, `getBoilerPressure`, `getEngineRpm`, `getBrakePressure`, `getCoupledFront/Rear`, `getHorn`, `getBell`, bogie angles, etc.

---

## stock.sound

`play(path, vol [, pitch [, repeat [, maxDist]]])` — non-blocking  
`utilPlay(...)` — pause script until done (continuations)  
`stopPlay([path])`

---

## stock.animation

`play(animFile, controlGroup, playMode, reverse, initialValue)`  
Modes: `VALUE`, `PLAY_FORWARD`, `PLAY_REVERSE`, `PLAY_BOTH`, `LOOP`, `LOOP_SPEED`  
`get(name)`, `list()`

---

## stock.particle

`smoke(start, offset, speed, time, concentration [, texture])`  
`steam(start, offset, speed, time [, texture])` — model coordinates

---

## stock.cg_group

`get(name)`, `set(name, value)` — 0~1

---

## math / model / track

See dedicated pages: [Math-EN](Math-EN), [Model-EN](Model-EN), [Track-EN](Track-EN).

---

## Debugging

| JVM flag | Effect |
|----------|--------|
| `-Dirscripts.debug=true` | Verbose logs |
| `-Dirscripts.scriptPrint=false` | Disable print |

Log prefixes: `[Bootstrap]`, `[Registry]`, `[Script|...]`, `[TrackBootstrap]`, `[TrackRegistry]`

---

## Limitations

- ES5 JavaScript recommended (Rhino)
- LOOP errors permanently disable that function per instance
- Track script registry = parse/query only (execution on placement TBD)
- BUTTON requires mod config + riding stock + inventory open

---

Full Chinese reference: [API-Reference-ZH](API-Reference-ZH)
