# Fix headers and admonitions in ZH wiki pages (ASCII-only script).
$ErrorActionPreference = "Stop"
$dir = $PSScriptRoot
$utf8 = [System.Text.UTF8Encoding]::new($false)
$zhLabel = [char]0x4e2d + [char]0x6587  # 中文
$navLabel = [char]0x76ee + [char]0x5f55  # 目录
$nav2Label = [char]0x5bfc + [char]0x822a # 导航
$noteLabel = "> **" + ([char]0x6ce8) + ([char]0x610f) + "：**"
$tipLabel = "> **" + ([char]0x63d0) + ([char]0x793a) + "：**"
$warnLabel = "> **" + ([char]0x8b66) + ([char]0x544a) + "：**"

Get-ChildItem $dir -Filter "*-ZH.md" | ForEach-Object {
    $name = $_.BaseName -replace '-ZH$',''
    $text = [System.IO.File]::ReadAllText($_.FullName, $utf8)

    if ($name -eq "API-Reference") {
        $header = "[EN](API-Reference-EN) | **$zhLabel**`n`n[< $navLabel](Handbook-ZH) · [$nav2Label](Sidebar-ZH)`n"
    } elseif ($name -eq "Sidebar" -or $name -eq "Home") {
        $enName = if ($name -eq "Sidebar") { "Sidebar-EN" } else { "Home-EN" }
        $header = "[EN]($enName) | **$zhLabel**`n"
    } else {
        $header = "[EN]($name-EN) | **$zhLabel**`n`n[< $navLabel](Handbook-ZH)`n"
    }

    $text = $text -replace '(?s)^\[EN\][^\n]*\n(?:\n\[<[^\n]*\n)?', $header
    $text = $text -replace '(?m)^> \*\*[^\n]{2,12}：\*\*', $noteLabel
    $text = $text -replace '(?m)^> \*\*[^\n]{2,12}：\*\*\s*$', $noteLabel
    # Fix broken admonition lines (truncated encoding)
    $text = $text -replace '(?m)^> \*\*.{2,20}\?\*\s*$', $noteLabel

    [System.IO.File]::WriteAllText($_.FullName, $text, $utf8)
    Write-Host "Fixed $($_.Name)"
}

Write-Host "Done."
