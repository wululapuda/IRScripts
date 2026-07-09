# 动画

`stock.animation` — 播放车辆 JSON 里 `animations` 注册的 IR **`.anim`** 动画。

如果你做过 [Animatrix 动画](https://goldenfield192.github.io/#/Main/Markdowns/Animatrix)，这里的概念完全衔接。

---

## play — 统一入口

```javascript
stock.animation.play(animFile, controlOrReadout, playMode, reverse, initialValue)
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `animFile` | string | animatrix 路径；传 `""` 则只按控制组查找 |
| `controlOrReadout` | string | JSON 里的 `control_group` 名 |
| `playMode` | string | 见下表 |
| `reverse` | boolean | 是否反向（如 `PLAY_BOTH` 关门） |
| `initialValue` | number | 初始控制值 `0.0~1.0` |

### playMode 取值

| 值 | 含义 |
|----|------|
| `VALUE` | 动画变量直接控制进度 |
| `PLAY_FORWARD` | 变量 ≥ 0.95 时正放 |
| `PLAY_REVERSE` | 变量 < 0.95 时正放 |
| `PLAY_BOTH` | 正放 + 反放（开关门常用） |
| `LOOP` | 变量 > 0.95 时循环 |
| `LOOP_SPEED` | 循环，速度随变量变化 |

> [!WARNING]
> `PLAY_FORWARD` / `LOOP` 等通常需要 `initialValue >= 0.95`（传 `1.0`）才会开始；`PLAY_REVERSE` 或 `reverse=true` 用于反向。

---

## 辅助方法

| 方法 | 说明 |
|------|------|
| `get(name)` | 读控制组当前值 |
| `list()` | 本车动画控制组列表 |

---

## CRH1A 车门示例

JSON 里：

```json
{
  "control_group": "leftdoor",
  "animatrix": "immersiverailroading:amin/1/left.anim",
  "mode": "PLAY_BOTH"
}
```

脚本：

```javascript
function openLeftDoor() {
    stock.animation.play(
        "immersiverailroading:amin/1/left.anim",
        "leftdoor",
        "PLAY_BOTH",
        false,
        1.0
    );
}

function closeLeftDoor() {
    stock.animation.play(
        "immersiverailroading:amin/1/left.anim",
        "leftdoor",
        "PLAY_BOTH",
        true,    // reverse
        1.0
    );
}
```

挂到 BUTTON：

```json
"functions": {
  "openLeftDoor": "BUTTON",
  "closeLeftDoor": "BUTTON"
}
```

---

## 其它用法

```javascript
// 不指定 anim 文件，只按控制组
stock.animation.play("", "rightdoor", "PLAY_BOTH", false, 1.0);

// VALUE 模式：scrub 到 50%
stock.animation.play("", "my_anim", "VALUE", false, 0.5);

// 风扇循环
stock.animation.play("", "fan", "LOOP", false, 1.0);

// 停止循环
stock.animation.play("", "fan", "LOOP", true, 0.0);
```

> [!NOTE]
> `readout` 型动画（只读仪表驱动）无法从脚本触发，请用 `control_group`。
