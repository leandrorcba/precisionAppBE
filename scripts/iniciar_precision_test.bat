@echo off
title PrecisionApp - Entorno de Testing E2E (precision_test)
echo ===================================================
echo Iniciando PrecisionApp en modo TEST (precision_test)
echo ===================================================

set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "DB_USER=root"
set "DB_PASS=Escaramujo;01"

:: 1. Crear schema precision_test si no existe
"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS precision_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

:: 2. Variables de entorno para modo TEST
set DB_NAME=precision_test
set DB_PASSWORD=Escaramujo;01
set JWT_SECRET=28Hl0Hq1a3rFX25bNKAsP1YvCsl9TB2rc+znyJgYXfc=
set CORS_ALLOWED_ORIGINS=https://precision.lbrebolini.net,http://localhost:10081,http://localhost
set SUPER_ADMIN_PASSWORD=Escaramujo;01
set SERVER_PORT=10080

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
start "Backend TEST - Spring Boot" powershell -NoProfile -Command "$host.UI.RawUI.WindowTitle = 'Backend TEST - Spring Boot'; & '%JAVA_EXEC%' '-Dserver.port=10080' '-DDB_NAME=precision_test' '-DDB_PASSWORD=Escaramujo;01' '-DJWT_SECRET=28Hl0Hq1a3rFX25bNKAsP1YvCsl9TB2rc+znyJgYXfc=' '-DCORS_ALLOWED_ORIGINS=https://precision.lbrebolini.net,http://localhost:10081,http://localhost' '-DSUPER_ADMIN_PASSWORD=Escaramujo;01' -jar C:\precision_app\precisionAppBE.jar"

:: 4. Levantar Docker frontend
cd /d "C:\precision_app"
docker compose up -d --remove-orphans

echo ===================================================
echo Entorno de TEST iniciado exitosamente!
echo Base de Datos: precision_test
echo Frontend:      http://localhost:10081
echo ===================================================
pause
