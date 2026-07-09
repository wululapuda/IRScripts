# 第一个脚本

我们从零写一份能跑的脚本。

---

## 目标

- 列车生成时打印一行日志
- 速度超过 60 km/h 时自动拉制动
- 玩家点按钮吹笛

---

## 第一步：准备音效

把 `horn_1.ogg` 放到：

```
assets/mypack/sounds/horn_1.ogg
```

IR 惯例是 `sounds/` 子目录，扩展名 `.ogg`。

---

## 第二步：写脚本

`assets/mypack/scripts/my_train.js`：

```javascript
function onSpawn() {
    print("列车已生成: " + stock.getDefinitionId());
    print("UUID: " + stock.getUuid());
}

function onTick() {
    var kmh = stock.getSpeedKmh();

    // 每 100 tick 打一次日志，避免刷屏
    if (stock.getTickCount() % 100 === 0) {
        print("速度 " + kmh + " km/h");
    }

    if (kmh > 60 && stock.control.isLocomotive()) {
        stock.control.setTrainBrake(0.5);
    }
}

function playHorn() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
}
```

---

## 第三步：挂到 JSON

```json
"scripts": [{
  "path": "mypack:scripts/my_train.js",
  "functions": {
    "onSpawn": "ONCE",
    "onTick": "LOOP-TICK",
    "playHorn": "BUTTON"
  }
}]
```

---

## 第四步：进游戏验证

1. 重载资源 / 重启
2. 放置或刷出该列车
3. 看 `latest.log` 有没有 `[Script|...]` 开头的 `print` 输出
4. 加速到 60 km/h 以上，观察制动是否生效
5. 骑乘列车，打开 IR 背包界面，点 **playHorn** 按钮

> [!TIP]
> 如果 `print` 没出来，检查 `config/irscripts.cfg` 里「脚本 print 输出」是否开启。

---

## 你应该认识的全局对象

| 名称 | 是什么 |
|------|--------|
| `stock` | 当前列车实例，所有 API 的入口 |
| `print(msg)` | 写日志 |
| `time` | 时间、非阻塞 sleep |
| `util` | clamp、lerp 等数学工具 |
| `random` | 随机数 |

下一章：[执行模式](ScriptModes.md)
