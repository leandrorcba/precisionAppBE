-- 1) Agregar columnas nuevas para backfill
ALTER TABLE ventas
    ADD COLUMN fecha_hora_venta DATETIME NULL,
  ADD COLUMN id_material INT NULL,
  ADD COLUMN id_insumos INT NULL,
  ADD COLUMN precio_material DECIMAL(10,2) NULL,
  ADD COLUMN precio_venta DECIMAL(10,2) NULL;

-- 2) Backfill de fecha y hora (formato esperado: 27/01/2025 y 05:26:24)
UPDATE ventas
SET fecha_hora_venta = STR_TO_DATE(
        CONCAT(TRIM(fechaventa), ' ', COALESCE(NULLIF(TRIM(horaventa), ''), '00:00:00')),
        '%d/%m/%Y %H:%i:%s'
                       ) where idVentas > 0;

-- 3) Mapear material (texto) -> id_material (ajusta los nombres según tu tabla de materiales)
-- Variante A: materiales(id_material, material)
UPDATE ventas v
    JOIN materiales m ON UPPER(TRIM(m.materiales)) = UPPER(TRIM(v.material))
    SET v.id_material = m.id_materiales  where idVentas > 0;


-- 3) Mapear insumo (texto) -> id_material (ajusta los nombres según tu tabla de materiales)
-- Variante A: materiales(id_material, material)
/*UPDATE ventas v
    JOIN insumos m ON UPPER(TRIM(m.nombre_insumo)) = UPPER(TRIM(v.material))
    SET v.id_insumos = m.id_insumos  where idVentas > 0;*/

-- Variante B (si tu esquema es id_materiales/materiales):
-- UPDATE ventas v
-- JOIN materiales m ON UPPER(TRIM(m.materiales)) = UPPER(TRIM(v.material))
-- SET v.id_material = m.id_materiales;

-- 4) Mapear superficie (texto) -> id_superficie
-- Suponiendo superficies(id_superficie, superficie)
-- UPDATE ventas v
-- JOIN superficies s ON UPPER(TRIM(s.superficie)) = UPPER(TRIM(v.superficie))
-- SET v.id_superficie = s.id_superficie;

-- 5) Normalizar precios a DECIMAL(10,2)
UPDATE ventas
SET precio_material = ROUND(preciomaterial, 2),
    precio_venta    = ROUND(precioventa, 2)   where idVentas > 0;

-- 6) Validaciones rápidas (registros que NO mapearon bien)
-- Materiales sin ID
-- SELECT v.id_venta, v.material
-- FROM ventas v
-- WHERE v.id_material IS NULL AND v.material IS NOT NULL;

-- Superficies sin ID
-- SELECT v.id_venta, v.superficie
-- FROM ventas v
-- WHERE v.id_superficie IS NULL AND v.superficie IS NOT NULL;

-- Fechas que no parsearon
/*SELECT v.idventas, v.fechaventa, v.horaventa
FROM ventas v
WHERE v.fecha_hora_venta IS NULL AND (v.fechaventa IS NOT NULL OR v.horaventa IS NOT NULL);*/

-- borro la columna superficie
alter table ventas
drop superficie,
drop fechaventa,
drop preciomaterial,
drop precioventa,
drop horaventa;


-- uso esto para saber que me quedo sin migrar de nombre a id;
-- select * from ventas where id_insumos is null and id_material is null;

#FALTAN AGREGAR VARIOS ELEMENTOS A LOS INSUMOS


CREATE TABLE venta_detalle (
                               id_venta_detalle INT AUTO_INCREMENT PRIMARY KEY,
                               id_venta         INT NOT NULL,
                               material         VARCHAR(45) NOT NULL,
                               superficie       VARCHAR(45) NULL,
                               precio_material  DECIMAL(10,2) NULL,                  -- de tu columna actual
                               cantidad         INT NULL,
                               precio_unitario  DECIMAL(10,2) NULL,
                               precio_total     DECIMAL(10,2) NOT NULL);              -- total de la línea

ALTER TABLE ventas
    CHANGE COLUMN idventas id_ventas INT NOT NULL AUTO_INCREMENT ;