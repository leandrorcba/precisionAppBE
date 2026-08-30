@echo off
title PrecisionApp - Entorno de Testing E2E (precision_test)
echo ===================================================
echo Iniciando PrecisionApp en modo TEST (precision_test)
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
if not defined SERVER_PORT set "SERVER_PORT=10080"
if not defined CORS_ALLOWED_ORIGINS set "CORS_ALLOWED_ORIGINS=https://precision.lbrebolini.net,http://localhost:10081,http://localhost"
set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not defined DB_PASS (
    echo [ADVERTENCIA] No se detecto DB_PASSWORD en .env o variables de entorno.
    set /p "DB_PASS=Ingrese la clave de MySQL root: "
    set "DB_PASSWORD=%DB_PASS%"
)

:: 2. Crear schema precision_test si no existe
"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS precision_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

:: 3. Detectar Java 21
set "JAVA_EXEC=java"
if defined JAVA21_HOME (
    if exist "%JAVA21_HOME%\bin\java.exe" set "JAVA_EXEC=%JAVA21_HOME%\bin\java.exe"
) else (
    for /d %%i in ("C:\Program Files\Zulu\zulu-21*") do (
        if exist "%%i\bin\java.exe" set "JAVA_EXEC=%%i\bin\java.exe"
    )
    for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do (
        if exist "%%i\bin\java.exe" set "JAVA_EXEC=%%i\bin\java.exe"
    )
    for /d %%i in ("C:\Program Files\Java\jdk-21*") do (
        if exist "%%i\bin\java.exe" set "JAVA_EXEC=%%i\bin\java.exe"
    )
    for /d %%i in ("C:\Program Files\Microsoft\jdk-21*") do (
        if exist "%%i\bin\java.exe" set "JAVA_EXEC=%%i\bin\java.exe"
    )
)

echo Levantando Backend con schema 'precision_test'...
start "Backend TEST - Spring Boot" powershell -NoProfile -Command "$host.UI.RawUI.WindowTitle = 'Backend TEST - Spring Boot'; & '%JAVA_EXEC%' '-Dserver.port=%SERVER_PORT%' '-DDB_NAME=precision_test' '-DDB_PASSWORD=%DB_PASSWORD%' '-DJWT_SECRET=%JWT_SECRET%' '-DCORS_ALLOWED_ORIGINS=%CORS_ALLOWED_ORIGINS%' '-DSUPER_ADMIN_PASSWORD=%SUPER_ADMIN_PASSWORD%' -jar C:\precision_app\precisionAppBE.jar"

:: 4. Levantar Docker frontend
cd /d "C:\precision_app"
docker compose up -d --remove-orphans

echo ===================================================
echo Entorno de TEST iniciado exitosamente!
echo Base de Datos: precision_test
echo Frontend:      http://localhost:10081
echo ===================================================
pause
