-- =============================================================
-- SCRIPT DE DIAGNÓSTICO DE INTEGRIDAD — precision_schema_v2
-- Correr contra la base real. Solo SELECTs, no modifica nada.
-- Resultado: COUNT(*) = 0 en cada sección significa OK.
-- =============================================================

USE precision_schema_v2;

-- =============================================================
-- 1. HUÉRFANOS EN precio_materiales → materiales
-- =============================================================
SELECT
    'precio_materiales → materiales' AS relacion,
    COUNT(*)                          AS registros_huerfanos
FROM precio_materiales pm
LEFT JOIN materiales m ON m.id_materiales = pm.id_materiales
WHERE pm.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL;

-- Detalle:
SELECT pm.id_precio_materiales, pm.id_materiales
FROM precio_materiales pm
LEFT JOIN materiales m ON m.id_materiales = pm.id_materiales
WHERE pm.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL
LIMIT 50;

-- =============================================================
-- 2. HUÉRFANOS EN trabajo_presupuestado → materiales
-- =============================================================
SELECT
    'trabajo_presupuestado → materiales' AS relacion,
    COUNT(*)                              AS registros_huerfanos
FROM trabajo_presupuestado tp
LEFT JOIN materiales m ON m.id_materiales = tp.id_materiales
WHERE tp.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL;

-- Detalle:
SELECT tp.id_trabajo_presupuestado, tp.id_materiales, tp.material
FROM trabajo_presupuestado tp
LEFT JOIN materiales m ON m.id_materiales = tp.id_materiales
WHERE tp.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL
LIMIT 50;

-- =============================================================
-- 3. HUÉRFANOS EN trabajo_presupuestado → presupuesto
-- =============================================================
SELECT
    'trabajo_presupuestado → presupuesto' AS relacion,
    COUNT(*)                               AS registros_huerfanos
FROM trabajo_presupuestado tp
LEFT JOIN presupuesto p ON p.id_presupuesto = tp.id_presupuesto
WHERE p.id_presupuesto IS NULL;

-- =============================================================
-- 4. HUÉRFANOS EN trabajo_presupuestado → superficies
-- =============================================================
SELECT
    'trabajo_presupuestado → superficies' AS relacion,
    COUNT(*)                               AS registros_huerfanos
FROM trabajo_presupuestado tp
LEFT JOIN superficies s ON s.id_superficie = tp.id_superficie
WHERE tp.id_superficie IS NOT NULL
  AND s.id_superficie IS NULL;

-- =============================================================
-- 5. HUÉRFANOS EN ventas → materiales (columna id_materiales)
-- =============================================================
SELECT
    'ventas.id_materiales → materiales' AS relacion,
    COUNT(*)                             AS registros_huerfanos
FROM ventas v
LEFT JOIN materiales m ON m.id_materiales = v.id_materiales
WHERE v.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL;

-- =============================================================
-- 6. COHERENCIA ventas: id_material vs id_materiales (columna duplicada)
-- =============================================================
SELECT
    'ventas — solo id_material poblado (columna vieja)'       AS caso,
    COUNT(*) AS cantidad
FROM ventas
WHERE id_material IS NOT NULL AND id_materiales IS NULL

UNION ALL

SELECT
    'ventas — solo id_materiales poblado (columna nueva)'     AS caso,
    COUNT(*) AS cantidad
FROM ventas
WHERE id_materiales IS NOT NULL AND id_material IS NULL

UNION ALL

SELECT
    'ventas — ambas pobladas pero distintas (mismatch)'       AS caso,
    COUNT(*) AS cantidad
FROM ventas
WHERE id_material IS NOT NULL
  AND id_materiales IS NOT NULL
  AND id_material != id_materiales

UNION ALL

SELECT
    'ventas — ninguna poblada (sin material)'                  AS caso,
    COUNT(*) AS cantidad
FROM ventas
WHERE id_material IS NULL AND id_materiales IS NULL;

-- =============================================================
-- 7. trabajo_presupuestado: cobertura de id_materiales
--    columna 'material' (texto legacy) ya fue eliminada por V67
-- =============================================================
SELECT
    'trabajo_presupuestado — con id_materiales'    AS caso,
    COUNT(*) AS cantidad
FROM trabajo_presupuestado
WHERE id_materiales IS NOT NULL

UNION ALL

SELECT
    'trabajo_presupuestado — sin id_materiales (NULL)' AS caso,
    COUNT(*) AS cantidad
FROM trabajo_presupuestado
WHERE id_materiales IS NULL;

-- =============================================================
-- 8. HUÉRFANOS pago_venta → ventas (FK faltante)
-- =============================================================
SELECT
    'pago_venta → ventas' AS relacion,
    COUNT(*)               AS registros_huerfanos
FROM pago_venta pv
LEFT JOIN ventas v ON v.id_ventas = pv.id_venta
WHERE pv.id_venta IS NOT NULL
  AND v.id_ventas IS NULL;

-- =============================================================
-- 9. HUÉRFANOS presupuesto → clientes
-- =============================================================
SELECT
    'presupuesto → clientes' AS relacion,
    COUNT(*)                  AS registros_huerfanos
FROM presupuesto p
LEFT JOIN clientes c ON c.id_cliente = p.id_cliente
WHERE p.id_cliente IS NOT NULL
  AND c.id_cliente IS NULL;

-- =============================================================
-- 11. HUÉRFANOS control_puntos → presupuesto
-- =============================================================
SELECT
    'control_puntos → presupuesto' AS relacion,
    COUNT(*)                        AS registros_huerfanos
FROM control_puntos cp
LEFT JOIN presupuesto p ON p.id_presupuesto = cp.id_presupuesto
WHERE cp.id_presupuesto IS NOT NULL
  AND p.id_presupuesto IS NULL;

