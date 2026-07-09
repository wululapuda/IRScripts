# IR Scripts API 参考（中文 · 最新）

> **Mod ID：** `irscripts` · **版本：** 1.0.0 · **Minecraft：** 1.12.2  
> **依赖：** Immersive Railroading、UniversalModCore、TrackAPI  
> **脚本引擎：** Mozilla Rhino（已打包进 mod，不依赖 JRE 自带 Nashorn）  
> **执行端：** 仅服务端

---

## 目录

1. [快速开始](#快速开始)
2. [脚本配置与执行模式](#脚本配置与执行模式)
3. [全局对象](#全局对象)
4. [标准库](#标准库)
5. [完整 API 列表](#完整-api-列表)
5. [stock 根级 API](#stock-根级-api)
6. [stock.control — 机车控制](#stockcontrol--机车控制)
7. [stock.coupler — 耦合器](#stockcoupler--耦合器)
8. [stock.readout — 读数](#stockreadout--读数)
9. [stock.sound — 音效](#stocksound--音效)
10. [stock.animation — 动画](#stockanimation--动画)
11. [stock.particle — 烟雾 / 蒸汽](#stockparticle--烟雾--蒸汽)
12. [数值约定与调试](#数值约定与调试)
13. [示例脚本](#示例脚本)
14. [限制说明](#限制说明)

---

## 快速开始

### 1. 放置脚本文件

将 `.js` 放入资源包，例如：

```
assets/mypack/scripts/my_train.js
```

在 JSON 中引用：`mypack:scripts/my_train.js`

### 2. 在车辆 JSON 中注册

在 IR 车辆定义 JSON **根节点**添加 `scripts` 数组：

```json
{
  "name": "My Train",
  "max_speed_kmh": 200,
  "scripts": [{
    "path": "mypack:scripts/my_train.js",
    "functions": {
      "onTick": "LOOP-TICK",
      "onCycle": "LOOP-SCRIPTS",
      "onSpawn": "ONCE",
      "playHorn": "BUTTON"
    }
  }]
}
```

| 字段 | 说明 |
|------|------|
| `path` | 脚本资源路径（IR Identifier 格式） |
| `functions` | 键 = JS 函数名，值 = 执行模式（见下表） |

### 3. 编写脚本

```javascript
function onSpawn() {
    print("生成: " + stock.getDefinitionId());
}

function onTick() {
    var kmh = stock.getSpeedKmh();
    var ratio = stock.readout.getSpeed(); // 0~1，相对最高速度
    if (stock.getTickCount() % 100 === 0) {
        print("kmh=" + kmh + " ratio=" + ratio);
    }
}

function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

### 4. 重载资源

修改 zip / 资源包后，重启游戏或在 IR 中重载定义，使 Bootstrap 重新扫描 JSON。

---

## 脚本配置与执行模式

| 模式 | JSON 值 | 说明 |
|------|---------|------|
| `LOOP-TICK` | `LOOP-TICK` 或旧版 `LOOP` | **每个游戏 tick** 调用一次（原 LOOP 行为） |
| `LOOP-SCRIPTS` | `LOOP-SCRIPTS` | **上一次运行完全结束后** 才允许下一次运行；运行中跳过后续 tick |
| `ONCE` | `ONCE` | 实例创建后仅调用一次 |
| `BUTTON` | `BUTTON` | GUI 按钮触发 |

**循环模式错误处理：** `LOOP-TICK` / `LOOP-SCRIPTS` 中若函数抛出错误，该函数会被**永久禁用**（本列车实例内不再运行），日志**只记录一次**。

**BUTTON 显示条件（需 Mixin / GUI 钩子生效时）：**

- 玩家正在骑乘该列车
- 打开 IR 车辆背包界面，或按 `E` 打开原版背包
- 该车辆定义至少有一个 `BUTTON` 函数

按钮标签默认为函数名。

**运行机制：**

- 每个列车实例拥有**独立的 JavaScript 引擎**，互不干扰
- 脚本在服务端扫描 JSON 中的 `scripts` 块并注册（不依赖 Mixin 也能加载）
- 服务端每 tick 遍历所有 `EntityRollingStock` 执行 `LOOP` / `ONCE` 逻辑

---

## 全局对象

| 名称 | 类型 | 说明 |
|------|------|------|
| `stock` | object | 当前列车实例，包含全部 API |
| `print(message)` | function | 输出脚本日志（见下方） |
| `time` | object | Python `time` 模块子集（见 [标准库](#标准库)） |
| `util` | object | 常用数学/映射工具 |
| `random` | object | Python `random` 模块子集 |

### print(message)

将内容写入游戏日志：

```
[Script|<uuid前8位>|<脚本path>] <message>
```

可用 JVM 参数关闭脚本输出：`-Dirscripts.scriptPrint=false`

---

## 完整 API 列表

### 全局

| 名称 | 签名 | 说明 |
|------|------|------|
| `print` | `print(message)` | 输出日志 |
| `stock` | — | 列车绑定对象 |

### stock（根级）— 共 12 项

| 方法 / 属性 | 返回类型 | 读写 | 说明 |
|-------------|----------|------|------|
| `getUuid()` | string | 只读 | 实例 UUID |
| `getDefinitionId()` | string | 只读 | 定义 ID，如 `rolling_stock/locomotives/foo.json` |
| `getTag()` | string | 只读 | IR 车辆 tag |
| `getTickCount()` | number | 只读 | 实体存活 tick 数 |
| `getSpeedKmh()` | number | 只读 | **实际速度 km/h** |
| `getSpeedMps()` | number | 只读 | **实际速度 m/s** |
| `control` | object | — | 机车控制 API |
| `coupler` | object | — | 耦合器 API |
| `readout` | object | — | IR 读数 API |
| `sound` | object | — | 音效 API |
| `cg_group` | object | — | 控制组 (CG) API |
| `animation` | object | — | IR `.anim` 动画 API |
| `particle` | object | — | 烟雾 / 蒸汽粒子 API |
| `getStock()` | object | 只读 | 原始 IR 实体（高级用途，一般不需要） |

> **注意：** 根级 **没有** `getSpeed()`。  
> - 要 **km/h** → `stock.getSpeedKmh()`  
> - 要 **相对最高速度 0~1** → `stock.readout.getSpeed()`

### stock.control — 共 14 项

| 方法 | 返回 | 读写 | 说明 |
|------|------|------|------|
| `getType()` | string | 只读 | `"diesel"` / `"steam"` / `"locomotive"` / `"none"` |
| `isLocomotive()` | boolean | 只读 | 是否为机车 |
| `isDiesel()` | boolean | 只读 | 是否为内燃机车 |
| `isSteam()` | boolean | 只读 | 是否为蒸汽机车 |
| `setEngine(value)` | void | 写 | 内燃引擎开关：`≥0.5` 开，`<0.5` 关 |
| `getEngine()` | number | 只读 | `0` 或 `1` |
| `setTrainBrake(value)` | void | 写 | 列车制动 `0.0~1.0` |
| `getTrainBrake()` | number | 只读 | 列车制动 |
| `setIndependentBrake(value)` | void | 写 | 独立制动 `0.0~1.0` |
| `getIndependentBrake()` | number | 只读 | 独立制动 |
| `setThrottle(value)` | void | 写 | 节流 `0.0~1.0` |
| `getThrottle()` | number | 只读 | 节流 |
| `setReverser(value)` | void | 写 | 换向（语义见下） |
| `getReverser()` | number | 只读 | 换向 |

### stock.coupler — 共 9 项

| 方法 | 返回 | 读写 | 说明 |
|------|------|------|------|
| `isCoupleable()` | boolean | 只读 | 是否支持耦合器 |
| `setCouplerFront(value)` | void | 写 | 前耦合器：`≥0.5` 接合 |
| `setCouplerRear(value)` | void | 写 | 后耦合器 |
| `getCouplerFront()` | number | 只读 | 前耦合器状态 `0/1` |
| `getCouplerRear()` | number | 只读 | 后耦合器状态 `0/1` |
| `getCoupledFront()` | number | 只读 | 前方是否已连挂 `0/1` |
| `getCoupledRear()` | number | 只读 | 后方是否已连挂 `0/1` |
| `getSlackFront()` | number | 只读 | 前松弛量比例 `0~1` |
| `getSlackRear()` | number | 只读 | 后松弛量比例 `0~1` |

### stock.readout — 共 20 项（全部只读）

| 方法 | 说明 |
|------|------|
| `getLiquid()` | 液体装载百分比；不可装液体时为 `0` |
| `getSpeed()` | 当前速度 / 最大速度（**0.0~1.0**） |
| `getTemperature()` | 蒸汽：温度/100；内燃：温度/150 |
| `getCargoFill()` | 货物装载百分比 |
| `getBoilerPressure()` | 蒸汽锅炉压力 / JSON `maxPSI` |
| `getEngineRpm()` | 内燃 RPM/节流（相对设定有延迟） |
| `getTrainBrakeLever()` | 全列刹车杆位置 |
| `getBrakePressure()` | 整列制动压力 |
| `getCoupledFront()` | 前方连挂 `0/1` |
| `getCoupledRear()` | 后方连挂 `0/1` |
| `getCouplerSlackFront()` | 前耦合器松弛量比例 |
| `getCouplerSlackRear()` | 后耦合器松弛量比例 |
| `getBell()` | 铃是否响 `0/1` |
| `getHorn()` | 汽笛/喇叭是否响 `0/1` |
| `getWhistle()` | 同 `getHorn()` |
| `getFrontBogeyAngle()` | 前转向架偏转角 `0~1` |
| `getRearBogeyAngle()` | 后转向架偏转角 `0~1` |
| `getFrontLocomotiveAngle()` | 前机车架偏转角 `0~1` |
| `getRearLocomotiveAngle()` | 后机车架偏转角 `0~1` |
| `getCylinderDrain()` | 蒸汽汽缸排水阀 `0/1` |

### stock.sound — 播放 / 阻塞 / 停止

| 方法 | 阻塞 | 说明 |
|------|------|------|
| `play(path, volume)` | 否 | 非阻塞播放 |
| `play(path, volume, pitch)` | 否 | 指定音调 |
| `play(path, volume, pitch, repeat)` | 否 | 指定循环 |
| `play(path, volume, pitch, repeat, maxDistance)` | 否 | 指定听距（格） |
| `utilPlay(...)` | **脚本内** | 与 `play` 参数相同；**等待音频播完**后再执行后续代码（不阻塞服务端主线程） |
| `stopPlay()` | 否 | 停止本实例所有脚本音效 |
| `stopPlay(path)` | 否 | 停止指定路径的脚本音效 |

**`utilPlay` 说明：**
- 服务端根据 Ogg 时长估算等待时间（受 `pitch` 影响），按 tick 暂停脚本并在后续 tick 恢复
- `repeat: true` 时等待**一轮**后自动 `stopPlay`
- 会暂停当前脚本的后续逻辑（含 `LOOP-SCRIPTS` 的下一次调度），但**不会冻结世界或其它实体**

**`play` 说明：** 触发后立即返回，不等待播完。

---

## stock 根级 API

### 速度：两种读法

```javascript
var kmh = stock.getSpeedKmh();      // 实际 km/h，例如 120.5
var mps = stock.getSpeedMps();      // 实际 m/s
var ratio = stock.readout.getSpeed(); // 0~1，相对 max_speed_kmh
```

写走行音脚本时，推荐用 **比例 + 最高速度**：

```javascript
function sounds() {
    var speed = stock.getSpeedKmh();
    var maxKmh = 200.0; // 与 JSON max_speed_kmh 一致
    var ratio = speed / maxKmh;

    if (ratio > 0.5) {
        stock.sound.play("sounds/train/run.ogg", 1.0, 1.5, true);
    } else if (speed > 0) {
        stock.sound.play("sounds/train/run.ogg", 0.6, 1.0, true);
    }
}
```

---

## stock.control — 机车控制

**所有 setter 仅在服务端生效。** 在非对应车型上调用 setter 会写警告日志并忽略，不会抛异常。

### 换向器 setReverser / getReverser

| 车型 | setReverser | getReverser |
|------|-------------|-------------|
| 内燃 (diesel) | `≤-0.5` 后退；`≥0.5` 前进；其余空档 | `-1` / `0` / `1` |
| 蒸汽 (steam) | `0.0~1.0` 线性 | `0.0~1.0` |
| 其他机车 | `-1.0~1.0` | 原始值 |

### 示例

```javascript
function startDiesel() {
    if (!stock.control.isDiesel()) return;
    stock.control.setEngine(1);
    stock.control.setThrottle(0.3);
    stock.control.setReverser(1); // 前进
}

function autoBrake() {
    if (!stock.control.isLocomotive()) return;
    if (stock.getSpeedKmh() > 60) {
        stock.control.setTrainBrake(1.0);
    } else if (stock.getSpeedKmh() < 5) {
        stock.control.setTrainBrake(0.0);
    }
}
```

---

## stock.coupler — 耦合器

```javascript
function disconnectRear() {
    if (stock.coupler.isCoupleable()) {
        stock.coupler.setCouplerRear(0); // 断开
    }
}

function checkCoupling() {
    print("front coupled=" + stock.coupler.getCoupledFront()
        + " slack=" + stock.coupler.getSlackFront());
}
```

---

## stock.readout — 读数

与 IR 游戏内 GUI overlay 读数一致，**全部只读**，适用于任意车辆类型。

```javascript
function monitor() {
    var r = stock.readout;
    print("ratio=" + r.getSpeed()
        + " kmh=" + stock.getSpeedKmh()
        + " boiler=" + r.getBoilerPressure()
        + " brake=" + r.getBrakePressure()
        + " horn=" + r.getHorn());
}
```

---

## stock.cg_group — 控制组 (CG)

读写 IR 车辆 JSON / 模型里定义的 **control group**（如门、灯光动画组）。值域与 IR 一致：**0.0 ~ 1.0**。

| 方法 | 参数 | 返回 | 说明 |
|------|------|------|------|
| `get(name)` | string 控制组名 | number | 读取当前值 |
| `set(name, value)` | string, number | void | 设置值（服务端；自动 clamp 到 0~1） |

控制组名称来自车辆 JSON 中 widget 的 `control_group` 字段，例如 CRH1A 的 `leftdoor`、`rightdoor`。

```javascript
function openLeftDoor() {
    stock.cg_group.set("leftdoor", 1.0);
}

function closeDoors() {
    stock.cg_group.set("leftdoor", 0.0);
    stock.cg_group.set("rightdoor", 0.0);
}

function doorState() {
    print("left=" + stock.cg_group.get("leftdoor")
        + " right=" + stock.cg_group.get("rightdoor"));
}
```

---

## stock.animation — 动画

播放车辆 JSON 中 `animations` 注册的 IR **`.anim`** 动画。通过控制组写入驱动值；脚本自行指定播放模式与初始值。

### `play(animFile, controlOrReadout, playMode, reverse, initialValue)`

| 参数 | 类型 | 说明 |
|------|------|------|
| `animFile` | string | animatrix 路径，如 `immersiverailroading:amin/1/left.anim`；传空字符串 `""` 则仅按控制组/readout 查找 |
| `controlOrReadout` | string | JSON 中的 `control_group` 名，或 `readout` 名（只读型动画无法从脚本触发） |
| `playMode` | string | `VALUE` / `PLAY_FORWARD` / `PLAY_REVERSE` / `PLAY_BOTH` / `LOOP` / `LOOP_SPEED` |
| `reverse` | boolean | 是否反向（按模式翻转控制值，如 `PLAY_BOTH` 关门） |
| `initialValue` | number | 初始控制值 `0.0~1.0` |

辅助方法：`get(name)` 读取控制组当前值；`list()` 返回本车动画控制组列表。

```javascript
// CRH1A 左门（JSON: PLAY_BOTH, control_group: leftdoor）
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
        true,
        1.0
    );
}

// 不指定文件，只按控制组名
function openRightDoor() {
    stock.animation.play("", "rightdoor", "PLAY_BOTH", false, 1.0);
}

// VALUE 模式：scrub 到 50%
stock.animation.play("", "my_anim", "VALUE", false, 0.5);

// LOOP 循环动画
stock.animation.play("", "fan", "LOOP", false, 1.0);

// 停止循环
stock.animation.play("", "fan", "LOOP", true, 0.0);
```

> `PLAY_FORWARD` / `LOOP` 等模式通常需 `initialValue >= 0.95`（或传 `1.0`）才会开始播放；`PLAY_REVERSE` 模式通常需较低值（或 `reverse=true`）。

---

## stock.particle — 烟雾 / 蒸汽

在**列车模型坐标系**（与 IR 模型轴向一致，自动按轨距缩放）生成 IR 原生烟雾/蒸汽粒子，效果与 IR 内燃机烟雾 / 蒸汽机车烟囱类似。仅客户端渲染，脚本在服务端调用。

### `smoke(start, offset, speed, time, concentration[, texture])`

| 参数 | 类型 | 说明 |
|------|------|------|
| `start` | `[x, y, z]` | 效果起始位置（IR 模型坐标系） |
| `offset` | `[x, y, z]` | 相对起始位置的偏移量（IR 模型坐标系，与 `start` 相加） |
| `speed` | number | 粒子运动速度 |
| `time` | number | 效果持续时间（**秒**） |
| `concentration` | number | 烟雾浓度 `0.0~1.0`（与 IR 内燃机排烟类似） |
| `texture` | string | 可选，粒子贴图资源路径 |

### `steam(start, offset, speed, time[, texture])`

与 `smoke` 相同，但无 `concentration`，为浅色蒸汽效果。

未指定 `texture` 时使用车辆 JSON 中的 `smokeParticleTexture` / `steamParticleTexture` 默认值。

```javascript
// start = 模型坐标系起始点，offset = 相对偏移
stock.particle.smoke([0, 4.2, 0.5], [0, 0.2, 0], 0.4, 3.0, 0.8);

stock.particle.smoke([0, 4.2, 0.5], [0, 0, 0], 0.4, 2.0, 0.6, "mypack:textures/diesel_smoke.png");

stock.particle.steam([0, 3.8, -1.0], [0, 0.3, 0], 0.5, 2.5);

stock.particle.steam([0, 3.8, -1.0], [0, 0, 0], 0.5, 1.5, "immersiverailroading:textures/light.png");
```

> 需在 IR 图形设置中开启粒子（`particlesEnabled`）。效果持续期间每 tick 生成粒子，与 IR 原生排烟逻辑一致。

---

## stock.sound — 音效

**仅服务端调用**；客户端通过数据包同步播放。

### 路径规则

| 写法 | 解析 |
|------|------|
| `sounds/horn_1.ogg` | `<车辆包域>:sounds/horn_1.ogg` |
| `horn_1` | 自动补全为 `sounds/horn_1.ogg` |
| `mypack:sounds/custom.ogg` | 完整 Identifier |

**域规则：**

- 若 `definitionId` 含 `:`（如 `mypack:train_a`），域为 `mypack`
- 否则默认 `immersiverailroading`

```javascript
stock.sound.play("sounds/horn_1.ogg", 0.8);
stock.sound.play("sounds/engine_loop.ogg", 0.5, 1.2);
stock.sound.play("sounds/idle.ogg", 0.3, 1.0, true); // 循环
stock.sound.play("sounds/horn_1.ogg", 1.0, 1.0, false, 64); // 64 格内可听见
stock.sound.play("otherpack:sounds/alarm.ogg", 1.0);
```

---

## 数值约定与调试

### 数值约定

| 概念 | 约定 |
|------|------|
| 开关量 | `0` = 关；`1` = 开 |
| 比例量 | 通常 `0.0~1.0`，setter 自动 clamp |
| 服务端限制 | `control` / `coupler` 的 setter、`sound.play` 仅服务端执行 |
| 错误处理 | API 非法调用写警告日志；脚本函数异常写 `[Script|...]` 错误，不崩溃服务端 |

### JVM 调试参数

| 参数 | 说明 |
|------|------|
| `-Dirscripts.debug=true` | 开启详细调试日志 |
| `-Dirscripts.scriptPrint=false` | 关闭 `print()` 输出 |
| `-Dirscripts.loopErrorCooldownMs=5000` | LOOP 错误重复日志间隔（毫秒） |

### 日志关键字

| 前缀 | 含义 |
|------|------|
| `[Bootstrap]` | 扫描车辆 JSON |
| `[Registry]` | 注册脚本函数 |
| `[Runtime]` | 创建/销毁脚本实例 |
| `[Engine]` | JS 引擎初始化（Rhino） |
| `[Script\|...\|...]` | 脚本 print / 错误 |

### 单人 / 多人

| 项目 | 说明 |
|------|------|
| 脚本运行端 | 始终为服务端（单人 = 集成服务端） |
| 客户端 | 不执行 JS；负责 BUTTON UI 与音效播放 |
| BUTTON | 客户端点击 → 发包 → 服务端执行函数 |

---

## 标准库

每个脚本实例启动时自动加载以下全局模块（无需 `import`）。

### time — Python `time` 子集

| 方法 | 说明 |
|------|------|
| `time.time()` | 墙钟秒（Unix 时间戳，浮点） |
| `time.monotonic()` / `time.perf_counter()` | 单调时钟秒，适合测间隔 |
| `time.process_time()` | 线程 CPU 时间（近似） |
| `time.sleep(seconds)` | **非阻塞**暂停脚本约 N 秒（按 tick 调度，不冻结世界） |
| `time.localtime([secs])` / `time.gmtime([secs])` | 转 struct_time（含 `tm_year` 等字段） |
| `time.mktime(st)` / `time.strftime(fmt, st)` | 时间结构互转 / 格式化（支持 `%Y` `%m` `%d` `%H` `%M` `%S` 等） |
| `time.ctime([secs])` / `time.asctime(st)` | 可读字符串 |
| `time.world_tick()` | 当前维度世界总 tick |
| `time.stock_tick()` | 当前列车实体 tick 计数 |
| `time.ticks_to_seconds(ticks)` / `time.seconds_to_ticks(seconds)` | tick ↔ 秒 |
| `time.TICKS_PER_SECOND` | `20` |
| `time.SECONDS_PER_TICK` | `0.05` |

```javascript
function onTick() {
    if (time.stock_tick() % 100 === 0) {
        print(time.strftime("%H:%M:%S", time.localtime()));
    }
}

function playHorn() {
    stock.sound.utilPlay("sounds/horn.ogg", 1.0);
    time.sleep(0.5); // 暂停脚本 0.5s，世界继续运行
    stock.sound.play("sounds/horn.ogg", 0.8);
}
```

### util — 常用工具

| 方法 | 说明 |
|------|------|
| `util.clamp(x, min, max)` | 限制范围 |
| `util.lerp(a, b, t)` | 线性插值 |
| `util.inverseLerp(a, b, value)` | 反插值 → 0~1 |
| `util.mapRange(v, inMin, inMax, outMin, outMax)` | 区间映射 |
| `util.sign(x)` | 符号 `-1/0/1` |
| `util.approximately(a, b[, eps])` | 近似相等 |
| `util.mod(x, m)` | 正余数 |
| `util.degToRad(deg)` / `util.radToDeg(rad)` | 角度转换 |

### random — Python `random` 子集

| 方法 | 说明 |
|------|------|
| `random.random()` | `[0, 1)` 浮点 |
| `random.uniform(a, b)` | 均匀分布 |
| `random.randint(a, b)` | 闭区间整数 |
| `random.choice(seq)` | 随机元素 |
| `random.shuffle(arr)` | 原地洗牌 |
| `random.seed(n)` | 可选固定种子（可重复序列） |

---

## 示例脚本

### CRH 走行音（LOOP）

JSON：

```json
"scripts": [{
  "path": "immersiverailroading:scripts/sounds.js",
  "functions": { "sounds": "LOOP" }
}]
```

`sounds.js`：

```javascript
function sounds() {
    var speed = stock.getSpeedKmh();
    var ratio = speed / 200.0; // max_speed_kmh

    if (ratio > 0.9) {
        stock.sound.play("sounds/train/crh2a_run1.ogg", 1.0, 1.9, true);
    } else if (ratio > 0.8) {
        stock.sound.play("sounds/train/crh2a_run1.ogg", 1.0, 1.8, true);
    } else if (ratio > 0.5) {
        stock.sound.play("sounds/train/crh2a_run1.ogg", 1.0, 1.5, true);
    } else if (speed > 0) {
        stock.sound.play("sounds/train/crh2a_run1.ogg", 0.6, 1.0, true);
    }
}
```

### 完整功能示例

```javascript
function init() {
    print("spawned: " + stock.getDefinitionId());
    if (stock.control.isDiesel()) {
        stock.control.setEngine(1);
    }
}

function mainLoop() {
    if (stock.control.isLocomotive() && stock.getSpeedKmh() > 80) {
        stock.control.setTrainBrake(Math.min(1.0, (stock.getSpeedKmh() - 80) / 20));
    }
}

function horn() {
    stock.sound.play("sounds/horn_1.ogg", 0.9);
}
```

---

## 限制说明

1. **JavaScript 方言：** Rhino，兼容 **ES5**；不支持 `let`/`const` 以外的 ES6+ 语法（视版本而定，建议用 `var` + 传统函数）。
2. **每实例独立引擎：** 同定义的多节车各自独立运行脚本。
3. **LOOP 性能：** 避免每 tick 重计算；注意 `sound.play(..., true)` 循环播放频率。
4. **尚未提供：**
   - `stopSound()` / 停止循环音效
   - 铃、汽笛、排水阀的 **setter**
   - 自定义 BUTTON 标签
   - 客户端脚本
5. **GUI 按钮：** 依赖 Mixin；若 MixinBooter 未加载 irscripts 配置，LOOP/ONCE 仍可用，BUTTON 可能无效。

---

## API 数量汇总

| 分类 | 数量 |
|------|------|
| 全局 | 2（`print`、`stock`） |
| stock 根级 | 12 |
| stock.control | 14 |
| stock.coupler | 9 |
| stock.readout | 20 |
| stock.sound | 3 个重载 + stopPlay |
| stock.animation | play + get + list |
| stock.particle | smoke + steam |
| **合计（方法级）** | **59+** |
