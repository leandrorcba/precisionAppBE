ALTER TABLE `cierre`
    CHANGE COLUMN `idcierre`               `id_cierre`               INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN `montoInicial`           `monto_inicial`           DECIMAL(12,2) NULL,
    CHANGE COLUMN `montoFinal`             `monto_final`             DECIMAL(12,2) NULL,
    CHANGE COLUMN `montoExtracciones`      `monto_extracciones`      DECIMAL(12,2) NULL,
    CHANGE COLUMN `montoPresupuestos`      `monto_presupuestos`      DECIMAL(12,2) NULL,
    CHANGE COLUMN `arqueo`                 `arqueo`                  DECIMAL(12,2) NULL,
    CHANGE COLUMN `diferencia`             `diferencia`              DECIMAL(12,2) NULL,
    CHANGE COLUMN `responsable`            `responsable`             VARCHAR(100) NULL,
    CHANGE COLUMN `fechaCierre`            `fecha_cierre`            DATETIME NULL,
    CHANGE COLUMN `senia`                  `senia`                   DECIMAL(12,2) NULL,
    CHANGE COLUMN `ventas`                 `ventas`                  DECIMAL(12,2) NULL,
    CHANGE COLUMN `montoCompraMateriales`  `monto_compra_materiales` DECIMAL(12,2) NULL,
    CHANGE COLUMN `mesCierre`              `mes_cierre`              VARCHAR(45) NULL;

-- ✅ 2) Normalizar NULLs a 0 para poder poner NOT NULL
UPDATE `cierre`
SET
    monto_inicial            = IFNULL(monto_inicial, 0),
    monto_final              = IFNULL(monto_final, 0),
    monto_extracciones       = IFNULL(monto_extracciones, 0),
    monto_presupuestos       = IFNULL(monto_presupuestos, 0),
    arqueo                   = IFNULL(arqueo, 0),
    senia                    = IFNULL(senia, 0),
    ventas                   = IFNULL(ventas, 0),
    monto_compra_materiales  = IFNULL(monto_compra_materiales, 0);

-- ✅ 3) Defaults y NOT NULL (y default CURRENT_TIMESTAMP para fecha)
ALTER TABLE `cierre`
    MODIFY `fecha_cierre`            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY `monto_inicial`           DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `monto_final`             DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `monto_extracciones`      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `monto_presupuestos`      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `arqueo`                  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `senia`                   DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `ventas`                  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MODIFY `monto_compra_materiales` DECIMAL(12,2) NOT NULL DEFAULT 0.00;

alter table cierre add column descuento_efectivo DECIMAL(12,2) after monto_presupuestos;