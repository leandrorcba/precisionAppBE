-- =============================================================
-- V6__import_precios_materiales.sql
-- is_material=1  → 1 fila por superficie (4 filas por material)
-- is_grabado=1 o resto → 1 fila por unidad
-- Todos los precios iniciales = 1.00
-- =============================================================

-- Materiales con superficie (is_material = 1)
INSERT INTO `precio_materiales` (`id_materiales`, `id_superficie`, `superficie`, `precio_material`)
SELECT m.`id_materiales`, s.`id_superficie`, s.`valor`, 1.00
FROM `materiales` m
CROSS JOIN `superficies` s
WHERE m.`is_material` = 1;

-- Materiales por unidad (is_grabado = 1 o is_material = 0)
INSERT INTO `precio_materiales` (`id_materiales`, `unidades`, `precio_material`)
SELECT m.`id_materiales`, 1, 1.00
FROM `materiales` m
WHERE m.`is_material` = 0;