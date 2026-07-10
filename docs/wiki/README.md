# IR Scripts — GitHub Wiki 源文件

本目录包含可推送到 [IRScripts Wiki](https://github.com/wululapuda/IRScripts.wiki) 的 Markdown 页面。

## 结构

| 文件 | 说明 |
|------|------|
| `Home.md` | 语言选择入口（GitHub Wiki 默认首页） |
| `Home-ZH.md` / `Home-EN.md` | 中英文首页 |
| `Handbook-ZH.md` / `Handbook-EN.md` | 教程手册 |
| `Sidebar-ZH.md` / `Sidebar-EN.md` | 全站导航 |
| `*-ZH.md` / `*-EN.md` | 各章节（后缀区分语言） |
| `API-Reference-ZH.md` | 完整中文 API 速查（来自 `docs/Wiki-ZH.md`） |
| `API-Reference-EN.md` | 英文 API 速查 |

## 从教程站生成中文页

```powershell
cd docs/wiki
.\convert-to-wiki.ps1
```

将 `docs/site/Main/Markdowns/*.md` 转为 GitHub Wiki 格式（修正链接、admonition）。

## 推送到 GitHub Wiki

```powershell
cd docs/wiki
.\publish.ps1
```

或手动：

```bash
git clone https://github.com/wululapuda/IRScripts.wiki.git
cp docs/wiki/*.md IRScripts.wiki/
cd IRScripts.wiki
git add -A
git commit -m "Update wiki from IRScripts repo"
git push
```

## 链接约定

- 页面名：`First-Script-ZH`（GitHub Wiki 文件名去掉 `.md`）
- 文内链接：`[第一个脚本](First-Script-ZH)`
- 语言切换：页首 `[EN](First-Script-EN) | **中文**`

## 与旧 Docsify 站点的关系

- `docs/site/` — 原 Docsify 静态站（保留作本地预览）
- `docs/wiki/` — **GitHub Wiki 正式文档**（英汉双语）

GitHub 仓库 README 可链接到：`https://github.com/wululapuda/IRScripts/wiki`
