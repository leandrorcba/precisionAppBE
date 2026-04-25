ALTER TABLE `precision_schema_v2`.`varios_historial`
    ADD COLUMN `descuento_por_punto` INT NULL AFTER `descuento_efectivo`,
ADD COLUMN `varios_historialcol` VARCHAR(45) NULL AFTER `id_user`,
CHANGE COLUMN `ajuste` `ajuste` INT NULL DEFAULT NULL ,
CHANGE COLUMN `descuento_efectivo` `descuento_efectivo` INT NULL DEFAULT NULL ;
