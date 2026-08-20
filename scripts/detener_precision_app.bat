@echo off
title Deteniendo PrecisionApp
echo ===================================================
echo Deteniendo Backend (Spring Boot / Java)...
echo ===================================================
taskkill /F /IM java.exe 2>nul
taskkill /F /FI "WINDOWTITLE eq Backend - Spring Boot*" 2>nul

echo ===================================================
echo Deteniendo Frontend (NGINX)...
echo ===================================================
taskkill /F /IM nginx.exe 2>nul

echo ===================================================
echo Todos los servicios han sido detenidos con exito!
echo Cerrando en 2 segundos...
echo ===================================================
timeout /t 2 >nul
