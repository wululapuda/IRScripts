# 标准库

IR Scripts 在加载你的脚本之前，会自动注入三个全局模块。风格上借鉴 Python 标准库，方便写逻辑。

---

## time — 时间与等待

| 方法 | 说明 |
|------|------|
| `time.time()` | Unix 时间戳（秒，浮点） |
| `time.monotonic()` | 单调时钟，适合测间隔 |
| `time.sleep(seconds)` | **非阻塞**暂停脚本约 N 秒 |
| `time.localtime([secs])` | 转本地时间结构 |
| `time.strftime(fmt, st)` | 格式化时间 |
| `time.world_tick()` | 当前维度世界总 tick |
| `time.stock_tick()` | 当前列车实体 tick |
| `time.ticks_to_seconds(t)` | tick → 秒 |
| `time.seconds_to_ticks(s)` | 秒 → tick |
| `time.TICKS_PER_SECOND` | `20` |

### sleep 不会卡死世界

```javascript
function playHorn() {
    stock.sound.utilPlay("sounds/horn.ogg", 1.0);
    time.sleep(0.5);   // 暂停本函数 0.5 秒
    stock.sound.play("sounds/horn.ogg", 0.8);  // 0.5 秒后继续
}
```

`time.sleep` 和 `stock.sound.utilPlay` 一样，用 **Rhino 协程**按 tick 调度恢复，**不阻塞**服务端主线程。

> [!TIP]
> 需要「等播完再干别的」时，用 `utilPlay` + `time.sleep` 组合；配合 `LOOP-SCRIPTS` 模式效果最好。

---

## util — 数学工具

| 方法 | 说明 |
|------|------|
| `util.clamp(x, min, max)` | 限制到范围内 |
| `util.lerp(a, b, t)` | 线性插值 |
| `util.inverseLerp(a, b, v)` | 反插值 → 0~1 |
| `util.mapRange(v, inMin, inMax, outMin, outMax)` | 区间映射 |
| `util.sign(x)` | 符号 -1 / 0 / 1 |
| `util.approximately(a, b[, eps])` | 浮点近似相等 |
| `util.mod(x, m)` | 正余数 |
| `util.degToRad` / `util.radToDeg` | 角度转换 |

```javascript
var ratio = util.clamp(stock.getSpeedKmh() / 200.0, 0, 1);
var pitch = util.mapRange(ratio, 0, 1, 0.8, 1.8);
```

---

## random — 随机数

| 方法 | 说明 |
|------|------|
| `random.random()` | [0, 1) 浮点 |
| `random.uniform(a, b)` | 均匀分布 |
| `random.randint(a, b)` | 闭区间整数 |
| `random.choice(arr)` | 随机取一个元素 |
| `random.shuffle(arr)` | 原地洗牌 |
| `random.seed(n)` | 固定种子（可复现） |

```javascript
if (random.random() < 0.01) {
    stock.sound.play("sounds/clack.ogg", 0.3);
}
```
