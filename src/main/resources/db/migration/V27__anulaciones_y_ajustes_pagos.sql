-- Columnas adicionales para control de auditoría y anulación en pago_presupuesto
ALTER TABLE `pago_presupuesto`
ADD COLUMN `anulado` TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN `motivo_anulado` VARCHAR(250) NULL,
ADD COLUMN `fecha_anulado` DATETIME NULL,
ADD COLUMN `usuario_creador` VARCHAR(50) NULL,
ADD COLUMN `usuario_anulador` VARCHAR(50) NULL;

-- Asignar usuario creador por defecto para registros históricos
UPDATE `pago_presupuesto` SET `usuario_creador` = 'SYSTEM' WHERE `usuario_creador` IS NULL;

-- Agregar tipo de pago AJUSTE
INSERT INTO `tipo_pago` (`id_tipo_pago`, `tipo`) VALUES (4, 'AJUSTE');

-- Agregar medio de pago AJUSTE
INSERT INTO `medio_pago` (`tipo`, `descripcion`) VALUES ('AJUSTE', 'Ajuste de Saldo');

-- Tabla de auditoría específica para anulaciones (supervisión de fraude)
CREATE TABLE `auditoria_anulacion_pago` (
    `id_auditoria` INT AUTO_INCREMENT PRIMARY KEY,
    `id_pago` INT NOT NULL,
    `id_presupuesto` INT NOT NULL,
    `monto` DECIMAL(10,2) NOT NULL,
    `cliente_nombre` VARCHAR(150) NULL,
    `usuario_creador` VARCHAR(50) NULL,
    `usuario_anulador` VARCHAR(50) NULL,
    `fecha_hora_anulacion` DATETIME NOT NULL,
    `motivo` VARCHAR(250) NOT NULL
);
