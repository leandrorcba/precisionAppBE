@echo off
title Deteniendo PrecisionApp
echo ===================================================
echo 1. Realizando backup preventivo de la base de datos...
echo ===================================================
if exist "%~dp0backup_db.bat" (
    call "%~dp0backup_db.bat"
) else if exist "C:\precision_app\backup_db.bat" (
    call "C:\precision_app\backup_db.bat"
)

echo ===================================================
echo 2. Deteniendo Backend (Spring Boot / Java)...
echo ===================================================
taskkill /F /IM java.exe 2>nul
taskkill /F /FI "WINDOWTITLE eq Backend - Spring Boot*" 2>nul

echo ===================================================
echo 3. Deteniendo Frontend (NGINX)...
echo ===================================================
taskkill /F /IM nginx.exe 2>nul

echo ===================================================
echo Todos los servicios han sido detenidos con exito!
echo Cerrando en 3 segundos...
echo ===================================================
timeout /t 3 >nul
