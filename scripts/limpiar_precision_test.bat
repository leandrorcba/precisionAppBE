@echo off
title Limpiar precision_test
echo ===================================================
echo Reseteando base de datos precision_test...
echo ===================================================

set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "DB_USER=root"
set "DB_PASS=Escaramujo;01"

"%MYSQL_CMD%" -u %DB_USER% -p"%DB_PASS%" -e "DROP DATABASE IF EXISTS precision_test; CREATE DATABASE precision_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo Base de datos precision_test recreada en blanco con exito!
pause
