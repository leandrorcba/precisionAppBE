-- V28__integridad_trabajo_presupuestado.sql
-- Integridad para trabajo_presupuestado:
-- - material -> materiales
-- - superficie -> superficies
-- Regla de negocio:
-- - un trabajo_presupuestado puede tener solo un material
-- - un trabajo_presupuestado puede tener solo una superficie

-- ======================================================
-- VALIDACIONES PREVIAS
-- ======================================================

-- Materiales inexistentes
SELECT tp.id_trabajo_presupuestado, tp.id_materiales
FROM trabajo_presupuestado tp
         LEFT JOIN materiales m
                   ON m.id_materiales = tp.id_materiales
WHERE tp.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL;

-- Superficies inexistentes
SELECT tp.id_trabajo_presupuestado, tp.id_superficie
FROM trabajo_presupuestado tp
         LEFT JOIN superficies s
                   ON s.id_superficie = tp.id_superficie
WHERE tp.id_superficie IS NOT NULL
  AND s.id_superficie IS NULL;

-- ======================================================
-- AJUSTE DE TIPO: id_superficie debe coincidir con superficies.id_superficie
-- superficies.id_superficie = INT UNSIGNED
-- ======================================================

SET @col_tp_superficie_unsigned = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'trabajo_presupuestado'
      AND column_name = 'id_superficie'
      AND column_type = 'int unsigned'
);

SET @sql_modify_tp_superficie = IF(
    @col_tp_superficie_unsigned = 0,
    'ALTER TABLE trabajo_presupuestado MODIFY COLUMN id_superficie INT UNSIGNED DEFAULT NULL',
    'SELECT "id_superficie already INT UNSIGNED"'
);

PREPARE stmt FROM @sql_modify_tp_superficie;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ======================================================
-- INDICE idx_tp_material
-- ======================================================

SET @idx_tp_material_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'trabajo_presupuestado'
      AND index_name = 'idx_tp_material'
);

SET @sql_idx_tp_material = IF(
    @idx_tp_material_exists = 0,
    'CREATE INDEX idx_tp_material ON trabajo_presupuestado (id_materiales)',
    'SELECT "idx_tp_material already exists"'
);

PREPARE stmt FROM @sql_idx_tp_material;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ======================================================
-- INDICE idx_tp_superficie
-- ======================================================

SET @idx_tp_superficie_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'trabajo_presupuestado'
      AND index_name = 'idx_tp_superficie'
);

SET @sql_idx_tp_superficie = IF(
    @idx_tp_superficie_exists = 0,
    'CREATE INDEX idx_tp_superficie ON trabajo_presupuestado (id_superficie)',
    'SELECT "idx_tp_superficie already exists"'
);

PREPARE stmt FROM @sql_idx_tp_superficie;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ======================================================
-- FK fk_tp_material
-- ======================================================

SET @fk_tp_material_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE constraint_schema = DATABASE()
      AND table_name = 'trabajo_presupuestado'
      AND constraint_name = 'fk_tp_material'
);

SET @sql_fk_tp_material = IF(
    @fk_tp_material_exists = 0,
    'ALTER TABLE trabajo_presupuestado
        ADD CONSTRAINT fk_tp_material
        FOREIGN KEY (id_materiales)
        REFERENCES materiales (id_materiales)',
    'SELECT "fk_tp_material already exists"'
);

PREPARE stmt FROM @sql_fk_tp_material;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ======================================================
-- FK fk_tp_superficie
-- ======================================================

SET @fk_tp_superficie_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE constraint_schema = DATABASE()
      AND table_name = 'trabajo_presupuestado'
      AND constraint_name = 'fk_tp_superficie'
);

SET @sql_fk_tp_superficie = IF(
    @fk_tp_superficie_exists = 0,
    'ALTER TABLE trabajo_presupuestado
        ADD CONSTRAINT fk_tp_superficie
        FOREIGN KEY (id_superficie)
        REFERENCES superficies (id_superficie)',
    'SELECT "fk_tp_superficie already exists"'
);

PREPARE stmt FROM @sql_fk_tp_superficie;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;