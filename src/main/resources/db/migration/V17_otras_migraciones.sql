-- DROP TABLE `precision_schema_v2`.`cajaahorro`;

-- DROP TABLE `precision_schema_v2`.`cajamensual`;

-- DROP TABLE `precision_schema_v2`.`cierrecuatrimestre`;

-- DROP TABLE `precision_schema_v2`.`controlpuntos`;

-- DROP TABLE `precision_schema_v2`.`costosvariables`;

-- DROP TABLE `precision_schema_v2`.`departamentos`;

-- DROP TABLE `precision_schema_v2`.`movimientocajaahorro`;

-- DROP TABLE `precision_schema_v2`.`puntos`;


ALTER TABLE cierre CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE materiales CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE ventas CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE events CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE extracciones CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE trabajo_presupuestado CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE varios CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE compramateriales CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE costosfijos CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE montoinicialcaja CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;



DELETE FROM `precision_schema_v2`.`compramateriales` WHERE (`idcompraMateriales` = '0');

ALTER TABLE compramateriales
    MODIFY montounitario DECIMAL(12,2) NULL,
    MODIFY montototal DECIMAL(12,2) NULL;

ALTER TABLE montoinicialcaja
    MODIFY montoInicialCaja DECIMAL(12,2) NULL,
    MODIFY montoAhorroEstipulado DECIMAL(12,2) NULL;


ALTER TABLE pago_presupuesto
    ADD CONSTRAINT fk_pago_tarjeta
        FOREIGN KEY (id_tarjeta) REFERENCES tarjeta(id_tarjeta),
  ADD CONSTRAINT fk_pago_cuenta_bancaria
    FOREIGN KEY (id_cuenta_bancaria) REFERENCES cuenta_bancaria(id_cuenta_bancaria);

ALTER TABLE pago_venta
    ADD CONSTRAINT fk_pago_tarjeta_ve
        FOREIGN KEY (id_tarjeta) REFERENCES tarjeta(id_tarjeta),
  ADD CONSTRAINT fk_pago_cuenta_bancaria_ve
    FOREIGN KEY (id_cuenta_bancaria) REFERENCES cuenta_bancaria(id_cuenta_bancaria);

-- CREATE INDEX idx_pago_origen ON pago(id_origen_pago);
-- CREATE INDEX idx_pago_fecha ON pago(fecha_hora);

ALTER TABLE `precision_schema_v2`.`superficies`
    CHANGE COLUMN `id_superficie` `id_superficie` INT NOT NULL AUTO_INCREMENT ;


INSERT INTO superficies (valor)
SELECT 'ND'
    WHERE NOT EXISTS (
  SELECT 1 FROM superficies WHERE valor = 'ND'
);

UPDATE trabajo_presupuestado tp
    LEFT JOIN superficies s ON s.id_superficie = tp.id_superficie
    SET tp.id_superficie = (
        SELECT s2.id_superficie
        FROM superficies s2
        WHERE s2.valor = 'ND'
        LIMIT 1
        )
WHERE tp.id_superficie IS NULL
   OR s.id_superficie IS NULL;

DELETE tp
FROM trabajo_presupuestado tp
LEFT JOIN presupuesto p ON p.id_presupuesto = tp.id_presupuesto
WHERE p.id_presupuesto IS NULL;

UPDATE trabajo_presupuestado tp
    LEFT JOIN materiales m ON m.id_materiales = tp.id_materiales
    SET tp.id_materiales = (
        SELECT m2.id_materiales
        FROM materiales m2
        WHERE m2.materiales = 'NO CATALOGADO'
        ORDER BY m2.id_materiales DESC
        LIMIT 1
        )
WHERE tp.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL;

ALTER TABLE trabajo_presupuestado
    ADD CONSTRAINT fk_tp_presupuesto
        FOREIGN KEY (id_presupuesto) REFERENCES presupuesto(id_presupuesto);
ALTER TABLE trabajo_presupuestado
  ADD CONSTRAINT fk_tp_materiales
    FOREIGN KEY (id_materiales) REFERENCES materiales(id_materiales);
ALTER TABLE trabajo_presupuestado
  ADD CONSTRAINT fk_tp_superficie
    FOREIGN KEY (id_superficie) REFERENCES superficies(id_superficie);

CREATE INDEX idx_tp_materiales ON trabajo_presupuestado(id_materiales);
CREATE INDEX idx_tp_superficie ON trabajo_presupuestado(id_superficie);

CREATE INDEX idx_venta_detalle_venta ON venta_detalle(id_venta);

ALTER TABLE extracciones
    MODIFY monto_extraccion DECIMAL(12,2) NOT NULL;

ALTER TABLE venta_detalle
    MODIFY cantidad INT NOT NULL,
    MODIFY precio_unitario DECIMAL(10,2) NOT NULL,
    MODIFY precio_material DECIMAL(10,2) NOT NULL,
    MODIFY precio_total DECIMAL(10,2) NOT NULL;

ALTER TABLE precio_materiales
    CONVERT TO CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE costosfijos
    MODIFY electricidad DECIMAL(12,2) NULL,
    MODIFY afip DECIMAL(12,2) NULL,
    MODIFY municipalidad DECIMAL(12,2) NULL,
    MODIFY rentas DECIMAL(12,2) NULL,
    MODIFY alquiler DECIMAL(12,2) NULL,
    MODIFY expensas DECIMAL(12,2) NULL,
    MODIFY sueldo DECIMAL(12,2) NULL,
    MODIFY agua DECIMAL(12,2) NULL,
    MODIFY ingresosbrutos DECIMAL(12,2) NULL,
    MODIFY plussueldos DECIMAL(12,2) NULL,
    MODIFY total DECIMAL(12,2) NULL,
    MODIFY telefono DECIMAL(12,2) NULL,
    MODIFY cable_internet DECIMAL(12,2) NULL,
    MODIFY empleado DECIMAL(12,2) NULL,
    MODIFY tarjeta DECIMAL(12,2) NULL;

/*
ALTER TABLE ventas ADD COLUMN fecha_hora_venta DATETIME NULL;

-- Si 'fechaventa' es tipo 'YYYY-MM-DD' y 'horaventa' tipo 'HH:MM:SS' (ajustar formatos reales)
UPDATE ventas
SET fecha_hora_venta = STR_TO_DATE(CONCAT(fechaventa,' ',horaventa), '%Y-%m-%d %H:%i:%s')
WHERE fecha_hora_venta IS NULL;*/





