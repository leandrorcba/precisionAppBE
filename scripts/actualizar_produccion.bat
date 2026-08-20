@echo off
setlocal enabledelayedexpansion
title Actualizar PrecisionApp desde Main (Produccion)

echo ===================================================
echo   ACTUALIZACION DE PRECISION APP - RAMA MAIN
echo ===================================================

:: 1. Definir rutas
set "PROD_DIR=C:\precision_app"

:: Detectar ruta del repositorio Backend
if exist "%~dp0..\build.gradle" (
    set "BE_DIR=%~dp0.."
) else if exist "C:\precision_app\precisionAppBE\build.gradle" (
    set "BE_DIR=C:\precision_app\precisionAppBE"
) else (
    set "BE_DIR=C:\Users\LEandro\Documents\development\MigracionPrecision\precisionAppBE"
)

:: Detectar ruta del repositorio Frontend
if exist "%BE_DIR%\..\presision-app\package.json" (
    set "FE_DIR=%BE_DIR%\..\presision-app"
) else if exist "C:\precision_app\presision-app\package.json" (
    set "FE_DIR=C:\precision_app\presision-app"
) else (
    set "FE_DIR=C:\Users\LEandro\Documents\development\MigracionPrecision\presision-app"
)

echo Directorio Produccion : %PROD_DIR%
echo Repositorio Backend   : %BE_DIR%
echo Repositorio Frontend  : %FE_DIR%
echo ===================================================
echo.

:: 2. Detener servicios en ejecucion
echo [1/6] Deteniendo servicios en ejecucion...
if exist "%PROD_DIR%\detener_precision_app.bat" (
    call "%PROD_DIR%\detener_precision_app.bat"
) else (
    taskkill /F /IM java.exe 2>nul
    taskkill /F /FI "WINDOWTITLE eq Backend - Spring Boot*" 2>nul
    taskkill /F /IM nginx.exe 2>nul
)
echo Servicios detenidos.
echo.

:: 3. Actualizar Backend desde Git (rama main)
echo [2/6] Actualizando Backend desde git origin/main...
cd /d "%BE_DIR%"
git fetch origin main
if %errorlevel% neq 0 (
    echo [ERROR] No se pudo conectar a GitHub o fallo git fetch en Backend.
    pause
    exit /b 1
)
git checkout main
git pull origin main
if %errorlevel% neq 0 (
    echo [ERROR] Fallo git pull en Backend.
    pause
    exit /b 1
)
echo Backend actualizado a la ultima version de main.
echo.

:: 4. Compilar Backend
echo [3/6] Compilando Backend (Spring Boot)...
call .\gradlew.bat bootJar
if %errorlevel% neq 0 (
    echo [ERROR] Error durante la compilacion del Backend.
    pause
    exit /b 1
)

:: Copiar JAR a produccion
echo Copiando precisionAppBE.jar a %PROD_DIR%...
if not exist "%PROD_DIR%" mkdir "%PROD_DIR%"
copy /Y "build\libs\precisionAppBE-0.0.1-SNAPSHOT.jar" "%PROD_DIR%\precisionAppBE.jar"
echo Backend copiado exitosamente.
echo.

:: 5. Actualizar Frontend desde Git (rama main)
echo [4/6] Actualizando Frontend desde git origin/main...
cd /d "%FE_DIR%"
git fetch origin main
if %errorlevel% neq 0 (
    echo [ERROR] No se pudo conectar a GitHub o fallo git fetch en Frontend.
    pause
    exit /b 1
)
git checkout main
git pull origin main
if %errorlevel% neq 0 (
    echo [ERROR] Fallo git pull en Frontend.
    pause
    exit /b 1
)
echo Frontend actualizado a la ultima version de main.
echo.

:: 6. Compilar Frontend
echo [5/6] Compilando Frontend (React/Vite)...
call npm run build
if %errorlevel% neq 0 (
    echo [ERROR] Error durante la compilacion del Frontend.
    pause
    exit /b 1
)

:: Copiar Frontend a produccion
echo Copiando archivos compilados a %PROD_DIR%\frontend...
if not exist "%PROD_DIR%\frontend" mkdir "%PROD_DIR%\frontend"
xcopy /E /I /Y "dist" "%PROD_DIR%\frontend"
echo Frontend copiado exitosamente.
echo.

:: 7. Iniciar los servicios nuevamente
echo [6/6] Reiniciando servicios de PrecisionApp...
cd /d "%PROD_DIR%"
if exist "%PROD_DIR%\iniciar_precision_app.bat" (
    call "%PROD_DIR%\iniciar_precision_app.bat"
) else (
    echo Inicie los servicios manualmente con iniciar_precision_app.bat.
)

echo ===================================================
echo   ACTUALIZACION COMPLETADA CON EXITO!
echo ===================================================
pause
