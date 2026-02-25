

-- 1) Catálogo de tipos de descuento
CREATE TABLE IF NOT EXISTS tipo_descuento (
                                              id_tipo_descuento INT AUTO_INCREMENT PRIMARY KEY,
                                              nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB;

-- Semillas (idempotente por UNIQUE en nombre)
INSERT INTO tipo_descuento (nombre) VALUES
                                        ('EFECTIVO'), ('ESTUDIANTE'), ('PREVIO_MIGRACION')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- 2) Tabla descuento (1:1 con presupuesto)
CREATE TABLE IF NOT EXISTS descuento (
                                         id_descuento INT AUTO_INCREMENT PRIMARY KEY,
                                         id_presupuesto INT NOT NULL,
                                         id_tipo_descuento INT NOT NULL,
                                         monto DECIMAL(10,2) NOT NULL,
    CONSTRAINT uq_descuento_presupuesto UNIQUE (id_presupuesto),
    CONSTRAINT fk_descuento_presupuesto
    FOREIGN KEY (id_presupuesto) REFERENCES presupuesto(id_presupuesto),
    CONSTRAINT fk_descuento_tipo
    FOREIGN KEY (id_tipo_descuento) REFERENCES tipo_descuento(id_tipo_descuento)
    ) ENGINE=InnoDB;

-- 3) Backfill: todos los descuentos actuales a PREVIO_MIGRACION
SET @id_prev_mig := (
  SELECT id_tipo_descuento FROM tipo_descuento
  WHERE nombre = 'PREVIO_MIGRACION' LIMIT 1
);

INSERT INTO descuento (id_presupuesto, id_tipo_descuento, monto)
SELECT
    p.id_presupuesto,
    @id_prev_mig,
    p.descuento AS monto
FROM presupuesto p
         LEFT JOIN descuento d ON d.id_presupuesto = p.id_presupuesto
WHERE
    d.id_presupuesto IS NULL
  AND p.descuento IS NOT NULL;

delete from descuento where monto = 0;

-- 4) (Opcional) Si existía columna descuento en presupuesto, eliminarla
--    Usa IF EXISTS para no fallar si no existe (MySQL 8+).
ALTER TABLE presupuesto
DROP COLUMN descuento;


