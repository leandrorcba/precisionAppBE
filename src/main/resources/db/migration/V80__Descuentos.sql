-- 1. Agregamos la nueva columna
ALTER TABLE `precision_schema_v2`.`descuento`
    ADD COLUMN `id_trabajo_presupuestado` INT NULL AFTER `id_presupuesto`;

-- 2. BORRAR la Foreign Key primero (esto libera el índice único)
ALTER TABLE `precision_schema_v2`.`descuento`
DROP FOREIGN KEY `fk_descuento_presupuesto`;

-- 3. Ahora sí podemos borrar el índice único sin protestas
ALTER TABLE `precision_schema_v2`.`descuento`
DROP INDEX `uq_descuento_presupuesto`;

-- 4. RE-CREAMOS la Foreign Key hacia presupuesto
-- MySQL creará automáticamente un índice NO ÚNICO para esta relación
ALTER TABLE `precision_schema_v2`.`descuento`
    ADD CONSTRAINT `fk_descuento_presupuesto`
        FOREIGN KEY (`id_presupuesto`) REFERENCES `precision_schema_v2`.`presupuesto` (`id_presupuesto`);

-- 5. Agregamos la Foreign Key para el trabajo presupuestado (ítem específico)
ALTER TABLE `precision_schema_v2`.`descuento`
    ADD CONSTRAINT `fk_descuento_trabajo`
        FOREIGN KEY (`id_trabajo_presupuestado`)
            REFERENCES `precision_schema_v2`.`trabajo_presupuestado` (`id_trabajo_presupuestado`)
            ON DELETE CASCADE;

-- 6. Creamos el nuevo índice único inteligente:
-- Permite múltiples descuentos por presupuesto, pero solo UNO por cada ítem.
ALTER TABLE `precision_schema_v2`.`descuento`
    ADD UNIQUE INDEX `uq_descuento_item` (`id_presupuesto`, `id_trabajo_presupuestado`);