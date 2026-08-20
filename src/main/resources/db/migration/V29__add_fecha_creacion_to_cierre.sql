-- Agregar columna fecha_creacion para registrar el momento de apertura de la caja
ALTER TABLE `cierre` ADD COLUMN `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `responsable`;
UPDATE `cierre` SET `fecha_creacion` = `fecha_cierre` WHERE `fecha_cierre` IS NOT NULL;
ALTER TABLE `cierre` MODIFY COLUMN `fecha_cierre` datetime NULL DEFAULT NULL;

