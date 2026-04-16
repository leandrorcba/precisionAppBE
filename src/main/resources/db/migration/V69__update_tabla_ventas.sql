-- Unificación de Ventas (MDF)
UPDATE ventas
SET id_materiales = 91
WHERE id_materiales = 11;
UPDATE ventas
SET id_materiales = 92
WHERE id_materiales = 12;
UPDATE ventas
SET id_materiales = 93
WHERE id_materiales = 13;

-- Unificación de Ventas (Insumos y Pegamentos)
UPDATE ventas
SET id_materiales = 66
WHERE id_materiales = 25; -- Carton Blanco 2mm
UPDATE ventas
SET id_materiales = 125
WHERE id_materiales = 53; -- Gotita
UPDATE ventas
SET id_materiales = 126
WHERE id_materiales = 54; -- Gotita Gel
UPDATE ventas
SET id_materiales = 127
WHERE id_materiales = 60; -- Unipox Chico
UPDATE ventas
SET id_materiales = 159
WHERE id_materiales = 61; -- Unipox Grande
UPDATE ventas
SET id_materiales = 162
WHERE id_materiales = 59;
-- Cola Pritt

-- Unificación de Ventas (Cintas)
UPDATE ventas
SET id_materiales = 133
WHERE id_materiales = 58; -- Cinta Embalar
UPDATE ventas
SET id_materiales = 165
WHERE id_materiales = 56; -- Cinta Papel 36mm
UPDATE ventas
SET id_materiales = 164
WHERE id_materiales = 57;
-- Cinta Papel 18mm

-- Otros
UPDATE ventas
SET id_materiales = 170
WHERE id_materiales = 42; -- Polipropileno
UPDATE ventas
SET id_materiales = 158
WHERE id_materiales IN (139, 181);
-- No Catalogados
-- 2. Ahora sí, el DELETE no debería fallar
DELETE
FROM materiales
WHERE id_materiales IN (11, 12, 13, 25, 53, 54, 60, 61, 59, 58, 56, 57, 42, 139, 181);

-- 5. ELIMINACIÓN DE LOS IDs DUPLICADOS (MAESTRO)
DELETE
FROM materiales
WHERE id_materiales IN (53, 54, 60, 61, 59, 58, 56, 57, 11, 12, 13, 42, 139, 181);