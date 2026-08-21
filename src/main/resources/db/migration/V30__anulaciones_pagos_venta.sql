-- Columnas para soporte de anulación y auditoría en pago_venta
ALTER TABLE `pago_venta`
ADD COLUMN `enabled` TINYINT(1) NOT NULL DEFAULT 1,
ADD COLUMN `anulado` TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN `motivo_anulado` VARCHAR(250) NULL,
ADD COLUMN `fecha_anulado` DATETIME NULL,
ADD COLUMN `usuario_creador` VARCHAR(50) NULL,
ADD COLUMN `usuario_anulador` VARCHAR(50) NULL;

-- Asignar usuario creador por defecto a registros existentes
UPDATE `pago_venta` SET `usuario_creador` = 'SYSTEM' WHERE `usuario_creador` IS NULL;

-- Permitir id_presupuesto NULL en auditoria_anulacion_pago para pagos de ventas y agregar id_venta
ALTER TABLE `auditoria_anulacion_pago`
MODIFY COLUMN `id_presupuesto` INT NULL,
ADD COLUMN `id_venta` INT NULL;
