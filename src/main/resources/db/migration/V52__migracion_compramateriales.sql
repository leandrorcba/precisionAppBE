-- 1. Eliminar datos basura antes de cualquier cambio (usando el nombre original)
DELETE
FROM `precision_schema_v2`.`compramateriales`
WHERE `idcompramateriales` = 0;

-- 2. Renombrar la tabla al estándar xx_xxx
RENAME TABLE `precision_schema_v2`.`compramateriales` TO `precision_schema_v2`.`compra_materiales`;

-- 3. Cambiar charset y normalizar columnas básicas
ALTER TABLE `precision_schema_v2`.`compra_materiales`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE `precision_schema_v2`.`compra_materiales`
    CHANGE COLUMN `idcompramateriales` `id_compra_materiales` INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN `montounitario` `monto_unitario` DECIMAL(12, 2) NULL,
    CHANGE COLUMN `montototal` `monto_total` DECIMAL(12, 2) NULL;
-- Consistencia con otras tablas

-- 4. AGREGAR id_materiales para integridad relacional
ALTER TABLE `precision_schema_v2`.`compra_materiales`
    ADD COLUMN `id_materiales` INT NULL AFTER `id_compra_materiales`;

-- 5. Unificar fecha y hora (Corregido el nombre de tabla a compra_materiales)
ALTER TABLE `precision_schema_v2`.`compra_materiales`
    ADD COLUMN `fecha_hora_compra` DATETIME NULL AFTER `monto_total`;

UPDATE `precision_schema_v2`.`compra_materiales`
SET `fecha_hora_compra` = DATE_ADD(
        CASE
            WHEN `HoraCompra` IS NOT NULL THEN TIMESTAMP(`fechaCompra`, `HoraCompra`)
            ELSE CAST(`fechaCompra` AS DATETIME)
            END,
        INTERVAL 3 HOUR
                          )
WHERE `fechaCompra` IS NOT NULL;

-- 6. Limpieza de columnas y creación de FKs
ALTER TABLE `precision_schema_v2`.`compra_materiales`
    DROP COLUMN `fechaCompra`,
    DROP COLUMN `HoraCompra`,
    DROP COLUMN `mescompra`;

ALTER TABLE compra_materiales
    ADD COLUMN id_user INT NULL AFTER caja;

-- FK de Usuario
ALTER TABLE `precision_schema_v2`.`compra_materiales`
    ADD CONSTRAINT `fk_compra_materiales_user`
        FOREIGN KEY (`id_user`) REFERENCES `precision_schema_v2`.`users` (`id_user`);

-- FK de Material (Integración con el maestro)
-- Nota: Quedará en NULL inicialmente hasta que hagas un proceso de macheo por nombre
ALTER TABLE `precision_schema_v2`.`compra_materiales`
    ADD CONSTRAINT `fk_compra_materiales_maestro`
        FOREIGN KEY (`id_materiales`) REFERENCES `precision_schema_v2`.`materiales` (`id_materiales`);

-- 7. Índice final con nombre normalizado
CREATE INDEX `idx_compra_materiales_fecha`
    ON `precision_schema_v2`.`compra_materiales` (`fecha_hora_compra`);