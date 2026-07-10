[EN](QaA-EN) | **中文**

[< 目录](Handbook-ZH)

---
# Q&A

一些在动手写脚本之前，大家常问的问题。

---

## IR Scripts 和 IR 自带的 JSON 配置是什么关系？

IR 原生 JSON 负责**静态定义**：模型、物理、默认音效、Animatrix 注册、控制组……  
IR Scripts 负责**动态逻辑**：根据速度、按钮、时间等条件，在运行时调用 API。

你可以把脚本理解为「挂在车上的小型程序」，而 JSON 是「车的说明书」。

---

## 我需要会 Java 吗？

不需要。脚本用 **JavaScript** 写，引擎是 Mozilla Rhino（已打包进 mod）。

建议写法：

- 用 `var`，传统 `function` 声明
- 兼容 **ES5** 语法
- 不要用 `import`——标准库会自动注入为全局对象

---

## 脚本跑在客户端还是服务端？

**始终服务端。**

| 端 | 做什么 |
|----|--------|
| 服务端 | 执行 JS、调用 control/sound 等 API |
| 客户端 | 播放音效、画 BUTTON 按钮、渲染粒子 |

单人游戏 = 本地集成服务端，逻辑一样。

---

## 一节车一个引擎，还是整列车共享？

**每个列车实体实例**各自拥有一个独立的 JS 引擎。

同一编组的 8 节动车，如果每节都挂了脚本，就会跑 8 份——互不干扰。

---

## 和 RTM 脚本比呢？

IR Scripts 是专为 IR 1.12.2 设计的轻量方案，API 直接映射 IR 的读数、控制组、音效系统。  
如果你熟悉 [原野手册](https://goldenfield192.github.io/) 里的 Animatrix / READOUT，脚本里的 `stock.readout` 和 `stock.animation` 概念是对得上的。

---

## 资源包改了脚本，要重启吗？

修改 zip / 资源包后，需要让 IR **重新加载车辆定义**（重启游戏，或在 IR 里重载定义），Bootstrap 才会重新扫描 `scripts` 块。

---

## 哪里看完整 API 列表？

本手册各章节即 API 参考，另见 [API 速查表](API-Reference-ZH)。  
扩展 API：[math](Math-ZH)、[model](Model-ZH)、[track](Track-ZH)。