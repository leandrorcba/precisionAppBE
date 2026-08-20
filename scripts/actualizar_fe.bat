@echo off
title Actualizando Frontend - PrecisionApp
echo ===================================================
echo 1. Compilando Frontend (React)...
echo ===================================================
cd /d C:\Users\LEandro\Documents\development\MigracionPrecision\presision-app
call npm run build

echo ===================================================
echo 2. Copiando archivos compilados a la carpeta de produccion...
echo ===================================================
xcopy /E /I /Y dist C:\precision_app\frontend

echo ===================================================
echo Frontend actualizado con exito!
echo Haz Ctrl+F5 en tu navegador para ver los cambios.
echo ===================================================
pause
