@echo off
title Iniciando PrecisionApp
echo ===================================================
echo Verificando y cargando base de datos inicial...
echo ===================================================

set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "DB_USER=root"
set "DB_PASS=Escaramujo;01"

:: 1. Crear la base de datos precision_v2 si no existe
"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS precision_v2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

:: 2. Validar si precisionschema existe y contiene tablas
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
    
    :: Buscar dinamicamente el archivo .sql mas reciente en C:\precision_app\
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

:: 3. Definir variables de entorno requeridas por el Backend
set DB_PASSWORD=Escaramujo;01
set JWT_SECRET=28Hl0Hq1a3rFX25bNKAsP1YvCsl9TB2rc+znyJgYXfc=
set CORS_ALLOWED_ORIGINS=https://precision.lbrebolini.net,http://localhost:10081,http://localhost
set SUPER_ADMIN_PASSWORD=Escaramujo;01
set SERVER_PORT=10080

:: 4. Levantar el Backend (Spring Boot)
echo Levantando servidor Backend (Spring Boot)...
start "Backend - Spring Boot" powershell -NoProfile -Command "$host.UI.RawUI.WindowTitle = 'Backend - Spring Boot'; java '-Dserver.port=10080' '-DDB_PASSWORD=Escaramujo;01' '-DJWT_SECRET=28Hl0Hq1a3rFX25bNKAsP1YvCsl9TB2rc+znyJgYXfc=' '-DCORS_ALLOWED_ORIGINS=https://precision.lbrebolini.net,http://localhost:10081,http://localhost' '-DSUPER_ADMIN_PASSWORD=Escaramujo;01' -jar C:\precision_app\precisionAppBE.jar | Tee-Object -FilePath C:\precision_app\backend.log"

:: 5. Levantar NGINX (servidor web del Frontend)
echo Levantando servidor Frontend (NGINX)...
cd C:\nginx
start nginx.exe

echo ===================================================
echo Servidores iniciados con exito!
echo URL local: http://localhost:10081
echo URL publica: https://precision.lbrebolini.net
echo ===================================================
pause
