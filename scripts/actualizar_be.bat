@echo off
title Actualizando Backend - PrecisionApp
echo ===================================================
echo 1. Compilando Backend (Spring Boot)...
echo ===================================================
cd /d C:\Users\LEandro\Documents\development\MigracionPrecision\precisionAppBE
call .\gradlew.bat bootJar

echo ===================================================
echo 2. Copiando ejecutable .jar a la carpeta de produccion...
echo ===================================================
copy /Y build\libs\precisionAppBE-0.0.1-SNAPSHOT.jar C:\precision_app\precisionAppBE.jar

echo ===================================================
echo Backend compilado y copiado con exito!
echo Recuerda reiniciar el servicio para aplicar los cambios.
echo ===================================================
pause
