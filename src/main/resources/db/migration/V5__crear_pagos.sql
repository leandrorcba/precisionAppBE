CREATE TABLE medio_pago (
                            id_medio_pago INT AUTO_INCREMENT PRIMARY KEY,
                            tipo VARCHAR(20) NOT NULL UNIQUE,     -- EFECTIVO, DEBITO, CREDITO, TRANSFERENCIA
                            descripcion VARCHAR(50) NOT NULL
);

INSERT INTO medio_pago (tipo, descripcion) VALUES
                                               ('EFECTIVO','Efectivo'),
                                               ('DEBITO','Tarjeta de débito'),
                                               ('CREDITO','Tarjeta de crédito'),
                                               ('TRANSFERENCIA','Transferencia bancaria'),
                                               ('MERCADO PAGO', 'Mercado Pago'),
                                               ('OTRO', 'Otro'),
                                               ('PREVIO_MIGRACION', 'Previo Migracion');

-- 2) Catálogo de tarjetas
CREATE TABLE tarjeta (
                         id_tarjeta INT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(50) NOT NULL UNIQUE      -- VISA, MASTERCARD, AMEX, CABAL, etc.
);

CREATE TABLE tipo_pago (
                           id_tipo_pago INT AUTO_INCREMENT PRIMARY KEY,
                           tipo VARCHAR(20) NOT NULL UNIQUE     -- SENIA, PRESUPUESTO, ETC
);

INSERT INTO tipo_pago (tipo) VALUES
                                 ('SENIA'),
                                 ('PRESUPUESTO'),
                                 ('VENTA INSUMOS'),
                                 ('VENTA MATERIALES');

-- 3) Cuentas bancarias
CREATE TABLE cuenta_bancaria (
                                 id_cuenta_bancaria INT AUTO_INCREMENT PRIMARY KEY,
                                 banco VARCHAR(100) NOT NULL,
                                 alias_cbu VARCHAR(30) UNIQUE,
                                 cbu CHAR(22) UNIQUE,
                                 numero_cuenta VARCHAR(34) UNIQUE,
                                 moneda CHAR(3) NOT NULL DEFAULT 'ARS',
                                 habilitada BOOLEAN NOT NULL DEFAULT TRUE
);


CREATE TABLE pago_presupuesto (
                                  id_pago_presupuesto INT NOT NULL AUTO_INCREMENT,
                                  id_presupuesto INT NOT NULL,

    -- solo SENIA o PRESUPUESTO (lo controlás con FK + trigger o enum)
                                  id_tipo_pago INT NOT NULL,         -- apunta a tipo_pago
                                  id_medio_pago INT NOT NULL,        -- apunta a medio_pago

                                  monto DECIMAL(10,2) NOT NULL,
                                  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  id_tarjeta INT DEFAULT NULL,       -- si aplica
                                  cuotas TINYINT UNSIGNED DEFAULT NULL,
                                  id_cuenta_bancaria INT DEFAULT NULL,
                                  autorizacion VARCHAR(45) DEFAULT NULL,
                                  notas VARCHAR(255) DEFAULT NULL,

                                  PRIMARY KEY (id_pago_presupuesto),

                                  KEY idx_pp_presupuesto (id_presupuesto),
                                  KEY idx_pp_tipo (id_tipo_pago),
                                  KEY idx_pp_fecha (fecha_hora),

                                  CONSTRAINT fk_pp_presupuesto
                                      FOREIGN KEY (id_presupuesto) REFERENCES presupuesto(id_presupuesto),

                                  CONSTRAINT fk_pp_tipo_pago
                                      FOREIGN KEY (id_tipo_pago) REFERENCES tipo_pago(id_tipo_pago),

                                  CONSTRAINT fk_pp_medio_pago
                                      FOREIGN KEY (id_medio_pago) REFERENCES medio_pago(id_medio_pago),

                                  CONSTRAINT fk_pp_tarjeta
                                      FOREIGN KEY (id_tarjeta) REFERENCES tarjeta(id_tarjeta),

                                  CONSTRAINT fk_pp_cuenta
                                      FOREIGN KEY (id_cuenta_bancaria) REFERENCES cuenta_bancaria(id_cuenta_bancaria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE pago_venta (
                            id_pago_venta INT NOT NULL AUTO_INCREMENT,
                            id_venta INT NOT NULL,
                            id_tipo_pago INT NOT NULL,       -- VENTA INSUMOS / VENTA MATERIALES
                            id_medio_pago INT NOT NULL,
                            monto DECIMAL(10,2) NOT NULL,
                            fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            id_tarjeta INT DEFAULT NULL,
                            cuotas TINYINT UNSIGNED DEFAULT NULL,
                            id_cuenta_bancaria INT DEFAULT NULL,
                            autorizacion VARCHAR(45) DEFAULT NULL,
                            notas VARCHAR(255) DEFAULT NULL,

                            PRIMARY KEY (id_pago_venta),

                            KEY idx_pv_venta (id_venta),
                            KEY idx_pv_tipo (id_tipo_pago),
                            KEY idx_pv_fecha (fecha_hora),

    -- FK a tu tabla de ventas (cuando la tengas):
    -- CONSTRAINT fk_pv_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_ventas),

                            CONSTRAINT fk_pv_tipo_pago FOREIGN KEY (id_tipo_pago) REFERENCES tipo_pago(id_tipo_pago),
                            CONSTRAINT fk_pv_medio_pago FOREIGN KEY (id_medio_pago) REFERENCES medio_pago(id_medio_pago),
                            CONSTRAINT fk_pv_tarjeta FOREIGN KEY (id_tarjeta) REFERENCES tarjeta(id_tarjeta),
                            CONSTRAINT fk_pv_cuenta FOREIGN KEY (id_cuenta_bancaria) REFERENCES cuenta_bancaria(id_cuenta_bancaria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



