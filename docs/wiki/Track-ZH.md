[EN](Track-EN) | **中文**

[< 目录](Handbook-ZH)

---

# 轨道脚本与 track API

轨道脚本用于**自定义轨道放置与更新逻辑**，坐标一律以**轨道放置点**为准，**不使用火车位置**。

**仅服务端**执行。

---

## 注册（轨道 JSON）

在轨道定义 JSON 根节点添加 `scripts`（6 种类型均可空缺，空字符串走 IR 原生）：

```json
"scripts": {
  "straight": "nobase_straight",
  "slope": "",
  "curve": "",
  "switch": "nobase_switch",
  "customcurve": "",
  "turntable": ""
}
```

| 键 | IR 类型 |
|----|---------|
| `straight` | 直线 |
| `slope` | 坡道 |
| `curve` | 弯道 |
| `switch` | 道岔 |
| `customcurve` | 自定义曲线 |
| `turntable` | 转台 |

有值 → 解析为 `domain:scripts/xxx.js`（如 `immersiverailroading:scripts/nobase_straight.js`）。

---

## 硬性要求：必须实现的函数

每个已注册的轨道 `.js` **必须**定义：

### `init()` — 所有类型必填

- 在**每次轨道状态更新**时调用**一次**
- **玩家放置轨道**时触发（放置成功后）
- 世界加载、相邻方块变化导致轨道重建时也会触发
- 通过状态指纹去重：同一状态不会重复调用

### `switch()` — 仅道岔（`switch` 类型）必填

- 在**道岔走行方向改变**时调用
- 用于描述「道岔切换时轨道应如何变化」
- 与 `init` 独立；切换时只调用 `switch`（若方向实际改变）

缺少 `init` → 脚本不执行，日志报错。  
道岔缺少 `switch` → 切换时不执行 `switch`，日志报错。

---

## 示例脚本

`assets/immersiverailroading/scripts/nobase_straight.js`：

```javascript
function init() {
    var pos = track.pos();          // 放置点方块坐标 [x, y, z]
    var info = track.here();        // 以放置点为准的几何信息

    print("straight placed at " + pos[0] + ", " + pos[1] + ", " + pos[2]);
    print("trackId=" + info.trackId + " yaw start=" + info.start.position);
}

// 道岔脚本 additionally:
function switch() {
    var info = track.here();
    print("switch now " + info.switchDirection);  // STRAIGHT / TURN / NONE
}
```

> **注意：**
> 轨道脚本作用域内**没有** `stock`。`track.here()` 始终指向**本段轨道的放置点**，不是火车坐标。

---

## 何时触发

| 事件 | 调用 |
|------|------|
| 玩家放置轨道 | `init()` |
| 相邻方块变化 / 轨道重建 | `init()`（状态变化时） |
| 世界加载已有轨道 | `init()`（状态变化时） |
| 道岔方向切换 | `switch()` |

---

## track API（轨道脚本内）

### 坐标

| 方法 | 说明 |
|------|------|
| `track.pos()` | 放置点方块坐标 `[x, y, z]`（整数） |
| `track.here()` | 以放置点为准的轨道几何对象 |
| `track.at([x, y, z])` | 查询其他方块处的轨道（世界坐标） |

### `track.here()` 返回字段

| 字段 | 说明 |
|------|------|
| `placement` | 放置点 `[x, y, z]`（同 `track.pos()`） |
| `type` | `STRAIGHT` / `SLOPE` / `TURN` / `SWITCH` / `CUSTOM` / `TURNTABLE` |
| `trackId` | 轨道定义 ID |
| `direction` | 弯道左右：`NONE` / `LEFT` / `RIGHT` |
| `switchDirection` | 道岔走行：`NONE` / `STRAIGHT` / `TURN` |
| `switchForced` | 道岔锁定 |
| `start` / `end` | 端点 `{position, normal}` 世界坐标 |
| `branch1` / `branch2` | 道岔直向/侧向分支 `{position, normal}` |
| `track.getBranch2EndPosition()` | 方向二末端世界坐标 `[x, y, z]` |
| `track.getBranch2EndNormal()` | 方向二末端世界法向（单位切线） |
| `controls` | Bézier 控制点（世界坐标） |
| `gauge` / `gaugeScale` | 轨距 |

### 注册查询（任意脚本可用）

```javascript
track.script(trackId, "straight");
track.hasScript(trackId, "switch");
track.usesNative(trackId, "curve");
track.scriptTypes();
```

---

## 与 IR 原生的关系

- `scripts` 中**未注册**的类型仍由 IR 原生渲染/物理处理
- 已注册类型由你的 `init` / `switch` 接管自定义逻辑（查询几何用 `track.here()`，勿依赖列车实体）

---

## 日志

```
[TrackScript|domain:scripts/foo.js|100, 64, 200] your print output
[TrackScript|domain:scripts/foo.js] Missing required function init()
```

调试：`-Dirscripts.debug=true`
