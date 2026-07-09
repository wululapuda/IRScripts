# 控制组 cg_group

`stock.cg_group` — 读写 IR 车辆 JSON / 模型里定义的 **control group**。

控制组是 IR 动画、车门、灯光等部件的「开关量」，值域 **0.0 ~ 1.0**。

---

## API

| 方法 | 说明 |
|------|------|
| `get(name)` | 读取控制组当前值 |
| `set(name, value)` | 设置值（自动 clamp 到 0~1） |

`name` 来自车辆 JSON 里 widget 的 `control_group` 字段。

---

## 与 Animatrix 的关系

[原野手册 · Animatrix](https://goldenfield192.github.io/#/Main/Markdowns/Animatrix) 里这样写：

```json
{
  "control_group": "leftdoor",
  "animatrix": "immersiverailroading:amin/1/left.anim",
  "mode": "PLAY_BOTH"
}
```

脚本里对应的控制组名就是 `"leftdoor"`：

```javascript
stock.cg_group.set("leftdoor", 1.0);  // 开门方向
stock.cg_group.set("leftdoor", 0.0);  // 关门方向
```

> [!TIP]
> 想「播放一次完整开关门动画」，更推荐用 [stock.animation.play](Animation.md)，它能指定 `PLAY_BOTH` 和 `reverse`。

---

## 示例

```javascript
function openLeftDoor() {
    stock.cg_group.set("leftdoor", 1.0);
}

function closeDoors() {
    stock.cg_group.set("leftdoor", 0.0);
    stock.cg_group.set("rightdoor", 0.0);
}
```
