-- Crear usuario inicial si no existe
INSERT IGNORE INTO users (id_user, username, password, role)
VALUES (1, 'lrebolini', '$2a$10$30t3lty/u/UdcArxsTlsXORQ4rrqtttopDzqVT8VBFtJe4Tu2/qoi', 'SUPER_ADMIN');

CREATE TABLE varios_historial (
                                  id_varios_historial INT NOT NULL AUTO_INCREMENT,
                                  id_varios INT NOT NULL,
                                  precio_minuto DECIMAL(10,2) DEFAULT NULL,
                                  hora_inicio TIME DEFAULT NULL,
                                  hora_cierre TIME DEFAULT NULL,
                                  ajuste DECIMAL(10,2) DEFAULT NULL,
                                  precio_minuto_empresa DECIMAL(10,2) DEFAULT NULL,
                                  descuento_efectivo DECIMAL(10,2) DEFAULT NULL,
                                  hora_inicio_fds TIME DEFAULT NULL,
                                  hora_cierre_fds TIME DEFAULT NULL,
                                  fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  id_user INT DEFAULT NULL,
                                  PRIMARY KEY (id_varios_historial),
                                  KEY idx_varios_historial_varios (id_varios),
                                  KEY idx_varios_historial_fecha (fecha_cambio),
                                  KEY idx_varios_historial_user (id_user),
                                  CONSTRAINT fk_varios_historial_varios
                                      FOREIGN KEY (id_varios) REFERENCES varios(id_varios),
                                  CONSTRAINT fk_varios_historial_user
                                      FOREIGN KEY (id_user) REFERENCES users(id_user)
);

INSERT INTO varios_historial (
    id_varios,
    precio_minuto,
    hora_inicio,
    hora_cierre,
    ajuste,
    precio_minuto_empresa,
    descuento_efectivo,
    hora_inicio_fds,
    hora_cierre_fds,
    id_user
)
SELECT
    id_varios,
    precio_minuto,
    hora_inicio,
    hora_cierre,
    ajuste,
    precio_minuto_empresa,
    descuento_efectivo,
    hora_inicio_fds,
    hora_cierre_fds,
    1 -- id_user
FROM varios
WHERE id_varios = 1;