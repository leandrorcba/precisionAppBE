-- V20260321_02__add_id_materiales_to_ventas.sql

-- 1) Agregar la nueva columna
ALTER TABLE ventas
    ADD COLUMN id_materiales INT NULL AFTER material;

-- 2) Poblar id_materiales usando el nombre del material
UPDATE ventas v
    JOIN materiales m
ON TRIM(LOWER(v.material)) = TRIM(LOWER(m.materiales))
    SET v.id_materiales = m.id_materiales
WHERE v.material IS NOT NULL
  AND v.id_materiales IS NULL;