CREATE TABLE IF NOT EXISTS tipo_costo_fijo
(
    id_tipo_costo_fijo INT          NOT NULL AUTO_INCREMENT,
    codigo             VARCHAR(50)  NOT NULL,
    nombre             VARCHAR(100) NOT NULL,
    activo             TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id_tipo_costo_fijo),
    UNIQUE KEY uq_tipo_costo_codigo (codigo)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- 2) Cabecera por mes
CREATE TABLE IF NOT EXISTS costo_fijo
(
    id_costo_fijo INT            NOT NULL AUTO_INCREMENT,
    periodo       DATE           NOT NULL, -- usar siempre YYYY-MM-01
    fecha_cambio  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total         DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_costo_fijo),
    UNIQUE KEY uq_costo_fijo_periodo (periodo)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- 3) Detalle por concepto
CREATE TABLE IF NOT EXISTS costo_fijo_detalle
(
    id_costo_fijo_detalle INT            NOT NULL AUTO_INCREMENT,
    id_costo_fijo         INT            NOT NULL,
    id_tipo_costo_fijo    INT            NOT NULL,
    monto                 DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_costo_fijo_detalle),
    UNIQUE KEY uq_costo_detalle (id_costo_fijo, id_tipo_costo_fijo),
    KEY idx_costo_detalle_costo (id_costo_fijo),
    KEY idx_costo_detalle_tipo (id_tipo_costo_fijo),
    CONSTRAINT fk_costo_detalle_costo
        FOREIGN KEY (id_costo_fijo) REFERENCES costo_fijo (id_costo_fijo),
    CONSTRAINT fk_costo_detalle_tipo
        FOREIGN KEY (id_tipo_costo_fijo) REFERENCES tipo_costo_fijo (id_tipo_costo_fijo)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


INSERT INTO tipo_costo_fijo (codigo, nombre)
VALUES ('ELECTRICIDAD', 'Electricidad'),
       ('AFIP', 'AFIP'),
       ('MUNICIPALIDAD', 'Municipalidad'),
       ('RENTAS', 'Rentas'),
       ('ALQUILER', 'Alquiler'),
       ('EXPENSAS', 'Expensas'),
       ('SUELDO', 'Sueldo'),
       ('AGUA', 'Agua'),
       ('INGRESOS_BRUTOS', 'Ingresos Brutos'),
       ('PLUS_SUELDOS', 'Plus sueldos'),
       ('TELEFONO', 'Teléfono'),
       ('CABLE_INTERNET', 'Cable / Internet'),
       ('EMPLEADO', 'Empleado'),
       ('TARJETA', 'Tarjeta')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

ALTER TABLE `precision_schema`.`costosfijos`
    ADD COLUMN periodo DATE NULL;

-- Convierte "Noviembre-2015" -> 2015-11-01
UPDATE `precision_schema`.`costosfijos`
SET periodo = STR_TO_DATE(
        CONCAT(
                '01-',
                CASE
                    WHEN fecha LIKE 'Enero-%' THEN '01'
                    WHEN fecha LIKE 'Febrero-%' THEN '02'
                    WHEN fecha LIKE 'Marzo-%' THEN '03'
                    WHEN fecha LIKE 'Abril-%' THEN '04'
                    WHEN fecha LIKE 'Mayo-%' THEN '05'
                    WHEN fecha LIKE 'Junio-%' THEN '06'
                    WHEN fecha LIKE 'Julio-%' THEN '07'
                    WHEN fecha LIKE 'Agosto-%' THEN '08'
                    WHEN fecha LIKE 'Septiembre-%' OR fecha LIKE 'Setiembre-%' THEN '09'
                    WHEN fecha LIKE 'Octubre-%' THEN '10'
                    WHEN fecha LIKE 'Noviembre-%' THEN '11'
                    WHEN fecha LIKE 'Diciembre-%' THEN '12'
                    END,
                '-',
                SUBSTRING_INDEX(fecha, '-', -1)
        ),
        '%d-%m-%Y'
              )
WHERE periodo IS NULL;

INSERT INTO costo_fijo (periodo, fecha_cambio, total)
SELECT c.periodo,
       COALESCE(c.fechaCambio, NOW()),
       COALESCE(c.total, 0.00)
FROM `precision_schema`.`costosfijos` c
WHERE c.periodo IS NOT NULL
ON DUPLICATE KEY UPDATE fecha_cambio = VALUES(fecha_cambio),
                        total        = VALUES(total);

INSERT INTO costo_fijo_detalle (id_costo_fijo, id_tipo_costo_fijo, monto)
SELECT cf.id_costo_fijo, t.id_tipo_costo_fijo, x.monto
FROM (SELECT periodo, 'ELECTRICIDAD' codigo, COALESCE(electricidad, 0) monto
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'AFIP', COALESCE(afip, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'MUNICIPALIDAD', COALESCE(municipalidad, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'RENTAS', COALESCE(rentas, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'ALQUILER', COALESCE(alquiler, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'EXPENSAS', COALESCE(expensas, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'SUELDO', COALESCE(sueldo, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'AGUA', COALESCE(agua, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'INGRESOS_BRUTOS', COALESCE(ingresosbrutos, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'PLUS_SUELDOS', COALESCE(plussueldos, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'TELEFONO', COALESCE(telefono, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'CABLE_INTERNET', COALESCE(cable_internet, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'EMPLEADO', COALESCE(empleado, 0)
      FROM `precision_schema`.`costosfijos`
      UNION ALL
      SELECT periodo, 'TARJETA', COALESCE(tarjeta, 0)
      FROM `precision_schema`.`costosfijos`) x
         JOIN costo_fijo cf ON cf.periodo = x.periodo
         JOIN tipo_costo_fijo t ON t.codigo = x.codigo
ON DUPLICATE KEY UPDATE monto = VALUES(monto);
