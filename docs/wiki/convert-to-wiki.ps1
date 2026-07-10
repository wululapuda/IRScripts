# Converts docs/site tutorial pages to GitHub Wiki format (ZH suffix).
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$siteDir = Join-Path $root "site\Main\Markdowns"
$outDir = $PSScriptRoot

$slugMap = @{
    "QaA.md" = "QaA"
    "Install.md" = "Install"
    "Problems.md" = "Problems"
    "FirstScript.md" = "First-Script"
    "ScriptModes.md" = "Script-Modes"
    "Globals.md" = "Globals"
    "Stdlib.md" = "Stdlib"
    "StockBasic.md" = "Stock-Basic"
    "Control.md" = "Control"
    "Readout.md" = "Readout"
    "Coupler.md" = "Coupler"
    "CgGroup.md" = "Cg-Group"
    "Sound.md" = "Sound"
    "Animation.md" = "Animation"
    "Particle.md" = "Particle"
    "Button.md" = "Button"
    "Examples.md" = "Examples"
    "Debug.md" = "Debug"
}

function Convert-Admonitions([string]$text) {
    $text = $text -replace '(?m)^> \[!NOTE\]\s*$', '> **注意：**'
    $text = $text -replace '(?m)^> \[!TIP\]\s*$', '> **提示：**'
    $text = $text -replace '(?m)^> \[!WARNING\]\s*$', '> **警告：**'
    return $text
}

function Convert-Links([string]$text, [string]$lang) {
    $suffix = "-$lang"
    foreach ($entry in $slugMap.GetEnumerator()) {
        $old = $entry.Key
        $slug = $entry.Value + $suffix
        $base = $entry.Key -replace '\.md$',''
        $text = $text -replace "\($old\)", "($slug)"
        $text = $text -replace "\(Main/Markdowns/$old\)", "($slug)"
        $text = $text -replace "\($base\.md\)", "($slug)"
    }
    $text = $text -replace '\(Wiki-ZH\.md\)', '(API-Reference-ZH)'
    $text = $text -replace '\(Wiki-ZH\)', '(API-Reference-ZH)'
    $text = $text -replace '\(site/README\.md\)', '(Handbook-ZH)'
    return $text
}

function Add-WikiHeader([string]$slug, [string]$lang, [string]$body) {
    $other = if ($lang -eq "ZH") { "EN" } else { "ZH" }
    $handbook = "Handbook-$lang"
    $nav = if ($lang -eq "ZH") { ([char]0x76ee).ToString() + ([char]0x5f55).ToString() } else { "Contents" }
    $langLabel = if ($lang -eq "ZH") { ([char]0x4e2d).ToString() + ([char]0x6587).ToString() } else { "EN" }
    $header = @"
[$other]($slug-$other) | **$langLabel**

[< $nav]($handbook)

---

"@
    return $header + $body
}

function Write-Utf8NoBom([string]$path, [string]$content) {
    [System.IO.File]::WriteAllText($path, $content, [System.Text.UTF8Encoding]::new($false))
}

foreach ($entry in $slugMap.GetEnumerator()) {
    $src = Join-Path $siteDir $entry.Key
    if (-not (Test-Path $src)) { Write-Warning "Missing $src"; continue }
    $slug = $entry.Value
    $content = Get-Content $src -Raw -Encoding UTF8
    $content = Convert-Admonitions $content
    $content = Convert-Links $content "ZH"
    $content = Add-WikiHeader $slug "ZH" $content
    $out = Join-Path $outDir "$slug-ZH.md"
    Write-Utf8NoBom $out $content
    Write-Host "Wrote $out"
}

# Handbook from site README
$readme = Join-Path $root "site\README.md"
if (Test-Path $readme) {
    $content = Get-Content $readme -Raw -Encoding UTF8
    $content = $content -replace '(?m)^> \[!NOTE\]\s*$', '> **注意：**'
    $content = $content -replace '(?m)^> \[!TIP\]\s*$', '> **提示：**'
    $content = Convert-Links $content "ZH"
    $content = Add-WikiHeader "Handbook" "ZH" $content
    Write-Utf8NoBom (Join-Path $outDir "Handbook-ZH.md") $content
}

# API reference from Wiki-ZH
$api = Join-Path $root "Wiki-ZH.md"
if (Test-Path $api) {
    $content = Get-Content $api -Raw -Encoding UTF8
    $content = $content -replace '(?m)^> \*\*注意：\*\*', '> **注意：**'
    $header = @"
[EN](API-Reference-EN) | **$(([char]0x4e2d).ToString() + ([char]0x6587).ToString())**

[< $(([char]0x76ee).ToString() + ([char]0x5f55).ToString())](Handbook-ZH) · [$(([char]0x5bfc).ToString() + ([char]0x822a).ToString())](Sidebar-ZH)

---

"@
    Write-Utf8NoBom (Join-Path $outDir "API-Reference-ZH.md") ($header + $content)
}

Write-Host "Done."