-- =============================================================
-- 12. HUÉRFANOS puntos → clientes
-- =============================================================
SELECT
    'puntos → clientes' AS relacion,
    COUNT(*)             AS registros_huerfanos
FROM puntos pu
LEFT JOIN clientes c ON c.id_cliente = pu.id_cliente
WHERE pu.id_cliente IS NOT NULL
  AND c.id_cliente IS NULL;

-- =============================================================
-- 13. HUÉRFANOS pago_presupuesto → presupuesto
-- =============================================================
SELECT
    'pago_presupuesto → presupuesto' AS relacion,
    COUNT(*)                          AS registros_huerfanos
FROM pago_presupuesto pp
LEFT JOIN presupuesto p ON p.id_presupuesto = pp.id_presupuesto
WHERE p.id_presupuesto IS NULL;

-- =============================================================
-- 14. HUÉRFANOS descuento → presupuesto / trabajo_presupuestado
-- =============================================================
SELECT
    'descuento → presupuesto' AS relacion,
    COUNT(*)                   AS registros_huerfanos
FROM descuento d
LEFT JOIN presupuesto p ON p.id_presupuesto = d.id_presupuesto
WHERE p.id_presupuesto IS NULL

UNION ALL

SELECT
    'descuento → trabajo_presupuestado' AS relacion,
    COUNT(*)                             AS registros_huerfanos
FROM descuento d
LEFT JOIN trabajo_presupuestado tp ON tp.id_trabajo_presupuestado = d.id_trabajo_presupuestado
WHERE d.id_trabajo_presupuestado IS NOT NULL
  AND tp.id_trabajo_presupuestado IS NULL;

-- =============================================================
-- 15. FKs existentes en el esquema (verificación estructural)
-- =============================================================
SELECT
    kcu.table_name,
    kcu.column_name,
    kcu.referenced_table_name,
    kcu.referenced_column_name,
    rc.delete_rule,
    rc.update_rule
FROM information_schema.key_column_usage kcu
JOIN information_schema.referential_constraints rc
    ON rc.constraint_name = kcu.constraint_name
    AND rc.constraint_schema = kcu.constraint_schema
WHERE kcu.constraint_schema = 'precision_schema_v2'
ORDER BY kcu.table_name, kcu.column_name;

-- =============================================================
-- 16. COLUMNAS id_xxx SIN FK (candidatas a tener constraint)
--     Muestra columnas que parecen FK pero no tienen constraint
-- =============================================================
SELECT
    c.table_name,
    c.column_name,
    c.column_type,
    c.is_nullable
FROM information_schema.columns c
LEFT JOIN information_schema.key_column_usage kcu
    ON kcu.table_schema = c.table_schema
    AND kcu.table_name  = c.table_name
    AND kcu.column_name = c.column_name
    AND kcu.referenced_table_name IS NOT NULL
WHERE c.table_schema = 'precision_schema_v2'
  AND c.column_name LIKE 'id_%'
  AND kcu.column_name IS NULL
ORDER BY c.table_name, c.column_name;

-- =============================================================
-- 17. RESUMEN GENERAL — una sola vista rápida
-- =============================================================
SELECT resultado FROM (
    SELECT 1 AS ord, CONCAT('precio_materiales huérfanos: ', COUNT(*))            AS resultado FROM precio_materiales pm LEFT JOIN materiales m ON m.id_materiales = pm.id_materiales WHERE pm.id_materiales IS NOT NULL AND m.id_materiales IS NULL
    UNION ALL
    SELECT 2, CONCAT('trabajo_presupuestado → materiales huérfanos: ', COUNT(*))  FROM trabajo_presupuestado tp LEFT JOIN materiales m ON m.id_materiales = tp.id_materiales WHERE tp.id_materiales IS NOT NULL AND m.id_materiales IS NULL
    UNION ALL
    SELECT 3, CONCAT('trabajo_presupuestado → presupuesto huérfanos: ', COUNT(*)) FROM trabajo_presupuestado tp LEFT JOIN presupuesto p ON p.id_presupuesto = tp.id_presupuesto WHERE p.id_presupuesto IS NULL
    UNION ALL
    SELECT 4, CONCAT('trabajo_presupuestado → superficies huérfanos: ', COUNT(*)) FROM trabajo_presupuestado tp LEFT JOIN superficies s ON s.id_superficie = tp.id_superficie WHERE tp.id_superficie IS NOT NULL AND s.id_superficie IS NULL
    UNION ALL
    SELECT 5, CONCAT('ventas → materiales huérfanos: ', COUNT(*))                 FROM ventas v LEFT JOIN materiales m ON m.id_materiales = v.id_materiales WHERE v.id_materiales IS NOT NULL AND m.id_materiales IS NULL
    UNION ALL
    SELECT 6, CONCAT('pago_venta → ventas huérfanos: ', COUNT(*))                 FROM pago_venta pv LEFT JOIN ventas v ON v.id_ventas = pv.id_venta WHERE pv.id_venta IS NOT NULL AND v.id_ventas IS NULL
    UNION ALL
    SELECT 8, CONCAT('presupuesto → clientes huérfanos: ', COUNT(*))              FROM presupuesto p LEFT JOIN clientes c ON c.id_cliente = p.id_cliente WHERE p.id_cliente IS NOT NULL AND c.id_cliente IS NULL
    UNION ALL
    SELECT 9, CONCAT('pago_presupuesto → presupuesto huérfanos: ', COUNT(*))      FROM pago_presupuesto pp LEFT JOIN presupuesto p ON p.id_presupuesto = pp.id_presupuesto WHERE p.id_presupuesto IS NULL
) t
ORDER BY ord;