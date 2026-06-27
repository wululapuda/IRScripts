# IR Scripts

**JavaScript scripting for Immersive Railroading rolling stock on Minecraft 1.12.2.**

IR Scripts lets pack makers attach Nashorn JavaScript to any IR stock definition — control locomotives, read gauges, play sounds, and expose in-game buttons — without writing a separate Forge mod.

[中文介绍](#中文介绍) · [Wiki (EN)](docs/Wiki-EN.md) · [Wiki (中文)](docs/Wiki-ZH.md)

---

## Features

- **Per-instance script runtime** — each spawned stock gets its own isolated Nashorn engine
- **Three execution modes** — `LOOP` (every tick), `ONCE` (on spawn), `BUTTON` (player-triggered GUI)
- **Locomotive control** — engine, throttle, brakes, reverser (diesel & steam)
- **Coupler control** — engage/disengage front and rear couplers
- **Readout API** — speed, boiler pressure, RPM, cargo, coupler state, and more (mirrors IR GUI readouts)
- **Sound API** — play `.ogg` files with volume, pitch, and loop support (IR-compatible paths)
- **Server-authoritative** — scripts run on the server; safe for multiplayer

---

## Requirements

| | |
|---|---|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2864+ |
| [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) | Required |
| Java | 8 (for building) |

---

## Quick Start

### 1. Add scripts to your stock JSON

```json
{
  "name": "My Locomotive",
  "scripts": [{
    "path": "mypack:scripts/my_loco.js",
    "functions": {
      "mainLoop": "LOOP",
      "init": "ONCE",
      "horn": "BUTTON"
    }
  }]
}
```

### 2. Write the script

Place `my_loco.js` at `assets/mypack/scripts/my_loco.js`:

```javascript
function init() {
    print("Spawned: " + stock.getDefinitionId());
}

function mainLoop() {
    if (stock.getSpeedKmh() > 80 && stock.control.isLocomotive()) {
        stock.control.setTrainBrake(1.0);
    }
}

function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

### 3. Drop IR Scripts into your modpack

Install the `irscripts` jar alongside Immersive Railroading. Scripts load automatically when the stock spawns.

---

## Script Modes

| Mode | When it runs |
|------|----------------|
| `LOOP` | Every game tick after the stock instance is created |
| `ONCE` | Once, when the stock instance is created |
| `BUTTON` | When the player clicks a button in the stock GUI (top-left panel) |

BUTTON buttons appear when the player is riding the stock and opens the IR inventory or vanilla inventory (`E`).

---

## API Overview

All APIs are accessed through the global `stock` object in your scripts.

```javascript
stock.getUuid()              // instance UUID
stock.getDefinitionId()      // e.g. "mypack:my_loco"
stock.getSpeedKmh()          // actual speed in km/h
stock.getTickCount()         // entity age in ticks

stock.control.setThrottle(0.5)
stock.control.setTrainBrake(1.0)
stock.control.isDiesel()

stock.coupler.setCouplerFront(1)
stock.coupler.getCoupledRear()

stock.readout.getBoilerPressure()
stock.readout.getEngineRpm()
stock.readout.getSpeed()

stock.sound.play("sounds/horn_1.ogg", 0.8)
stock.sound.play("sounds/loop.ogg", 0.5, 1.2, true)  // volume, pitch, repeat
```

See the full API reference in [docs/Wiki-EN.md](docs/Wiki-EN.md) or [docs/Wiki-ZH.md](docs/Wiki-ZH.md).

---

## Single-Player & Multiplayer

Scripts always run on the **server logical side**, never on the client — even in single-player.

| | Single-player | Multiplayer |
|---|---------------|-------------|
| Where scripts run | Local integrated server | Dedicated / LAN server |
| Works out of the box | Yes | Yes (server-side mod) |

In single-player you do not need to host a separate server; the local integrated server executes all script logic.

---

## Building from Source

```bash
git clone https://github.com/<your-username>/irscripts.git
cd irscripts
```

Place IR dependencies in `libs/`:

- `ir.jar`
- `UniversalModCore-1.12.2-forge-1.2.1.jar`
- `TrackAPI-1.2.jar`
- `mixinbooter-10.7.jar`

Then build with **Java 8**:

```bash
./gradlew build
```

Output jar: `build/libs/irscripts-1.0.jar`

---

## Project Structure

```
src/main/java/cn/wululapuda/irscripts/
├── api/          # stock.control, coupler, readout, sound
├── script/       # Nashorn runtime, registry, bindings
├── net/          # button click & sound packets
├── gui/          # BUTTON overlay
└── mixin/        # IR hooks (definition load, tick, GUI)
```

---

## License

This project is licensed under the same terms as its Forge MDK base — see [LICENSE.txt](LICENSE.txt).

Immersive Railroading is developed by [TeamOpenIndustry](https://github.com/TeamOpenIndustry/ImmersiveRailroading) and is not affiliated with this mod.

---

## Contributing

Issues and pull requests are welcome. For API questions, check the [Wiki](docs/Wiki-EN.md) first.

---

## 中文介绍

**IR Scripts** 是为 Minecraft 1.12.2 版 Immersive Railroading (IR) 车辆提供 JavaScript 脚本支持的附属模组。

### 主要功能

- 每个列车实例独立脚本运行时（Nashorn）
- 三种模式：`LOOP`（每 tick）、`ONCE`（生成时一次）、`BUTTON`（GUI 按钮触发）
- 机车控制：引擎、节流、制动、换向
- 耦合器控制与状态读取
- 读数 API：速度、锅炉压力、RPM、货物等
- 音效播放（支持 IR 路径规则）
- 仅服务端执行，支持多人游戏

### 快速示例

```json
"scripts": [{
  "path": "mypack:scripts/my_loco.js",
  "functions": {
    "mainLoop": "LOOP",
    "init": "ONCE",
    "horn": "BUTTON"
  }
}]
```

```javascript
function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

完整 API 文档：[(https://github.com/wululapuda/IRScripts/wiki)](https://github.com/wululapuda/IRScripts/wiki)

---

<p align="center">
  Made for the Immersive Railroading community
</p>
