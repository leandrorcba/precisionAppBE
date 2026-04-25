ALTER TABLE `precision_schema_v2`.`descuento`
    ADD COLUMN `minutos_por_punto` INT NULL AFTER `monto`,
ADD COLUMN `precio_minuto` DECIMAL(10,2) NULL AFTER `minutos_por_punto`,
ADD COLUMN `minutos_descontados` INT NULL AFTER `precio_minuto`;