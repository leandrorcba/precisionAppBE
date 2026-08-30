# =========================================================================
# Backup y Rotacion de Base de Datos - PrecisionApp
# =========================================================================
param(
    [string]$backupDir = "C:\precision_app\backups",
    [string]$cloudDir = ""
)

$ErrorActionPreference = "Stop"

# 1. Asegurar directorio de backups locales
if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
}

# 2. Cargar variables desde .env si existe
$envFile = Join-Path $PSScriptRoot ".env"
if (-not (Test-Path $envFile)) {
    $envFile = "C:\precision_app\.env"
}
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
            $parts = $line.Split("=", 2)
            [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
        }
    }
}

$dbUser = if ($env:DB_USER) { $env:DB_USER } else { "root" }
$dbPass = $env:DB_PASSWORD
$dbName = if ($env:DB_NAME) { $env:DB_NAME } else { "precision_v2" }

if (-not $dbPass) {
    Write-Host "[ADVERTENCIA] No se encontro DB_PASSWORD en .env ni en variables de entorno." -ForegroundColor Yellow
    $dbPass = Read-Host -Prompt "Ingrese la contrasena de MySQL root"
}

$now = Get-Date
$dateStr = $now.ToString("yyyy_MM_dd_HHmmss")
$currentMonthKey = $now.ToString("yyyy_MM")
$sqlFile = Join-Path $backupDir "$($dbName)_$dateStr.sql"
$zipFile = Join-Path $backupDir "$($dbName)_$dateStr.zip"
$mysqlDump = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"

Write-Host "Generando dump de la base de datos $dbName..." -ForegroundColor Cyan

& $mysqlDump -u $dbUser "-p$dbPass" --single-transaction --quick --routines --triggers $dbName --result-file="$sqlFile"

if (-not (Test-Path $sqlFile) -or (Get-Item $sqlFile).Length -eq 0) {
    Write-Host "[ERROR] El archivo de backup no se creo o esta vacio." -ForegroundColor Red
    exit 1
}

# 3. Comprimir a archivo .zip
Write-Host "Comprimiendo backup a formato ZIP..." -ForegroundColor Cyan
Compress-Archive -Path $sqlFile -DestinationPath $zipFile -Force
Remove-Item -Path $sqlFile -Force

$zipSize = (Get-Item $zipFile).Length / 1MB
Write-Host "Backup comprimido exitosamente: $zipFile ($([math]::Round($zipSize, 2)) MB)" -ForegroundColor Green

# 4. Sincronizacion opcional a la nube
if ($cloudDir -and (Test-Path $cloudDir)) {
    Write-Host "Sincronizando backup a almacenamiento en la nube: $cloudDir" -ForegroundColor Cyan
    Copy-Item -Path $zipFile -Destination $cloudDir -Force
    Write-Host "Sincronizacion en la nube completada." -ForegroundColor Green
}

# 5. Politica de Retencion Mensual Inteligente
Write-Host "Aplicando politica de retencion mensual inteligente..." -ForegroundColor Cyan
$allBackups = Get-ChildItem -Path $backupDir -Filter "$($dbName)_*.zip" | Sort-Object CreationTime -Descending

# Agrupar por mes
$byMonth = $allBackups | Group-Object {
    if ($_.Name -match "$($dbName)_(\d{4}_\d{2})_") { $matches[1] } else { "otros" }
}

foreach ($group in $byMonth) {
    $monthKey = $group.Name
    $items = $group.Group | Sort-Object CreationTime -Descending

    if ($monthKey -eq $currentMonthKey) {
        # Mes actual: conservar todos los backups de los ultimos 30 dias
        continue
    } else {
        # Meses anteriores: conservar el mas reciente de ese mes como 'hito mensual' y borrar los demas
        $keep = $items[0]
        $toDelete = $items | Select-Object -Skip 1
        foreach ($old in $toDelete) {
            Write-Host "Depurando backup intermedio antiguo: $($old.Name)" -ForegroundColor DarkGray
            Remove-Item -Path $old.FullName -Force
        }
    }
}

Write-Host "Proceso de backup y rotacion finalizado con exito." -ForegroundColor Green
