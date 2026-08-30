@echo off
title Limpiar precision_test
echo ===================================================
echo Reseteando base de datos precision_test...
echo ===================================================

:: 1. Cargar variables desde .env si existe
if exist "%~dp0.env" (
    for /f "usebackq tokens=1,* delims==" %%a in ("%~dp0.env") do (
        if not "%%a"=="" if not "%%a:~0,1%"=="#" set "%%a=%%b"
    )
) else if exist "C:\precision_app\.env" (
    for /f "usebackq tokens=1,* delims==" %%a in ("C:\precision_app\.env") do (
        if not "%%a"=="" if not "%%a:~0,1%"=="#" set "%%a=%%b"
    )
)

if not defined DB_USER set "DB_USER=root"
if not defined DB_PASS set "DB_PASS=%DB_PASSWORD%"
set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not defined DB_PASS (
    echo [ADVERTENCIA] No se detecto DB_PASSWORD en .env o variables de entorno.
    set /p "DB_PASS=Ingrese la clave de MySQL root: "
)

"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "DROP DATABASE IF EXISTS precision_test; CREATE DATABASE precision_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo Base de datos precision_test recreada en blanco con exito!
pause
