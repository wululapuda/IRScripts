[ZH](Sound-ZH) | **EN**

[< Contents](Handbook-EN)

---

# Sound

`stock.sound` — server-side `.ogg` playback.

```javascript
stock.sound.play(path, volume, pitch, repeat, maxDistance);
stock.sound.utilPlay(...);  // pause script until done
stock.sound.stopPlay();
stock.sound.stopPlay("sounds/run.ogg");
```

Paths: `sounds/horn_1.ogg` → `<domain>:sounds/horn_1.ogg`. Domain from `definitionId` or `immersiverailroading`.

> **Warning**
> Don't `play(..., true)` every tick — track state, only re-play on change. See [Examples](Examples-EN).
