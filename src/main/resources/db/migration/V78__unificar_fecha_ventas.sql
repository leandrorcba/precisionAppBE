-- 1. Crear la columna (Esto está perfecto)
ALTER TABLE ventas
    ADD COLUMN fecha_hora_venta DATETIME NULL AFTER id_ventas;

-- 2. Actualizar los datos de forma directa
UPDATE ventas
SET fecha_hora_venta = CAST(CONCAT(fecha_venta, ' ', hora_venta) AS DATETIME)
WHERE fecha_venta IS NOT NULL
  AND hora_venta IS NOT NULL;

-- 3. Manejar casos donde la hora sea nula (opcional)
UPDATE ventas
SET fecha_hora_venta = CAST(fecha_venta AS DATETIME)
WHERE fecha_hora_venta IS NULL
  AND fecha_venta IS NOT NULL;

ALTER TABLE ventas
    DROP COLUMN fecha_venta;
ALTER TABLE ventas
    DROP COLUMN hora_venta;