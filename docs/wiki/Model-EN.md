[ZH](Model-ZH) | **EN**

[< Contents](Handbook-EN)

---

# model API

Root-level global for OBJ loading, mesh transforms, client rendering.

**Server-side** calls; client draws via packets.

---

## Coordinates

| Method | Description |
|--------|-------------|
| `model.setCenter([x,y,z])` | Render anchor in **world** X/Y/Z |
| `model.setNormal([x,y,z])` | Local frame normal |

`renderon` offsets apply in local frame at the anchor.

---

## Loading

```javascript
var handle = model.load("mypack:models/extra.obj");
```

---

## model.mesh

`get`, `scaling`, `scalingfrom`, `rotate`, `rotatefrom`, `mirrorx/mirrory/mirrorz`, `origin.set`, `group.create`

---

## model.render

`renderon(group, [x,y,z])`, `render(group)`, `adaptcurve(group, curveId)` (experimental)

```javascript
model.setCenter([100, 64, 200]);
var data = model.load("mypack:model/rail.obj");
var mesh = model.mesh.mirrorx(model.mesh.get(data, "rail"));
model.render.renderon(mesh, [0, 0, 0]);
```
