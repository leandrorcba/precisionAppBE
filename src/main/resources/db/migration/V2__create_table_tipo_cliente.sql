CREATE TABLE usuarios (
                          id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
                          nombre         VARCHAR(100) NOT NULL,         -- nombre y apellido
                          username       VARCHAR(50)  NULL,             -- único si usás login
                          email          VARCHAR(150) NULL,             -- único si lo usás
                          telefono       VARCHAR(30)  NULL,
                          password_hash  VARCHAR(255) NULL,             -- bcrypt/argon2 (opcional)
                          activo         BOOLEAN      NOT NULL DEFAULT TRUE,

                          created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          UNIQUE KEY uq_usuarios_username (username),
                          KEY ix_usuarios_nombre (nombre)
);

#Creacion la tabla tipo_cliente
CREATE TABLE tipo_cliente (
                              id_tipo_cliente INT AUTO_INCREMENT PRIMARY KEY,
                              nombre_tipo VARCHAR(100) NOT NULL
);

#insert los valores para la tabla tipo_cliente
INSERT INTO tipo_cliente (nombre_tipo)
VALUES
('NORMAL'),
('EMPRESA'),
('ESTUDIANTE');

