-- 1. Renombrar la tabla al formato xx_xxx
RENAME TABLE `precision_schema_v2`.`cajamensual` TO `precision_schema_v2`.`caja_mensual`;

-- 2. Normalizar columnas existentes al formato xx_xxx
ALTER TABLE `precision_schema_v2`.`caja_mensual`
    CHANGE COLUMN `idcajaMensual` `id_caja_mensual` INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN `destinadoaSueldo` `destinado_a_sueldo` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    CHANGE COLUMN `destinadoaAhorro` `destinado_a_ahorro` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    CHANGE COLUMN `ahorrodefinido` `ahorro_definido` DECIMAL(12, 2) NOT NULL DEFAULT 0.00;

-- 3. Eliminar columnas redundantes o mal formateadas
-- Borramos 'mesCajaMensual' y 'mescerrado' porque ya tenemos 'fecha_referencia' e 'is_cerrado'
ALTER TABLE `precision_schema_v2`.`caja_mensual`
    DROP COLUMN `mesCajaMensual`,
    DROP COLUMN `mescerrado`;

-- 4. Actualizar Charset y Collation
ALTER TABLE `precision_schema_v2`.`caja_mensual`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;