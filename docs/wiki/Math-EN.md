[ZH](Math-ZH) | **EN**

[< Contents](Handbook-EN)

---

# math API

Root-level global (same scope as `stock`, `model`). Vector math and Bézier curves.

---

## Vectors

Accept `[x, y, z]` arrays.

| Method | Description |
|--------|-------------|
| `math.add/sub/mul/div/neg` | Vector ops |
| `math.dot/cross` | Products |
| `math.length/normalize/distance` | Magnitude |
| `math.lerp/project/angle` | Interpolation / angle |

## Scalars & arrays

`clamp`, `sign`, `degToRad`, `radToDeg`, `linspace`, `arange`, `sum`, `min`, `max`, `mean`

---

## math.curve

| Method | Description |
|--------|-------------|
| `createEndpoints(start, startN, end, endN)` | Bézier from endpoints + normals → handle ID |
| `createCubic(p0,p1,p2,p3)` | Cubic from 4 points |
| `at(id, t)` | Point at t ∈ [0,1] |
| `tangent(id, t)` | Tangent vector |
| `sample(id, count)` | Uniform samples |
| `intersections(id1, id2 [, tol])` | `{position, t1, t2}[]` |

Handles can be passed to `model` `adaptcurve` and similar APIs.
