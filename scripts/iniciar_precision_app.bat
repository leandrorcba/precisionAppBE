@echo off
title Iniciando PrecisionApp
echo ===================================================
echo Verificando y cargando variables de entorno...
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

:: 2. Crear la base de datos precision_v2 si no existe
"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS precision_v2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

:: 3. Validar si precisionschema existe y contiene tablas
set "TABLE_COUNT=0"
for /f "tokens=*" %%a in ('"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -s -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'precisionschema';" 2^>nul') do (
    set "TABLE_COUNT=%%a"
)

if "%TABLE_COUNT%"=="" set "TABLE_COUNT=0"

if %TABLE_COUNT% GTR 0 (
    echo Base de datos precisionschema encontrada con %TABLE_COUNT% tablas. Omitiendo importacion del dump.
) else (
    echo Base de datos precisionschema no existe o esta vacia. Creando y buscando el archivo .sql mas reciente...
    "%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS precisionschema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    set "DUMP_FILE="
    for /f "delims=" %%f in ('dir /b /o:-d "C:\precision_app\*.sql" 2^>nul') do (
        if not defined DUMP_FILE set "DUMP_FILE=C:\precision_app\%%f"
    )

    if defined DUMP_FILE (
        echo Cargando dump de produccion %DUMP_FILE% en precisionschema...
        "%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" precisionschema < "%DUMP_FILE%"
        echo Dump importado con exito.
    ) else (
        echo [ADVERTENCIA] No se encontro ningun archivo .sql en C:\precision_app\ para importar.
    )
)

:: 4. Detectar ejecutable de Java 21 (coexistencia con Java 8 Legacy)
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

:: 5. Levantar el Backend (Spring Boot)
echo Levantando servidor Backend (Spring Boot con Java 21)...
start "Backend - Spring Boot" powershell -NoProfile -Command "$host.UI.RawUI.WindowTitle = 'Backend - Spring Boot'; & '%JAVA_EXEC%' '-Dserver.port=%SERVER_PORT%' '-DDB_PASSWORD=%DB_PASSWORD%' '-DJWT_SECRET=%JWT_SECRET%' '-DCORS_ALLOWED_ORIGINS=%CORS_ALLOWED_ORIGINS%' '-DSUPER_ADMIN_PASSWORD=%SUPER_ADMIN_PASSWORD%' -jar C:\precision_app\precisionAppBE.jar | Tee-Object -FilePath C:\precision_app\backend.log"

:: 6. Levantar Frontend NGINX, Grafana y Prometheus en Docker
echo Levantando servidores en Docker (NGINX Frontend + Grafana + Prometheus)...
cd /d "C:\precision_app"
docker compose up -d --remove-orphans

echo ===================================================
echo Servidores iniciados con exito!
echo URL Aplicacion: http://localhost:10081
echo URL Grafana:    http://localhost:30000
echo URL Publica:    https://precision.lbrebolini.net
echo ===================================================
pause
