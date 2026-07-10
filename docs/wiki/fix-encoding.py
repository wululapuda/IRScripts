# Fix UTF-8 headers in *-ZH.md wiki pages. Run from docs/wiki/.
import glob, os, re

d = os.path.dirname(os.path.abspath(__file__))
zh, mu, nav = "\u4e2d\u6587", "\u76ee\u5f55", "\u5bfc\u822a"
note = ">\u0020**\u6ce8\u610f\uff1a**"

for path in glob.glob(os.path.join(d, "*-ZH.md")):
    name = os.path.basename(path).replace("-ZH.md", "")
    with open(path, "r", encoding="utf-8") as f:
        text = f.read()
    if name == "API-Reference":
        header = f"[EN](API-Reference-EN) | **{zh}**\n\n[< {mu}](Handbook-ZH) \u00b7 [{nav}](Sidebar-ZH)\n"
    elif name in ("Sidebar", "Home"):
        en = "Sidebar-EN" if name == "Sidebar" else "Home-EN"
        header = f"[EN]({en}) | **{zh}**\n"
    else:
        header = f"[EN]({name}-EN) | **{zh}**\n\n[< {mu}](Handbook-ZH)\n"
    text = re.sub(r"(?s)^\[EN\][^\n]*\n(?:\n\[<[^\n]*\n)?", header, text, count=1)
    text = re.sub(r"(?m)^> \*\*[^\n]{1,30}\*\s*$", note, text)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)
    print("Fixed", os.path.basename(path))
