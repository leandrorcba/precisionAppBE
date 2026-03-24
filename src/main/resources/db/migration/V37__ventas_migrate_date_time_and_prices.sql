-- V20260321_02__ventas_migrate_date_time_and_prices.sql

UPDATE ventas
SET fecha_venta = STR_TO_DATE(TRIM(fechaventa), '%d/%m/%Y')
WHERE fechaventa IS NOT NULL
  AND TRIM(fechaventa) <> ''
  AND fecha_venta IS NULL;

UPDATE ventas
SET hora_venta = STR_TO_DATE(TRIM(horaventa), '%H:%i:%s')
WHERE horaventa IS NOT NULL
  AND TRIM(horaventa) <> ''
  AND hora_venta IS NULL;

UPDATE ventas
SET precio_material = ROUND(preciomaterial, 2)
WHERE preciomaterial IS NOT NULL
  AND precio_material IS NULL;

UPDATE ventas
SET precio_venta = ROUND(precioventa, 2)
WHERE precioventa IS NOT NULL
  AND precio_venta IS NULL;