-- Migración de fechas de horario Argentina (UTC-3) a UTC.
-- Usa checks condicionales vía information_schema para no fallar
-- si alguna columna ya fue migrada o no existe.

SET time_zone = '+00:00';

-- -----------------------------------------------------------------------
-- maquinas.fecha_creacion
-- -----------------------------------------------------------------------
SET @exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'precision_schema_v2'
      AND TABLE_NAME   = 'maquinas'
      AND COLUMN_NAME  = 'fecha_creacion'
      AND DATA_TYPE    = 'datetime');

SET @sql = IF(@exists > 0,
    'UPDATE precision_schema_v2.maquinas SET fecha_creacion = DATE_ADD(fecha_creacion, INTERVAL 3 HOUR)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(@exists > 0,
    'ALTER TABLE precision_schema_v2.maquinas MODIFY fecha_creacion TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP())',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------------------------
-- trabajo_presupuestado.precio_material_snapshot_at
-- -----------------------------------------------------------------------
SET @exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'precision_schema_v2'
      AND TABLE_NAME   = 'trabajo_presupuestado'
      AND COLUMN_NAME  = 'precio_material_snapshot_at'
      AND DATA_TYPE    = 'datetime');

SET @sql = IF(@exists > 0,
    'UPDATE precision_schema_v2.trabajo_presupuestado SET precio_material_snapshot_at = DATE_ADD(precio_material_snapshot_at, INTERVAL 3 HOUR)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(@exists > 0,
    'ALTER TABLE precision_schema_v2.trabajo_presupuestado MODIFY precio_material_snapshot_at TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP())',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------------------------
-- precio_materiales.updated_at
-- -----------------------------------------------------------------------
SET @exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'precision_schema_v2'
      AND TABLE_NAME   = 'precio_materiales'
      AND COLUMN_NAME  = 'updated_at'
      AND DATA_TYPE    = 'datetime');

SET @sql = IF(@exists > 0,
    'UPDATE precision_schema_v2.precio_materiales SET updated_at = DATE_ADD(updated_at, INTERVAL 3 HOUR)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(@exists > 0,
    'ALTER TABLE precision_schema_v2.precio_materiales MODIFY updated_at TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP()) ON UPDATE (UTC_TIMESTAMP())',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------------------------
-- extracciones.fecha_extraccion
-- -----------------------------------------------------------------------
SET @exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'precision_schema_v2'
      AND TABLE_NAME   = 'extracciones'
      AND COLUMN_NAME  = 'fecha_extraccion'
      AND DATA_TYPE    = 'datetime');

SET @sql = IF(@exists > 0,
    'UPDATE precision_schema_v2.extracciones SET fecha_extraccion = DATE_ADD(fecha_extraccion, INTERVAL 3 HOUR)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(@exists > 0,
    'ALTER TABLE precision_schema_v2.extracciones MODIFY fecha_extraccion TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP())',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------------------------
-- cierre.fecha_cierre
-- -----------------------------------------------------------------------
SET @exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'precision_schema_v2'
      AND TABLE_NAME   = 'cierre'
      AND COLUMN_NAME  = 'fecha_cierre'
      AND DATA_TYPE    = 'datetime');

SET @sql = IF(@exists > 0,
    'UPDATE precision_schema_v2.cierre SET fecha_cierre = DATE_ADD(fecha_cierre, INTERVAL 3 HOUR)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(@exists > 0,
    'ALTER TABLE precision_schema_v2.cierre MODIFY fecha_cierre TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP())',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
