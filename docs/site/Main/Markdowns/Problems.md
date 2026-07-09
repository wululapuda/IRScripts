# 常见问题

---

## 脚本报错后 LOOP 再也不跑了

**原因：** `LOOP-TICK` / `LOOP-SCRIPTS` 函数抛出未捕获异常后，该函数在本实例内被禁用。

**解决：** 修脚本，重新放置列车（新实例会重新加载）。看日志里第一次报错的原因。

---

## 修改了 .js 但游戏里没变化

- 资源包 zip 是否重新打包
- 是否重启或让 IR 重载定义
- 旧列车实例不会热替换脚本——拆掉重放一辆

---

## getSpeed() 不存在

用 `stock.getSpeedKmh()` 或 `stock.readout.getSpeed()`。  
根级没有 `getSpeed()`。

---

## 能不能用 let / const / 箭头函数？

引擎是 Rhino，建议 **ES5** 写法：`var` + `function`。部分 ES6 可能能用，但不保证。

---

## 一节车能挂几个脚本文件？

JSON 里 `scripts` 是数组，可以多个条目、多个 `.js`。每个文件独立函数表。

---

## 客户端能跑脚本吗？

不能。所有 JS 只在服务端。客户端只负责 UI 和音效/粒子渲染。

---

## 和 IR JSON 里 sounds 块冲突吗？

不冲突。JSON `sounds` 是 IR 原生事件音效；`stock.sound` 是脚本动态播放。见 [音效](Sound.md)。

---

## 列车拆掉后音效还在响

2.0 会在实例销毁时 `stopPlay` 并清理协程。若仍出现，更新到最新 jar 并反馈日志。

---

## ConcurrentModificationException 崩溃

多段 `utilPlay` / `time.sleep` 链式调用时，旧版可能在世界 tick 崩溃。请使用 **2.0 最新构建**（已修复协程调度）。

---

## 粒子看不见

- IR 设置里粒子是否开启
- 坐标是否在模型外（先用 `[0, 4, 0]` 试）
- 只在服务端调用即可，无需客户端脚本

---

## 还想学 IR 资源包本身

继续看 [原野的 IR 资源包手册](https://goldenfield192.github.io/)——模型、JSON、Animatrix、原生音效定义都在那里。

本手册只管 **IR Scripts 脚本 API**。
