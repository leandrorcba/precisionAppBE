ALTER TABLE `precision_schema_v2`.`varios`
    ADD COLUMN `minutos_por_punto` INT NULL DEFAULT 5 AFTER `descuento_efectivo`,
CHANGE COLUMN `ajuste` `ajuste` INT NULL DEFAULT NULL ,
CHANGE COLUMN `descuento_efectivo` `descuento_efectivo` INT NULL DEFAULT NULL ;
