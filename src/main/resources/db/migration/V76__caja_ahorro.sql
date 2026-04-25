-- 1. Renombrar la tabla al formato xx_xxx
RENAME TABLE `precision_schema_v2`.`cajaahorro` TO `precision_schema_v2`.`caja_ahorro`;

-- 2. Actualizar las columnas al formato xx_xxx y corregir tipos de datos
ALTER TABLE `precision_schema_v2`.`caja_ahorro`
    CHANGE COLUMN `idcajaahorro` `id_caja_ahorro` INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN `montoCajaAhorro` `monto_caja_ahorro` DECIMAL(12, 2) NOT NULL DEFAULT 0.00;

-- 3. Mejorar el charset a utf8mb4 (estándar moderno)
ALTER TABLE `precision_schema_v2`.`caja_ahorro`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. Agregar columnas de auditoría y utilidad
ALTER TABLE `precision_schema_v2`.`caja_ahorro`
    ADD COLUMN `nombre`               VARCHAR(50) NOT NULL DEFAULT 'Principal' AFTER `id_caja_ahorro`,
    ADD COLUMN `ultima_actualizacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;