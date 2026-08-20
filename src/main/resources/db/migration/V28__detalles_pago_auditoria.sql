-- Columnas adicionales para complementar la auditoría de anulación de pagos con el estado del pago original
ALTER TABLE `auditoria_anulacion_pago`
ADD COLUMN `fecha_hora_pago` DATETIME NULL,
ADD COLUMN `tipo_pago` VARCHAR(50) NULL,
ADD COLUMN `medio_pago` VARCHAR(100) NULL;
