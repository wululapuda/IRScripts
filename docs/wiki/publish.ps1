# Publish docs/wiki/*.md to GitHub Wiki repository.
param(
    [string]$WikiUrl = "https://github.com/wululapuda/IRScripts.wiki.git",
    [string]$CloneDir = ""
)

$ErrorActionPreference = "Stop"
$srcDir = $PSScriptRoot
if (-not $CloneDir) {
    $CloneDir = Join-Path (Split-Path $srcDir -Parent) "IRScripts.wiki"
}

if (-not (Test-Path $CloneDir)) {
    Write-Host "Cloning $WikiUrl -> $CloneDir"
    git clone $WikiUrl $CloneDir
} else {
    Write-Host "Pulling latest in $CloneDir"
    Push-Location $CloneDir
    git pull --rebase
    Pop-Location
}

Get-ChildItem $srcDir -Filter "*.md" | ForEach-Object {
    Copy-Item $_.FullName (Join-Path $CloneDir $_.Name) -Force
    Write-Host "Copied $($_.Name)"
}

Push-Location $CloneDir
$status = git status --porcelain
if ($status) {
    git add -A
    git commit -m "Update wiki from IRScripts docs/wiki"
    git push
    Write-Host "Pushed to GitHub Wiki."
} else {
    Write-Host "No changes to push."
}
Pop-Location
