-- Agregar la columna para el valor del descuento por punto acumulado
ALTER TABLE `precision_schema_v2`.`varios`
    ADD COLUMN `descuento_por_punto` DECIMAL(10, 2) NULL AFTER `descuento_efectivo`;