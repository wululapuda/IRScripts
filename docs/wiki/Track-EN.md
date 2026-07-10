[ZH](Track-ZH) | **EN**

[< Contents](Handbook-EN)

---

# Track scripts & track API

Track scripts customize **placement and update logic**. All coordinates use the **track placement point** — **never rolling stock position**.

**Server-side only.**

---

## Registration (track JSON)

Add optional `scripts` block (empty = IR native):

```json
"scripts": {
  "straight": "nobase_straight",
  "switch": "nobase_switch",
  "curve": ""
}
```

Non-empty values resolve to `domain:scripts/xxx.js`.

---

## Required functions (hard rule)

Every registered track `.js` **must** define:

### `init()` — all types

- Called **once** per **track state update**
- Fires when the **player places** the track
- Also fires on rebuild (neighbor changes, chunk load) when state changes
- Deduplicated by state fingerprint

### `switch()` — SWITCH type only

- Called when **switch routing direction** changes
- Defines how the track should change on switch toggle

Missing `init` → script skipped, error logged.  
Missing `switch` on switches → switch toggle skipped, error logged.

---

## Example

```javascript
function init() {
    var pos = track.pos();     // placement block [x, y, z]
    var info = track.here();   // geometry at placement point
    print("placed at " + pos.join(", "));
}

function switch() {
    print("routing: " + track.here().switchDirection);
}
```

> **Note**
> No `stock` in track scripts. `track.here()` is always the **placement tile**, not the train.

---

## When functions run

| Event | Function |
|-------|----------|
| Player places track | `init()` |
| Neighbor change / rebuild | `init()` (if state changed) |
| Chunk load | `init()` (if state changed) |
| Switch direction change | `switch()` |

---

## track API (in track scripts)

| Method | Description |
|--------|-------------|
| `track.pos()` | Placement block `[x, y, z]` |
| `track.here()` | Geometry at placement point |
| `track.at([x,y,z])` | Query another world position |

### `track.here()` fields

`placement`, `type`, `trackId`, `direction`, `switchDirection`, `switchForced`, `start`, `end`, `branch1`, `branch2`, `controls`, `gauge`, `gaugeScale`

**Switch branch 2 (turn / 方向二) shortcuts:**

```javascript
var endPos = track.getBranch2EndPosition();   // [x, y, z] world
var endNormal = track.getBranch2EndNormal();  // unit tangent at endpoint
// optional: pass [x, y, z] to query another switch tile
var pos2 = track.getBranch2EndPosition([100, 64, 200]);
```

### Registry queries

`track.script()`, `track.hasScript()`, `track.usesNative()`, `track.scriptTypes()`

---

## Logging

`[TrackScript|path|x, y, z] message` — enable debug with `-Dirscripts.debug=true`
