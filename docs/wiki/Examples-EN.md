[ZH](Examples-ZH) | **EN**

[< Contents](Handbook-EN)

---

# Examples

## CRH run sound

State-based — only `play` when volume/pitch changes. See `examples/crh1a_sound.js`.

## Horn sequence (LOOP-SCRIPTS)

```javascript
function hornSequence() {
    stock.sound.utilPlay("sounds/horn_1.ogg", 1.0);
    time.sleep(0.3);
    stock.sound.utilPlay("sounds/horn_1.ogg", 0.8);
}
```

## Door buttons

```javascript
stock.animation.play("immersiverailroading:amin/1/left.anim",
    "leftdoor", "PLAY_BOTH", false, 1.0);
```
