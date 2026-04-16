-- 1. UNIFICACIÓN DE PEGAMENTOS Y QUÍMICOS
UPDATE trabajo_presupuestado
SET id_materiales = 125
WHERE id_materiales = 53; -- Gotita
UPDATE precio_materiales
SET id_materiales = 125
WHERE id_materiales = 53;

UPDATE trabajo_presupuestado
SET id_materiales = 126
WHERE id_materiales = 54; -- Gotita Gel
UPDATE precio_materiales
SET id_materiales = 126
WHERE id_materiales = 54;

UPDATE trabajo_presupuestado
SET id_materiales = 127
WHERE id_materiales = 60; -- Unipox Chico
UPDATE precio_materiales
SET id_materiales = 127
WHERE id_materiales = 60;

UPDATE trabajo_presupuestado
SET id_materiales = 159
WHERE id_materiales = 61; -- Unipox Grande
UPDATE precio_materiales
SET id_materiales = 159
WHERE id_materiales = 61;

UPDATE trabajo_presupuestado
SET id_materiales = 162
WHERE id_materiales = 59; -- Cola Pritt
UPDATE precio_materiales
SET id_materiales = 162
WHERE id_materiales = 59;

-- 2. UNIFICACIÓN DE CINTAS
UPDATE trabajo_presupuestado
SET id_materiales = 133
WHERE id_materiales = 58; -- Cinta Embalar
UPDATE precio_materiales
SET id_materiales = 133
WHERE id_materiales = 58;

UPDATE trabajo_presupuestado
SET id_materiales = 165
WHERE id_materiales = 56; -- Cinta Papel 36mm
UPDATE precio_materiales
SET id_materiales = 165
WHERE id_materiales = 56;

UPDATE trabajo_presupuestado
SET id_materiales = 164
WHERE id_materiales = 57; -- Cinta Papel 18mm
UPDATE precio_materiales
SET id_materiales = 164
WHERE id_materiales = 57;

-- 3. UNIFICACIÓN DE MDF Y PLÁSTICOS
UPDATE trabajo_presupuestado
SET id_materiales = 91
WHERE id_materiales = 11; -- MDF 1mm
UPDATE precio_materiales
SET id_materiales = 91
WHERE id_materiales = 11;

UPDATE trabajo_presupuestado
SET id_materiales = 92
WHERE id_materiales = 12; -- MDF 1,5mm
UPDATE precio_materiales
SET id_materiales = 92
WHERE id_materiales = 12;

UPDATE trabajo_presupuestado
SET id_materiales = 93
WHERE id_materiales = 13; -- MDF 2mm
UPDATE precio_materiales
SET id_materiales = 93
WHERE id_materiales = 13;

UPDATE trabajo_presupuestado
SET id_materiales = 170
WHERE id_materiales = 42; -- Polipropileno
UPDATE precio_materiales
SET id_materiales = 170
WHERE id_materiales = 42;

-- 4. CASO ESPECIAL: NO CATALOGADO (Unificamos 139 y 181 hacia el 158)
UPDATE trabajo_presupuestado
SET id_materiales = 158
WHERE id_materiales IN (139, 181);
UPDATE precio_materiales
SET id_materiales = 158
WHERE id_materiales IN (139, 181);
