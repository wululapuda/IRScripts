[EN](Math-EN) | **中文**

[< 目录](Handbook-ZH)

---

# math API

与 `stock`、`model` 同级的全局对象，提供向量运算与 Bézier 曲线工具。

---

## 向量

接受 `[x, y, z]` 数组或类数组对象。

| 方法 | 说明 |
|------|------|
| `math.add(a, b)` | 向量加 |
| `math.sub(a, b)` | 向量减 |
| `math.mul(v, scalar)` | 标量乘 |
| `math.div(v, scalar)` | 标量除 |
| `math.neg(v)` | 取反 |
| `math.dot(a, b)` | 点积 |
| `math.cross(a, b)` | 叉积 |
| `math.length(v)` | 长度 |
| `math.normalize(v)` | 单位化 |
| `math.distance(a, b)` | 距离 |
| `math.lerp(a, b, t)` | 线性插值 |
| `math.project(a, b)` | 投影 |
| `math.angle(a, b)` | 夹角（弧度） |

## 标量与数组

`math.clamp`, `math.sign`, `math.degToRad`, `math.radToDeg`, `math.linspace`, `math.arange`, `math.sum`, `math.min`, `math.max`, `math.mean`

---

## math.curve — 曲线

| 方法 | 说明 |
|------|------|
| `math.curve.bezierEndpoints(...)` | 由端点+法线创建 Bézier |
| `math.curve.createEndpoints(...)` | 同上，返回曲线句柄 ID |
| `math.curve.createCubic(p0,p1,p2,p3)` | 四点三次曲线 |
| `math.curve.at(id, t)` | 曲线上 t∈[0,1] 的点 |
| `math.curve.tangent(id, t)` | 切线 |
| `math.curve.sample(id, count)` | 均匀采样 |
| `math.curve.controls(id)` | 控制点 |
| `math.curve.intersections(id1, id2 [, tol])` | 两曲线交点 `{position, t1, t2}[]` |

```javascript
var id = math.curve.createEndpoints(
    [0,0,0], [1,0,0],
    [10,0,0], [-1,0,0]
);
var pt = math.curve.at(id, 0.5);
var hits = math.curve.intersections(id1, id2, 0.01);
```

曲线句柄可传给 `model` 的 `adaptcurve` 等接口。