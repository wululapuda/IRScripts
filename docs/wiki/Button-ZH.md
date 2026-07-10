[EN](Button-EN) | **中文**

[< 目录](Handbook-ZH)

---
# BUTTON 按钮

`BUTTON` 模式让玩家在 GUI 里点击按钮，触发脚本函数。

---

## JSON 配置

```json
"functions": {
  "playHorn": "BUTTON",
  "openDoor": "BUTTON"
}
```

```javascript
function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.9);
}

function openDoor() {
    stock.animation.play("", "leftdoor", "PLAY_BOTH", false, 1.0);
}
```

按钮标签默认等于**函数名**（暂不支持自定义中文标签）。

---

## 什么时候显示

需满足：

1. 模组配置里 **「显示脚本按钮」** 已开启
2. 玩家**正在骑乘**该列车
3. 打开了 **IR 车辆背包界面**，或按 `E` 打开**原版背包**

按钮绘制在 GUI 层，不依赖玩家是否打开 IR 设置面板。

---

## 点击流程

```
玩家点按钮（客户端）
    → 发包到服务端
    → 服务端执行对应 JS 函数
    → sound / animation 等 API 生效
```

音效、动画的客户端表现由数据包同步，和 LOOP 脚本一样。

---

## 常见问题

**按钮没出来？**

- 检查 `config/irscripts.cfg` 或 IR 配置里的 IR Scripts 页
- 确认 JSON 里函数模式是 `"BUTTON"`
- 确认你骑在车上且打开了背包界面
- 看 `latest.log` 有没有 `[Bootstrap]` / `[Registry]` 注册记录

**能放多少个按钮？**

每个 `scripts` 条目里，所有 `BUTTON` 函数都会显示。太多会挤，建议一个脚本文件里 BUTTON 函数不超过 5~6 个。