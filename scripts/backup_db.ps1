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

$now = Get-Date
$dateStr = $now.ToString("yyyy_MM_dd_HHmmss")
$currentMonthKey = $now.ToString("yyyy_MM")
$sqlFile = Join-Path $backupDir "precision_v2_$dateStr.sql"
$zipFile = Join-Path $backupDir "precision_v2_$dateStr.zip"
$mysqlDump = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"

Write-Host "Generando dump de la base de datos precision_v2..." -ForegroundColor Cyan

& $mysqlDump -u root -p"Escaramujo;01" --single-transaction --quick --routines --triggers precision_v2 --result-file="$sqlFile"

if (-not (Test-Path $sqlFile) -or (Get-Item $sqlFile).Length -eq 0) {
    Write-Host "[ERROR] El archivo de backup no se creo o esta vacio." -ForegroundColor Red
    exit 1
}

# 2. Comprimir a formato .zip
Write-Host "Comprimiendo archivo de backup..." -ForegroundColor Cyan
Compress-Archive -Path $sqlFile -DestinationPath $zipFile -Force
Remove-Item -Path $sqlFile -Force

$zipItem = Get-Item $zipFile
$sizeKB = [math]::Round($zipItem.Length / 1KB, 2)
Write-Host "-> Backup generado con exito: $($zipItem.Name) ($sizeKB KB)" -ForegroundColor Green

# 3. Sincronizacion con Carpeta de la Nube (Google Drive / OneDrive)
if ($cloudDir -and $cloudDir.Trim() -ne "") {
    if (Test-Path $cloudDir) {
        Copy-Item -Path $zipFile -Destination $cloudDir -Force
        Write-Host "-> Copia sincronizada a la nube: $cloudDir" -ForegroundColor Green
    } else {
        Write-Host "[AVISO] La carpeta de nube configurada ($cloudDir) no existe o no esta accesible." -ForegroundColor Yellow
    }
}

# 4. Politica de Retencion y Rotacion Inteligente
# - Mes Actual: Se conservan todos los backups diarios.
# - Meses Anteriores: Se conserva solo el ultimo backup de cada mes (cierre mensual) y se eliminan los intermedios.
Write-Host "Verificando politica de retencion de backups..." -ForegroundColor Cyan

$allZips = Get-ChildItem -Path $backupDir -Filter "precision_v2_*.zip"
$grouped = $allZips | Where-Object { $_.Name -match '^precision_v2_(\d{4}_\d{2})' } | Group-Object { $Matches[1] }

foreach ($group in $grouped) {
    $monthKey = $group.Name
    if ($monthKey -ne $currentMonthKey) {
        # Ordenar por nombre (o fecha) descendente
        $sorted = $group.Group | Sort-Object Name -Descending
        # El primero es el mas reciente (ultimo del mes) -> se preserva
        $preserved = $sorted[0]
        Write-Host "Mes anterior ($monthKey): preservando backup final de mes ($($preserved.Name))" -ForegroundColor Gray
        
        # Eliminar backups intermedios del mes anterior
        $toDelete = $sorted | Select-Object -Skip 1
        foreach ($f in $toDelete) {
            Write-Host "  -> Eliminando backup intermedio antiguo: $($f.Name)" -ForegroundColor Yellow
            Remove-Item -Path $f.FullName -Force
        }
    }
}

Write-Host "Operacion de backup y rotacion finalizada." -ForegroundColor Green
