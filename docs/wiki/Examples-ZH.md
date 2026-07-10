[EN](Examples-EN) | **中文**

[< 目录](Handbook-ZH)

---
# 完整示例

几个可以直接改一改就用的脚本。

---

## CRH 走行音（LOOP-TICK）

**思路：** 按速度分层，只在音量/音调变化时 `play`，避免每 tick 叠音。

JSON：

```json
"scripts": [{
  "path": "mypack:scripts/crh_sound.js",
  "functions": { "onUpdate": "LOOP-TICK" }
}]
```

脚本核心（完整版见仓库 `examples/crh1a_sound.js`）：

```javascript
var TICK_INTERVAL = 2;
var lastTick = -TICK_INTERVAL;
var state = { run1: { active: false, vol: -1, pitch: -1 } };

function onUpdate() {
    if (stock.getTickCount() - lastTick < TICK_INTERVAL) return;
    lastTick = stock.getTickCount();

    var speed = stock.getSpeedKmh();
    updateRun1(speed);
}

function updateRun1(speed) {
    if (speed <= 25) { setInactive(state.run1); return; }
    var pitch = speed < 120 ? (speed + 60) / 180 : (speed + 130) / 250;
    playLayer(state.run1, "sounds/train/crh1a_run1.ogg", 1.0, pitch);
}

function playLayer(layer, path, vol, pitch) {
    if (layer.active && layer.vol === vol && layer.pitch === pitch) return;
    layer.active = true;
    layer.vol = vol;
    layer.pitch = pitch;
    stock.sound.play(path, vol, pitch, true, 128);
}

function setInactive(layer) {
    layer.active = false;
    layer.vol = -1;
    layer.pitch = -1;
}
```

---

## 鸣笛序列（LOOP-SCRIPTS）

```json
"functions": { "hornSequence": "LOOP-SCRIPTS" }
```

```javascript
function hornSequence() {
    stock.sound.utilPlay("sounds/horn_1.ogg", 1.0);
    time.sleep(0.3);
    stock.sound.utilPlay("sounds/horn_1.ogg", 0.8);
    time.sleep(2.0);
    // 跑完后 LOOP-SCRIPTS 会在下一圈再调用
}
```

---

## 动车车门 + 按钮

```json
"functions": {
  "openLeft": "BUTTON",
  "closeLeft": "BUTTON"
}
```

```javascript
function openLeft() {
    stock.animation.play(
        "immersiverailroading:amin/1/left.anim",
        "leftdoor", "PLAY_BOTH", false, 1.0
    );
}

function closeLeft() {
    stock.animation.play(
        "immersiverailroading:amin/1/left.anim",
        "leftdoor", "PLAY_BOTH", true, 1.0
    );
}
```

---

## 蒸汽机车监控（LOOP-TICK）

```javascript
function onTick() {
    if (!stock.control.isSteam()) return;

    var r = stock.readout;
    if (r.getBoilerPressure() < 0.3) {
        print("锅炉压力偏低: " + r.getBoilerPressure());
    }

    if (stock.getSpeedKmh() > 5 && r.getCylinderDrain() > 0.5) {
        stock.particle.steam([0, 3.5, 0], [0, 0.5, 0], 0.4, 0.5);
    }
}
```

---

## 综合模板

```javascript
function onSpawn() {
    print("spawned: " + stock.getDefinitionId());
    if (stock.control.isDiesel()) {
        stock.control.setEngine(1);
    }
}

function onTick() {
    if (stock.control.isLocomotive() && stock.getSpeedKmh() > 80) {
        stock.control.setTrainBrake(
            util.clamp((stock.getSpeedKmh() - 80) / 20, 0, 1)
        );
    }
}

function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.9);
}
```