CREATE TABLE venta (
                       id_venta INT NOT NULL AUTO_INCREMENT,
                       fecha_hora_venta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       id_materiales INT NOT NULL,
                       id_superficie INT UNSIGNED NOT NULL,
                       cantidad INT NOT NULL,
                       precio_unitario DECIMAL(10,2) NOT NULL,
                       precio_total DECIMAL(10,2) NOT NULL,
                       notas VARCHAR(255) DEFAULT NULL,

                       PRIMARY KEY (id_venta),

                       KEY idx_venta_fecha (fecha_hora_venta),
                       KEY idx_venta_material (id_materiales),
                       KEY idx_venta_superficie (id_superficie),

                       CONSTRAINT fk_venta_material
                           FOREIGN KEY (id_materiales)
                               REFERENCES materiales(id_materiales),

                       CONSTRAINT fk_venta_superficie
                           FOREIGN KEY (id_superficie)
                               REFERENCES superficies(id_superficie)
);