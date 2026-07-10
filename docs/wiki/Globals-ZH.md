[EN](Globals-EN) | **中文**

[< 目录](Handbook-ZH)

---
# 全局对象

每个脚本实例启动时，以下对象**自动存在**，无需 `import`。

---

## stock

当前列车实体。所有 API 都挂在它下面：

```
stock
├── getUuid() / getDefinitionId() / getSpeedKmh() …
├── control      机车控制
├── coupler      耦合器
├── readout      仪表读数
├── sound        音效
├── animation    动画
├── particle     烟雾/蒸汽
└── cg_group     控制组
```

`stock` 在函数调用期间始终指向**触发该函数的那节车**。

---

## math

向量运算与 Bézier 曲线。详见 [math API](Math-ZH)。

---

## model

OBJ 加载、网格变换、渲染。详见 [model API](Model-ZH)。

---

## track

轨道几何与道岔方向；**轨道脚本**内 `track.here()` / `track.pos()` 以放置点为准。详见 [track API](Track-ZH)。

---

## print(message)

把内容写入日志：

```
[Script|<uuid前8位>|<脚本path>] <message>
```

```javascript
print("速度=" + stock.getSpeedKmh());
print("定义ID=" + stock.getDefinitionId());
```

关闭输出：`-Dirscripts.scriptPrint=false` 或模组配置里关掉。

---

## time / util / random

标准库，详见 [标准库](Stdlib-ZH)。

| 对象 | 来源 |
|------|------|
| `time` | Python `time` 子集 + 世界 tick |
| `util` | clamp、lerp、mapRange…… |
| `random` | random、randint、choice…… |

---

## 没有的东西

- 没有 `require` / `import`
- 没有 `console.log`（用 `print`）
- 没有 DOM / 浏览器 API
- 没有多线程（脚本是单线程、服务端 tick 驱动）

> **注意：**
> 引擎是 Rhino，语法上按 **ES5** 写最稳妥：`var` + `function`。