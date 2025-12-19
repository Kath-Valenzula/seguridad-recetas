Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

$frontendUrl = 'http://host.docker.internal:8081'
$reportsDir = Resolve-Path .\zap-reports

Write-Host "Ejecutando ZAP Baseline contra: $frontendUrl"
Write-Host "Reportes en: $reportsDir"

# Nota: si hay WARN-NEW, ZAP suele devolver exit code 1.
# Para no fallar el script por eso, capturamos el código y lo mostramos.

docker run --rm -t -v "${reportsDir}:/zap/wrk" zaproxy/zap-stable \
  zap-baseline.py -t $frontendUrl -m 2 \
  -r zap-report.html -J zap-report.json -w zap-warn.md -x zap-report.xml

$exit = $LASTEXITCODE
Write-Host "ZAP exit code: $exit"

Write-Host ''
Write-Host 'Listo. Archivos generados:'
Get-ChildItem -Path $reportsDir -File | Select-Object Name,Length,LastWriteTime | Format-Table -AutoSize

exit $exit
