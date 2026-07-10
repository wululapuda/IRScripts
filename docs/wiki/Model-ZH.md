[EN](Model-EN) | **中文**

[< 目录](Handbook-ZH)

---

# model API

与 `stock` 同级的全局对象，用于加载 OBJ、变换网格、客户端渲染。

**仅服务端**调用加载/渲染指令；客户端负责实际绘制。

---

## 坐标系

| 方法 | 说明 |
|------|------|
| `model.setCenter([x,y,z])` | 渲染锚点：**世界坐标** X/Y/Z |
| `model.setNormal([x,y,z])` | 局部坐标系法线方向 |

`renderon` 等偏移在锚点处的局部坐标系中应用。

---

## 加载

```javascript
var handle = model.load("mypack:models/extra.obj");
```

返回模型数据句柄 ID，供 `model.mesh` / `model.render` 使用。

---

## model.mesh

| 方法 | 说明 |
|------|------|
| `model.mesh.get(modelData, meshName)` | 取出单个 mesh 句柄 |
| `model.mesh.scaling(mesh, [sx,sy,sz])` | 缩放 |
| `model.mesh.scalingfrom(mesh, scale, pivot)` | 绕点缩放 |
| `model.mesh.rotate(mesh, [rx,ry,rz])` | 旋转（度） |
| `model.mesh.rotatefrom(mesh, rot, pivot)` | 绕点旋转 |
| `model.mesh.mirrorx/mirrory/mirrorz(mesh)` | 镜像，返回新句柄 |
| `model.mesh.origin.set(mesh, [x,y,z])` | 设置 mesh 原点 |
| `model.mesh.group.create(start, middle, end)` | 创建 mesh 组 |

---

## model.render

| 方法 | 说明 |
|------|------|
| `model.render.renderon(group, [x,y,z])` | 在局部偏移处渲染 |
| `model.render.render(group)` | 在默认位置渲染 |
| `model.render.adaptcurve(group, curveId)` | 沿曲线适配（实验性） |

```javascript
model.setCenter([100, 64, 200]);
var data = model.load("mypack:model/rail.obj");
var mesh = model.mesh.get(data, "rail");
mesh = model.mesh.mirrorx(mesh);
model.render.renderon(mesh, [0, 0, 0]);
```