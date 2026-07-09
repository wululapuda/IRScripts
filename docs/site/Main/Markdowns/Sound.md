# 音效

`stock.sound` — 在列车位置播放自定义 `.ogg` 音效。

**仅服务端调用**；客户端通过数据包同步播放。

---

## play — 非阻塞播放

触发后**立刻返回**，不等待播完。

```javascript
stock.sound.play(path, volume)
stock.sound.play(path, volume, pitch)
stock.sound.play(path, volume, pitch, repeat)
stock.sound.play(path, volume, pitch, repeat, maxDistance)
```

| 参数 | 说明 |
|------|------|
| `path` | 音效路径（见下） |
| `volume` | 音量 `0.0~1.0` |
| `pitch` | 音调倍率，默认 `1.0` |
| `repeat` | 是否循环，默认 `false` |
| `maxDistance` | 最远听距（格）；`≤0` 为自动 |

---

## 路径规则

| 写法 | 解析结果 |
|------|----------|
| `sounds/horn_1.ogg` | `<车辆包域>:sounds/horn_1.ogg` |
| `horn_1` | 自动补全为 `sounds/horn_1.ogg` |
| `mypack:sounds/custom.ogg` | 完整 Identifier |

**域规则：**

- `definitionId` 含 `:`（如 `mypack:train_a`）→ 域为 `mypack`
- 否则默认 `immersiverailroading`

```javascript
stock.sound.play("sounds/horn_1.ogg", 0.8);
stock.sound.play("sounds/run.ogg", 1.0, 1.5, true);       // 循环走行音
stock.sound.play("sounds/horn_1.ogg", 1.0, 1.0, false, 64); // 64 格内可听见
```

---

## utilPlay — 播完再继续

参数与 `play` 相同，但会**暂停当前脚本函数**，等音频大致播完后再执行下一行。

```javascript
function announce() {
    stock.sound.utilPlay("sounds/horn_1.ogg", 1.0);
    print("笛声播完了");  // 不会立刻执行
}
```

- 根据 Ogg 时长估算等待（受 `pitch` 影响）
- `repeat: true` 时等**一轮**后自动 `stopPlay`
- **不阻塞**服务端主线程（协程按 tick 恢复）

配合 `LOOP-SCRIPTS` 模式使用。

---

## stopPlay — 停止

```javascript
stock.sound.stopPlay();              // 停本车所有脚本音效
stock.sound.stopPlay("sounds/run.ogg");  // 停指定路径
```

列车被拆除或脚本取消等待时，相关音效也会自动清理。

---

## 与 IR 原生音效的关系

[原野手册 · 音效](https://goldenfield192.github.io/#/Main/Markdowns/Sounds) 讲的是 JSON 里 `sounds.wheels`、`sounds.horn` 等**静态绑定**。

IR Scripts 的 `stock.sound` 是**运行时动态播放**——适合按速度切层、按钮触发、条件判断等逻辑。

两者可以并存：JSON 管默认轮轨音，脚本管额外逻辑音。

---

## 走行音注意

> [!WARNING]
> 每 tick 都 `play(..., true)` 会叠很多层循环音。  
> 应维护状态，只在 `volume` / `pitch` **变化时**才重新 `play`。  
> 见 [完整示例 · CRH 走行音](Examples.md)。
