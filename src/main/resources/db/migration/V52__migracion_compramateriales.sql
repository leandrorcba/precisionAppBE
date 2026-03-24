-- V52__migracion_compramateriales.sql
--
-- Migra la tabla compramateriales que quedó sin modificar en el schema v2:
--   1. Renombra columnas a snake_case
--   2. Convierte montos de DOUBLE a DECIMAL(12,2)
--   3. Elimina mescompra (redundante — derivable de fecha_compra con la regla día ≤ 10)
--   4. Unifica fechaCompra DATE + HoraCompra TIME en fecha_hora_compra DATETIME UTC
--   5. Agrega id_usuario INT NULL (FK a users) para trazabilidad
--   6. Agrega índice en fecha_hora_compra (columna de filtro frecuente en cierres)
--   7. Convierte charset a utf8mb4
-- --------------------------------------------------------------------------

-- Eliminar fila con fecha inválida '0000-00-00' que impide la migración
DELETE FROM compramateriales WHERE idcompramateriales = 0;

ALTER TABLE compramateriales
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- ======================================================
-- 1) Renombrar columnas a snake_case y corregir tipos monetarios
-- ======================================================

ALTER TABLE compramateriales
    CHANGE COLUMN `idcompramateriales` `id_compra_materiales` INT         NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN `material`           `material`             VARCHAR(100) NULL,
    CHANGE COLUMN `tipo`               `tipo`                 VARCHAR(45)  NULL,
    CHANGE COLUMN `montounitario`      `monto_unitario`       DECIMAL(12,2) NULL,
    CHANGE COLUMN `cantidad`           `cantidad`             INT           NULL,
    CHANGE COLUMN `montototal`         `monto_total`          DECIMAL(12,2) NULL,
    CHANGE COLUMN `caja`               `caja`                 VARCHAR(45)  NULL;

-- ======================================================
-- 2) Unificar fechaCompra DATE + HoraCompra TIME → fecha_hora_compra DATETIME UTC
-- ======================================================
-- Los datos originales están en horario de Argentina (ART, UTC-3).
-- HoraCompra puede ser NULL (solo se registraba la fecha en muchos casos).
-- Se aplica la misma conversión UTC que en V49: campo + INTERVAL 3 HOUR.

ALTER TABLE compramateriales
    ADD COLUMN fecha_hora_compra DATETIME NULL
        AFTER monto_total;

-- Combinar fecha + hora y convertir a UTC
UPDATE compramateriales
SET fecha_hora_compra = DATE_ADD(
    CASE
        -- Si tiene hora: combinar fecha y hora
        WHEN `HoraCompra` IS NOT NULL
            THEN TIMESTAMP(`fechaCompra`, `HoraCompra`)
        -- Si solo tiene fecha: usar medianoche (00:00:00)
        ELSE CAST(`fechaCompra` AS DATETIME)
    END,
    INTERVAL 3 HOUR
)
WHERE `fechaCompra` IS NOT NULL;

-- ======================================================
-- 3) Eliminar columnas reemplazadas
-- ======================================================
-- mescompra: redundante con fecha_hora_compra (se recalcula con la regla día ≤ 10)
-- fechaCompra, HoraCompra: absorbidas por fecha_hora_compra

ALTER TABLE compramateriales
    DROP COLUMN `fechaCompra`,
    DROP COLUMN `HoraCompra`,
    DROP COLUMN `mescompra`;

-- ======================================================
-- 4) Agregar id_usuario para trazabilidad
-- ======================================================

ALTER TABLE compramateriales
    ADD COLUMN id_usuario INT NULL AFTER caja;

ALTER TABLE compramateriales
    ADD CONSTRAINT fk_compramateriales_user
        FOREIGN KEY (id_usuario) REFERENCES users(id_user);

-- ======================================================
-- 5) Índice en fecha_hora_compra
-- ======================================================
-- Es la columna de filtro principal: el cierre consulta compras por fecha del día.

CREATE INDEX idx_compramateriales_fecha
    ON compramateriales (fecha_hora_compra);

-- ======================================================
-- Resultado final de la tabla:
--   id_compra_materiales  INT PK AUTO
--   material              VARCHAR(100)
--   tipo                  VARCHAR(45)
--   monto_unitario        DECIMAL(12,2)
--   cantidad              INT
--   monto_total           DECIMAL(12,2)
--   fecha_hora_compra     DATETIME (UTC)
--   caja                  VARCHAR(45)
--   id_usuario            INT NULL → FK users
-- ======================================================