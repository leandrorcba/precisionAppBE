ALTER TABLE `extracciones`
    CHANGE COLUMN `idextraccion`           `id_extraccion`           INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN `montoExtraccion`        `monto_extraccion`        DECIMAL(12,2) NULL,
    CHANGE COLUMN `motivoExtraccion`       `motivo_extraccion`       VARCHAR(255) NULL,
    CHANGE COLUMN `responsableExtraccion`  `responsable_extraccion`  VARCHAR(100) NULL,
    CHANGE COLUMN `fechaExtraccion`        `fecha_extraccion`        VARCHAR(45) NULL,
    CHANGE COLUMN `mesExtraccion`          `mes_extraccion`          VARCHAR(45) NULL;

ALTER TABLE `extracciones`
    ADD COLUMN `id_usuario` INT NULL AFTER `fecha_extraccion`;

ALTER TABLE extracciones
    ADD COLUMN fecha_extraccion_dt DATETIME NULL;

UPDATE extracciones
SET fecha_extraccion_dt = STR_TO_DATE(fecha_extraccion, '%d/%m/%Y');

ALTER TABLE extracciones
DROP COLUMN fecha_extraccion,
  DROP COLUMN mes_extraccion,
  CHANGE COLUMN fecha_extraccion_dt fecha_extraccion DATETIME NOT NULL;

