// CRH1A sound logic adapted to IR Scripts format (LOOP mode)
//
// Stock JSON:
//   "scripts": [{
//     "path": "mypack:scripts/crh1a_sound.js",
//     "functions": { "onUpdate": "LOOP" }
//   }]
//
// Sound files:
//   assets/mypack/sounds/train/crh1a_dj.ogg
//   assets/mypack/sounds/train/crh1a_js.ogg
//   assets/mypack/sounds/train/crh2a_run1.ogg
//   assets/mypack/sounds/train/crh1a_run1.ogg
//
// Note: IR Scripts has no stopSound API yet. This version uses state tracking
// and only calls stock.sound.play() when volume/pitch change, with repeat=true
// for active layers. Layers that should stop are simply not updated.
// Full RTM-style stop/start requires a future stopSound API.

var TICK_INTERVAL = 2;
var lastTick = -TICK_INTERVAL;

var state = {
    dj: { active: false, vol: -1, pitch: -1 },
    js: { active: false, vol: -1, pitch: -1 },
    run2: { active: false, vol: -1, pitch: -1 },
    run1: { active: false, vol: -1, pitch: -1 }
};

function onUpdate() {
    if (stock.getTickCount() - lastTick < TICK_INTERVAL) {
        return;
    }
    lastTick = stock.getTickCount();

    var speed = stock.getSpeedKmh();
    var throttle = stock.control.getThrottle();
    var idle = throttle < 0.05;

    updateDj(speed);
    updateJs(speed);

    if (idle) {
        setInactive(state.run2);
        setInactive(state.run1);
    } else {
        updateRun2(speed);
        updateRun1(speed);
    }
}

function updateDj(speed) {
    var vol = 1.0;
    if (speed > 30) {
        vol = Math.max(0, (50 - speed) / 20);
    }
    playLayer(state.dj, "sounds/train/crh1a_dj.ogg", vol, 1.0);
}

function updateJs(speed) {
    if (speed <= 5) {
        setInactive(state.js);
        return;
    }
    var pitch = fadeCon(0, 0.6, 300, 1.4, speed);
    var vol = 1.0;
    if (speed < 100) {
        vol = fadeCon(5, 0.0, 100, 1.0, speed);
    }
    if (speed > 300) {
        pitch = 1.4;
    }
    playLayer(state.js, "sounds/train/crh1a_js.ogg", vol, pitch);
}

function updateRun2(speed) {
    if (speed <= 0.2 || speed >= 25) {
        setInactive(state.run2);
        return;
    }
    var vol = 1.0;
    if (speed < 5) {
        vol = speed / 5;
    }
    if (speed > 20) {
        vol = (28 - speed) / 8;
    }
    playLayer(state.run2, "sounds/train/crh2a_run1.ogg", vol, 1.0);
}

function updateRun1(speed) {
    if (speed <= 25) {
        setInactive(state.run1);
        return;
    }
    var vol = 1.0;
    var pitch;
    if (speed <= 50) {
        pitch = (speed + 30) / 100;
    } else if (speed < 120) {
        pitch = (speed + 60) / 180;
    } else {
        pitch = (speed + 130) / 250;
    }
    playLayer(state.run1, "sounds/train/crh1a_run1.ogg", vol, pitch);
}

function playLayer(layer, path, vol, pitch) {
    vol = clamp(vol, 0, 1);
    pitch = clamp(pitch, 0.1, 2.0);
    if (layer.active && layer.vol === vol && layer.pitch === pitch) {
        return;
    }
    layer.active = true;
    layer.vol = vol;
    layer.pitch = pitch;
    stock.sound.play(path, vol, pitch, true);
}

function setInactive(layer) {
    layer.active = false;
    layer.vol = -1;
    layer.pitch = -1;
}

function fadeCon(speed1, fade1, speed2, fade2, speed) {
    if (speed2 === speed1) {
        return fade2;
    }
    return (((fade2 - fade1) / (speed2 - speed1)) * (speed - speed1)) + fade1;
}

function clamp(v, min, max) {
    return Math.min(max, Math.max(min, v));
}
