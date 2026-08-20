@echo off
setlocal enabledelayedexpansion
title Backup Base de Datos - PrecisionApp

echo ===================================================
echo   BACKUP Y ROTACION DE BASE DE DATOS
echo ===================================================

:: 1. Directorio local de backups
set "BACKUP_DIR=C:\precision_app\backups"

:: 2. Carpeta configurable para sincronizacion en la nube (Google Drive / OneDrive / Dropbox / Disco Externo)
:: Descomenta y configura la ruta deseada si quieres sincronizar automaticamente:
:: set "CLOUD_DIR=C:\Users\LEandro\Google Drive\PrecisionAppBackups"
:: set "CLOUD_DIR=C:\Users\LEandro\OneDrive\PrecisionAppBackups"
set "CLOUD_DIR="

:: 3. Ejecutar script de PowerShell
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0backup_db.ps1" -backupDir "%BACKUP_DIR%" -cloudDir "%CLOUD_DIR%"

echo ===================================================
