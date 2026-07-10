# Generates English wiki pages from embedded content.
$ErrorActionPreference = "Stop"
$outDir = $PSScriptRoot

function Write-Page([string]$name, [string]$slug, [string]$body) {
    $header = @"
[ZH]($slug-ZH) | **EN**

[< Contents](Handbook-EN)

---

"@
    Set-Content (Join-Path $outDir "$slug-EN.md") ($header + $body) -Encoding UTF8 -NoNewline
    Write-Host "Wrote $slug-EN.md"
}

Write-Page "Handbook" "Handbook" @'
# IR Scripts Handbook

Hello! This is the **IR Scripts** user guide.

IR Scripts adds **JavaScript scripting** to [Immersive Railroading](https://github.com/TeamOpenIndustry/ImmersiveRailroading) rolling stock. You do not need a separate Forge mod — attach `.js` files in stock JSON to control locomotives, read gauges, play sounds, drive animations, and add GUI buttons.

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
| Read gauges | Speed, boiler pressure, RPM, cargo fill… |
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
'@

# Tutorial pages — abbreviated but complete EN versions
$pages = @{
"QaA-EN" = @'
# Q&A

Common questions before you write your first script.

---

## How does IR Scripts relate to IR JSON?

IR JSON defines **static** behavior: models, physics, default sounds, Animatrix, control groups…  
IR Scripts adds **dynamic logic**: call APIs at runtime based on speed, buttons, time, etc.

Think of scripts as a small program on the stock; JSON is the specification sheet.

---

## Do I need Java?

No. Scripts use **JavaScript** with Mozilla Rhino (bundled in the mod).

- Use `var` and `function` declarations
- Stick to **ES5** syntax
- No `import` — stdlib globals are injected automatically

---

## Client or server?

**Always server.**

| Side | Role |
|------|------|
| Server | Runs JS, calls control/sound APIs |
| Client | Plays sounds, draws BUTTON UI, renders particles |

Single-player = integrated server; same rules apply.

---

## One engine per stock or per train?

**Each rolling stock entity instance** has its own JS engine.

An 8-car EMU with scripts on every car runs 8 independent copies.

---

## Full API list?

This handbook covers all APIs chapter by chapter.  
See also [API quick reference](API-Reference-EN).
'@

"Install-EN" = @'
# Install & dependencies

## Requirements

| Item | Version |
|------|---------|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2864+ |
| Immersive Railroading | required |
| UniversalModCore | IR dependency |
| TrackAPI | IR dependency |
| IR Scripts | 2.0.0 (`irscripts`) |

Place `irscripts-2.0.jar` in your `mods` folder.

> **Note**
> Rhino is bundled inside the mod — no JRE Nashorn required.

---

## Mod configuration

Config file: `config/irscripts.cfg`

| Option | Description |
|--------|-------------|
| Script `print()` output | Write to server log |
| Debug logging | Verbose debug messages |
| Show script buttons | Draw BUTTON-mode buttons in GUI |

You can also open the **IR config GUI** (default numpad `/`) and find the **IR Scripts** page.

JVM overrides:

```
-Dirscripts.debug=true
-Dirscripts.scriptPrint=false
```

---

## Where to put scripts

Scripts live in **your resource pack**, matching the `path` in JSON:

```
your_pack.zip
└── assets/
    └── mypack/
        ├── scripts/
        │   └── my_train.js
        └── sounds/
            └── horn_1.ogg
```

JSON:

```json
"path": "mypack:scripts/my_train.js"
```

Domain `mypack` maps to `assets/mypack/`.

---

## Register in stock JSON

Add `scripts` at the **root** of the stock definition:

```json
{
  "name": "CRH1A",
  "max_speed_kmh": 250,
  "scripts": [{
    "path": "mypack:scripts/crh1a.js",
    "functions": {
      "onTick": "LOOP-TICK",
      "onSpawn": "ONCE",
      "openDoor": "BUTTON"
    }
  }]
}
```

| Field | Description |
|-------|-------------|
| `path` | Script resource path (IR Identifier) |
| `functions` | Key = JS function name, value = execution mode |

Multiple `scripts` entries per stock are allowed.

> **Warning**
> Function names must **exactly** match `function xxx()` in the `.js` file (case-sensitive).
'@

"Problems-EN" = @'
# FAQ

---

## LOOP stops after an error

**Cause:** Uncaught exceptions in `LOOP-TICK` / `LOOP-SCRIPTS` permanently disable that function for the instance.

**Fix:** Fix the script and place a new stock (new instance reloads scripts). Check the first error in `latest.log`.

---

## Changed .js but nothing updates

- Re-pack the resource zip
- Restart or reload IR definitions
- Existing instances do not hot-reload — remove and re-place the stock

---

## getSpeed() does not exist

Use `stock.getSpeedKmh()` or `stock.readout.getSpeed()`.  
There is no root-level `getSpeed()`.

---

## let / const / arrow functions?

Rhino engine — use **ES5**: `var` + `function`. Some ES6 may work but is not guaranteed.

---

## Multiple script files per stock?

`scripts` is an array; multiple `.js` files with separate function tables are supported.

---

## Client-side scripts?

No. All JS runs on the server. The client handles UI and sound/particle rendering only.

---

## Conflict with IR JSON `sounds` block?

No conflict. JSON `sounds` = IR native event sounds; `stock.sound` = runtime dynamic playback. See [Sound](Sound-EN).

---

## Sound keeps playing after stock removed

2.0 stops sounds and clears continuations on destroy. If it persists, update to the latest jar and report logs.

---

## Particles invisible

- Enable particles in IR graphics settings
- Try coordinates like `[0, 4, 0]` first
- Call from server only

---

## Learn IR resource packs

See [Golden Field's IR handbook](https://goldenfield192.github.io/) for models, JSON, Animatrix, native sounds.  
This wiki covers **IR Scripts API** only.
'@

"First-Script-EN" = @'
# First script

We write a working script from scratch.

---

## Goals

- Print a log line on spawn
- Auto brake above 60 km/h
- Horn on button click

---

## Step 1: Sound file

Place `horn_1.ogg` at:

```
assets/mypack/sounds/horn_1.ogg
```

---

## Step 2: Script

`assets/mypack/scripts/my_train.js`:

```javascript
function onSpawn() {
    print("Stock spawned: " + stock.getDefinitionId());
    print("UUID: " + stock.getUuid());
}

function onTick() {
    var kmh = stock.getSpeedKmh();

    if (stock.getTickCount() % 100 === 0) {
        print("Speed " + kmh + " km/h");
    }

    if (kmh > 60 && stock.control.isLocomotive()) {
        stock.control.setTrainBrake(0.5);
    }
}

function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

---

## Step 3: Attach to JSON

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

---

## Step 4: Verify in-game

1. Reload resources / restart
2. Place or spawn the stock
3. Check `latest.log` for `[Script|...]` print output
4. Accelerate past 60 km/h — brakes should engage
5. Ride the stock, open IR inventory or press `E`, click **playHorn**

> **Tip**
> If `print` shows nothing, check `config/irscripts.cfg` for script print output.

---

## Global objects to know

| Name | What |
|------|------|
| `stock` | Current stock instance — all API entry point |
| `print(msg)` | Write log |
| `time` | Time, non-blocking sleep |
| `util` | clamp, lerp, etc. |
| `random` | Random numbers |
| `math` | Vectors, curves |
| `model` | OBJ loading & rendering |
| `track` | Track geometry queries |

Next: [Execution modes](Script-Modes-EN)
'@

"Script-Modes-EN" = @'
# Execution modes

Each function in `functions` must specify **when** it is called.

---

## Mode overview

| JSON value | Name | When called |
|------------|------|-------------|
| `LOOP-TICK` | Per-tick loop | **Every game tick** (20/sec) |
| `LOOP-SCRIPTS` | Script loop | Only after previous run **fully completes** |
| `ONCE` | One-shot | Once when entity is created |
| `BUTTON` | Button | When player clicks GUI button |

Legacy `"LOOP"` equals `LOOP-TICK`.

---

## LOOP-TICK

For run sounds, continuous monitoring, per-frame updates.

```json
"functions": { "sounds": "LOOP-TICK" }
```

> **Warning**
> Keep the function lightweight. Heavy work every tick or frequent looping `play` calls cost CPU.  
> Use state checks — only `play` when volume/pitch changes (see [Examples](Examples-EN)).

---

## LOOP-SCRIPTS

For sequential logic with `utilPlay` / `time.sleep`.

```javascript
function sequence() {
    stock.sound.utilPlay("sounds/horn_1.ogg", 1.0);
    time.sleep(1.0);
    stock.sound.play("sounds/bell.ogg", 0.8);
}
```

`utilPlay` and `time.sleep` pause the **current function** but do **not** freeze the world — Rhino continuations resume on later ticks.

---

## ONCE

For initialization on spawn.

---

## BUTTON

For manual player actions (horn, doors, lights). See [BUTTON](Button-EN).

---

## Errors

`LOOP-TICK` / `LOOP-SCRIPTS` uncaught errors **permanently disable** that function for the instance (logged once).  
`ONCE` / `BUTTON` failures affect only the current invocation.

---

## Which mode?

| Goal | Mode |
|------|------|
| Run sounds, speed-linked logic | `LOOP-TICK` |
| Horn → wait → play more | `LOOP-SCRIPTS` |
| Init on spawn | `ONCE` |
| Player manual action | `BUTTON` |
'@

"Globals-EN" = @'
# Global objects

These objects exist **automatically** in every script — no `import`.

---

## stock

Current rolling stock entity. All APIs hang off it:

```
stock
├── getUuid() / getDefinitionId() / getSpeedKmh() …
├── control      locomotive control
├── coupler      couplers
├── readout      gauge readouts
├── sound        sounds
├── animation    animations
├── particle     smoke/steam
└── cg_group     control groups
```

`stock` always points to the stock that triggered the current function.

---

## math

Vector math, arrays, Bézier curves. See [math API](Math-EN).

---

## model

OBJ model loading, mesh transforms, rendering. See [model API](Model-EN).

---

## track

Track geometry and switch direction queries. See [track API](Track-EN).

---

## print(message)

Writes to the game log:

```
[Script|<uuid8>|<script path>] <message>
```

Disable: `-Dirscripts.scriptPrint=false` or mod config.

---

## time / util / random

Standard library — see [Stdlib](Stdlib-EN).

---

## Not available

- No `require` / `import`
- No `console.log` (use `print`)
- No DOM / browser APIs
- No multithreading (single-threaded, server tick driven)

> **Note**
> Engine is Rhino — **ES5** (`var` + `function`) is safest.
'@

"Stdlib-EN" = @'
# Standard library

Three global modules are injected before your script runs. Style inspired by Python stdlib.

---

## time

| Method | Description |
|--------|-------------|
| `time.time()` | Unix timestamp (seconds) |
| `time.monotonic()` | Monotonic clock for intervals |
| `time.sleep(seconds)` | **Non-blocking** script pause |
| `time.localtime([secs])` | Local time struct |
| `time.strftime(fmt, st)` | Format time |
| `time.world_tick()` | World total tick |
| `time.stock_tick()` | Stock entity tick |
| `time.ticks_to_seconds(t)` | tick → seconds |
| `time.seconds_to_ticks(s)` | seconds → tick |
| `time.TICKS_PER_SECOND` | `20` |

`sleep` does not freeze the world — uses Rhino continuations.

---

## util

| Method | Description |
|--------|-------------|
| `util.clamp(x, min, max)` | Clamp to range |
| `util.lerp(a, b, t)` | Linear interpolation |
| `util.inverseLerp(a, b, v)` | Inverse lerp → 0~1 |
| `util.mapRange(v, inMin, inMax, outMin, outMax)` | Range mapping |
| `util.sign(x)` | Sign -1 / 0 / 1 |
| `util.approximately(a, b[, eps])` | Float equality |
| `util.mod(x, m)` | Positive modulo |
| `util.degToRad` / `util.radToDeg` | Angle conversion |

---

## random

| Method | Description |
|--------|-------------|
| `random.random()` | [0, 1) float |
| `random.uniform(a, b)` | Uniform distribution |
| `random.randint(a, b)` | Inclusive integer |
| `random.choice(arr)` | Random element |
| `random.shuffle(arr)` | In-place shuffle |
| `random.seed(n)` | Fixed seed |
'@

"Stock-Basic-EN" = @'
# stock basics

Methods and properties on the root `stock` object.

---

## Identity & state

| Method | Returns | Description |
|--------|---------|-------------|
| `getUuid()` | string | Instance UUID |
| `getDefinitionId()` | string | Definition ID |
| `getTag()` | string | IR stock tag |
| `getTickCount()` | number | Entity age in ticks |

---

## Speed: two ways to read

> **Warning**
> No root-level `getSpeed()`. Do not confuse with `readout.getSpeed()`.

| Method | Unit | Description |
|--------|------|-------------|
| `getSpeedKmh()` | km/h | **Actual speed** |
| `getSpeedMps()` | m/s | **Actual speed** |
| `readout.getSpeed()` | 0~1 | Current / max speed ratio |

---

## Sub-API entry points

| Property | Chapter |
|----------|---------|
| `stock.control` | [Control](Control-EN) |
| `stock.coupler` | [Coupler](Coupler-EN) |
| `stock.readout` | [Readout](Readout-EN) |
| `stock.sound` | [Sound](Sound-EN) |
| `stock.animation` | [Animation](Animation-EN) |
| `stock.particle` | [Particle](Particle-EN) |
| `stock.cg_group` | [Cg-Group](Cg-Group-EN) |

---

## Advanced: getStock()

Returns the raw IR `EntityRollingStock` Java object. Rarely needed.
'@

"Control-EN" = @'
# Locomotive control

`stock.control` — read/write engine, brakes, throttle, reverser.

> **Note**
> All **setters** run on server only. Wrong stock type → warning log, no exception.

---

## Type checks

| Method | Description |
|--------|-------------|
| `getType()` | `"diesel"` / `"steam"` / `"locomotive"` / `"none"` |
| `isLocomotive()` | Is locomotive |
| `isDiesel()` | Diesel locomotive |
| `isSteam()` | Steam locomotive |

---

## Engine (diesel)

| Method | Description |
|--------|-------------|
| `setEngine(value)` | `≥0.5` on, `<0.5` off |
| `getEngine()` | `0` or `1` |

---

## Brakes

| Method | Description |
|--------|-------------|
| `setTrainBrake(value)` | Train brake 0.0~1.0 |
| `getTrainBrake()` | Current value |
| `setIndependentBrake(value)` | Independent brake 0.0~1.0 |
| `getIndependentBrake()` | Current value |

---

## Throttle

| Method | Description |
|--------|-------------|
| `setThrottle(value)` | 0.0~1.0 |
| `getThrottle()` | Current value |

---

## Reverser

| Type | setReverser | getReverser |
|------|-------------|-------------|
| Diesel | `≤-0.5` reverse; `≥0.5` forward; middle = neutral | `-1` / `0` / `1` |
| Steam | 0.0~1.0 linear | 0.0~1.0 |
| Other | -1.0~1.0 | raw value |
'@

"Readout-EN" = @'
# Readout

`stock.readout` — **read-only** values matching IR in-game gauges.

Works on any stock type (locomotive, EMU, freight).

---

## Common readouts

| Method | Description |
|--------|-------------|
| `getSpeed()` | Current / max speed **0.0~1.0** |
| `getLiquid()` | Liquid fill %; `0` if N/A |
| `getCargoFill()` | Cargo fill % |
| `getTemperature()` | Steam: temp/100; diesel: temp/150 |
| `getBoilerPressure()` | Steam boiler / JSON `maxPSI` |
| `getEngineRpm()` | Diesel RPM (lagged) |
| `getTrainBrakeLever()` | Train brake lever position |
| `getBrakePressure()` | Train brake pressure |

---

## Coupling & bogies

| Method | Description |
|--------|-------------|
| `getCoupledFront()` / `getCoupledRear()` | Coupled 0/1 |
| `getCouplerSlackFront()` / `getCouplerSlackRear()` | Slack ratio |
| `getFrontBogeyAngle()` / `getRearBogeyAngle()` | Bogie angle 0~1 |
| `getFrontLocomotiveAngle()` / `getRearLocomotiveAngle()` | Loco frame angle 0~1 |

---

## Bell & horn

| Method | Description |
|--------|-------------|
| `getBell()` | Bell ringing 0/1 |
| `getHorn()` / `getWhistle()` | Horn 0/1 |
| `getCylinderDrain()` | Steam cylinder drain 0/1 |
'@

"Coupler-EN" = @'
# Coupler

`stock.coupler` — control and query coupler state.

---

## API

| Method | R/W | Description |
|--------|-----|-------------|
| `isCoupleable()` | R | Supports couplers |
| `setCouplerFront(value)` | W | Front: `≥0.5` couple |
| `setCouplerRear(value)` | W | Rear: `≥0.5` couple |
| `getCouplerFront()` | R | Front state 0/1 |
| `getCouplerRear()` | R | Rear state 0/1 |
| `getCoupledFront()` | R | Front coupled 0/1 |
| `getCoupledRear()` | R | Rear coupled 0/1 |
| `getSlackFront()` | R | Front slack 0~1 |
| `getSlackRear()` | R | Rear slack 0~1 |

> **Note**
> Coupling state is also available via `stock.readout.getCoupledFront()`.
'@

"Cg-Group-EN" = @'
# Control groups

`stock.cg_group` — read/write IR **control groups** from stock JSON / models.

Value range **0.0 ~ 1.0**.

---

## API

| Method | Description |
|--------|-------------|
| `get(name)` | Read current value |
| `set(name, value)` | Set value (clamped 0~1) |

`name` comes from widget `control_group` in stock JSON.

> **Tip**
> For full open/close animations, prefer [stock.animation.play](Animation-EN) with `PLAY_BOTH` and `reverse`.
'@

"Sound-EN" = @'
# Sound

`stock.sound` — play custom `.ogg` at stock position.

**Server only**; client syncs via packets.

---

## play — non-blocking

```javascript
stock.sound.play(path, volume)
stock.sound.play(path, volume, pitch)
stock.sound.play(path, volume, pitch, repeat)
stock.sound.play(path, volume, pitch, repeat, maxDistance)
```

Returns immediately — does not wait for playback to finish.

---

## Path rules

| Input | Resolved |
|-------|----------|
| `sounds/horn_1.ogg` | `<pack domain>:sounds/horn_1.ogg` |
| `horn_1` | `sounds/horn_1.ogg` |
| `mypack:sounds/custom.ogg` | Full Identifier |

Domain: from `definitionId` if it contains `:`, else `immersiverailroading`.

---

## utilPlay — wait for playback

Same parameters as `play`, but **pauses the script function** until audio finishes (estimated from Ogg duration).

Does not block the server thread — uses continuations. Best with `LOOP-SCRIPTS`.

---

## stopPlay

```javascript
stock.sound.stopPlay();
stock.sound.stopPlay("sounds/run.ogg");
```

---

## Run sounds

> **Warning**
> Calling `play(..., true)` every tick stacks layers. Track state and only re-play when volume/pitch changes. See [Examples](Examples-EN).
'@

"Animation-EN" = @'
# Animation

`stock.animation` — play IR **`.anim`** files registered in stock JSON.

---

## play

```javascript
stock.animation.play(animFile, controlOrReadout, playMode, reverse, initialValue)
```

| Param | Type | Description |
|-------|------|-------------|
| `animFile` | string | Animatrix path; `""` = lookup by control group only |
| `controlOrReadout` | string | `control_group` name from JSON |
| `playMode` | string | See table below |
| `reverse` | boolean | Reverse direction (e.g. close door) |
| `initialValue` | number | Initial value 0.0~1.0 |

### playMode

| Value | Meaning |
|-------|---------|
| `VALUE` | Direct scrub |
| `PLAY_FORWARD` | Play forward when ≥ 0.95 |
| `PLAY_REVERSE` | Play reverse when < 0.95 |
| `PLAY_BOTH` | Both directions (doors) |
| `LOOP` | Loop when > 0.95 |
| `LOOP_SPEED` | Loop speed follows variable |

---

## Helpers

| Method | Description |
|--------|-------------|
| `get(name)` | Read control group value |
| `list()` | List animation control groups |

> **Note**
> Readout-driven animations cannot be triggered from scripts — use `control_group`.
'@

"Particle-EN" = @'
# Smoke & steam

`stock.particle` — IR native smoke/steam particles in **stock model coordinates**.

Server calls; client renders. Enable particles in IR graphics settings.

---

## smoke

```javascript
stock.particle.smoke(start, offset, speed, time, concentration[, texture])
```

| Param | Type | Description |
|-------|------|-------------|
| `start` | `[x,y,z]` | Anchor in model space |
| `offset` | `[x,y,z]` | Added to start |
| `speed` | number | Particle speed |
| `time` | number | Duration (**seconds**) |
| `concentration` | number | Density 0.0~1.0 |
| `texture` | string | Optional texture path |

---

## steam

Same as smoke without `concentration` — lighter steam effect.

```javascript
stock.particle.steam([0, 3.8, -1.0], [0, 0.3, 0], 0.5, 2.5);
```

> **Tip**
> Particles spawn every tick during `time`. Avoid very long `time` every tick unless you want dense smoke.
'@

"Button-EN" = @'
# BUTTON

`BUTTON` mode lets players trigger script functions from the GUI.

---

## JSON

```json
"functions": {
  "playHorn": "BUTTON",
  "openDoor": "BUTTON"
}
```

Button label defaults to the **function name** (custom labels not yet supported).

---

## When buttons appear

1. Mod config **show script buttons** enabled
2. Player is **riding** the stock
3. IR stock inventory open, or vanilla inventory (`E`)

---

## Click flow

```
Player clicks (client) → packet → server runs JS → sound/animation APIs
```

---

## Troubleshooting

- Check `config/irscripts.cfg` and IR Scripts config page
- Confirm `"BUTTON"` mode in JSON
- Ride stock and open inventory
- Check `latest.log` for `[Bootstrap]` / `[Registry]`
'@

"Examples-EN" = @'
# Examples

Ready-to-adapt script patterns.

---

## CRH run sound (LOOP-TICK)

Only re-play when volume/pitch changes — see `examples/crh1a_sound.js` in the repo.

```javascript
var TICK_INTERVAL = 2;
var lastTick = -TICK_INTERVAL;
var state = { run1: { active: false, vol: -1, pitch: -1 } };

function onUpdate() {
    if (stock.getTickCount() - lastTick < TICK_INTERVAL) return;
    lastTick = stock.getTickCount();
    updateRun1(stock.getSpeedKmh());
}
```

---

## Horn sequence (LOOP-SCRIPTS)

```javascript
function hornSequence() {
    stock.sound.utilPlay("sounds/horn_1.ogg", 1.0);
    time.sleep(0.3);
    stock.sound.utilPlay("sounds/horn_1.ogg", 0.8);
    time.sleep(2.0);
}
```

---

## Door buttons

```javascript
function openLeft() {
    stock.animation.play(
        "immersiverailroading:amin/1/left.anim",
        "leftdoor", "PLAY_BOTH", false, 1.0
    );
}
```

---

## Steam monitoring

```javascript
function onTick() {
    if (!stock.control.isSteam()) return;
    var r = stock.readout;
    if (r.getBoilerPressure() < 0.3) {
        print("Low boiler: " + r.getBoilerPressure());
    }
}
```
'@

"Debug-EN" = @'
# Debugging

When scripts misbehave, **read the log first**.

---

## Log location

Modpack: `versions/<name>/logs/latest.log`

---

## Keywords

| Prefix | Meaning |
|--------|---------|
| `[Bootstrap]` | Scanning stock JSON |
| `[Registry]` | Registering functions |
| `[Runtime]` | Create/destroy instances |
| `[Engine]` | Rhino init |
| `[Script\|...\|...]` | Your `print()` or errors |
| `[TrackBootstrap]` | Scanning track JSON scripts |
| `[TrackRegistry]` | Track script registration |

---

## JVM flags

| Flag | Description |
|------|-------------|
| `-Dirscripts.debug=true` | Verbose debug |
| `-Dirscripts.scriptPrint=false` | Disable print |
| `-Dirscripts.loopErrorCooldownMs=5000` | LOOP error log interval |

---

## Checklist

```
1. irscripts 2.0 jar in mods
2. JSON scripts block correct
3. Restart / reload IR definitions
4. Search latest.log: [Bootstrap] [Registry] [Script
5. Add print() to trace branches
```
'@
}

foreach ($entry in $pages.GetEnumerator()) {
    $slug = $entry.Key -replace '-EN$',''
    $header = @"
[ZH]($slug-ZH) | **EN**

[< Contents](Handbook-EN)

---

"@
    Set-Content (Join-Path $outDir ($entry.Key + ".md")) ($header + $entry.Value) -Encoding UTF8 -NoNewline
    Write-Host "Wrote $($entry.Key).md"
}

Write-Host "EN pages done."
