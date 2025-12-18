Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

function Get-PlainTextFromSecureString([SecureString]$secure) {
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

if (-not $env:SONAR_TOKEN -or [string]::IsNullOrWhiteSpace($env:SONAR_TOKEN)) {
    Write-Host 'SONAR_TOKEN no está definido. Pégalo ahora (no se mostrará al escribirlo):'
    $secureToken = Read-Host -AsSecureString 'SONAR_TOKEN'
    $token = Get-PlainTextFromSecureString $secureToken

    if ([string]::IsNullOrWhiteSpace($token)) {
        throw 'No se ingresó SONAR_TOKEN.'
    }

    # Persistente para tu usuario (no imprime el token)
    setx SONAR_TOKEN $token | Out-Null

    # Disponible en esta sesión
    $env:SONAR_TOKEN = $token
}

Write-Host ''
Write-Host 'Este script ejecuta SonarCloud por módulo para evitar fallos del POM padre.'

$sonarHost = 'https://sonarcloud.io'
$sonarOrg = 'kath-valenzula'

$defaultBackendKey = 'Kath-Valenzula_seguridad-recetas'
$defaultFrontendKey = 'Kath-Valenzula_seguridad-recetas-frontend'

$backendKey = Read-Host "ProjectKey backend (ENTER = $defaultBackendKey)"
if ([string]::IsNullOrWhiteSpace($backendKey)) { $backendKey = $defaultBackendKey }

$frontendKey = Read-Host "ProjectKey frontend (ENTER = $defaultFrontendKey)"
if ([string]::IsNullOrWhiteSpace($frontendKey)) { $frontendKey = $defaultFrontendKey }

Write-Host ''
Write-Host "Analizando backend (demo) → $backendKey"
mvn -f .\demo\pom.xml sonar:sonar `
    "-Dsonar.host.url=$sonarHost" `
    "-Dsonar.organization=$sonarOrg" `
    "-Dsonar.projectKey=$backendKey" `
    "-Dsonar.token=$env:SONAR_TOKEN"

Write-Host ''
Write-Host "Analizando frontend (frontend) → $frontendKey"
mvn -f .\frontend\pom.xml sonar:sonar `
    "-Dsonar.host.url=$sonarHost" `
    "-Dsonar.organization=$sonarOrg" `
    "-Dsonar.projectKey=$frontendKey" `
    "-Dsonar.token=$env:SONAR_TOKEN"

Write-Host ''
Write-Host 'Listo. Revisa los dashboards en SonarCloud para ambos projectKeys.'
