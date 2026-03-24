-- V20260321_07__ventas_fix_invalid_cantidad.sql

UPDATE ventas
SET cantidad = 1
WHERE cantidad IS NULL
   OR cantidad <= 0;