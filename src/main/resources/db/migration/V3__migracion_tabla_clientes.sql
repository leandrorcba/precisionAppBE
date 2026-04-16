CREATE TABLE usuarios
(
    id_usuario    INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL, -- nombre y apellido
    username      VARCHAR(50)  NULL,     -- único si usás login
    email         VARCHAR(150) NULL,     -- único si lo usás
    telefono      VARCHAR(30)  NULL,
    password_hash VARCHAR(255) NULL,     -- bcrypt/argon2 (opcional)
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uq_usuarios_username (username),
    KEY ix_usuarios_nombre (nombre)
);

#Creacion de la tabla tipo_cliente
CREATE TABLE tipo_cliente
(
    id_tipo_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo     VARCHAR(100) NOT NULL
);

#insert los valores para la tabla tipo_cliente
INSERT INTO tipo_cliente (nombre_tipo)
VALUES ('NORMAL'),
       ('EMPRESA'),
       ('ESTUDIANTE');


ALTER TABLE clientes CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE clientes
    CHANGE COLUMN idCliente id_cliente INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN precioMinutoEmpresa precio_minuto_empresa DECIMAL(10,2),
    CHANGE COLUMN dniCliente dni_cliente VARCHAR(50),
    CHANGE COLUMN emailCliente email_cliente VARCHAR(100),
    CHANGE COLUMN nombreCliente nombre_cliente VARCHAR(200),
    CHANGE COLUMN telefonoCliente telefono_cliente VARCHAR(100);

#Creo una nueva columna mora_1
ALTER TABLE clientes
    ADD COLUMN mora_1 BOOLEAN DEFAULT FALSE;

#Convierto SI de la columna mora a true de la columna_mora1 y el resto a false
UPDATE clientes
SET mora_1 = CASE
                 WHEN UPPER(TRIM(mora)) = 'SI' THEN 1
                 ELSE 0
    END where id_cliente >= 1;

#Borro la vieja columna mora
ALTER TABLE clientes
DROP COLUMN mora;

#Creo el campo id_tipo_cliente
ALTER TABLE clientes
    ADD COLUMN id_tipo_cliente INT;

#Dependiendo del valor de tipo_cliente pongo el valir en id_tipo_cliente
UPDATE clientes c
    JOIN tipo_cliente t ON c.tipoCliente = t.nombre_tipo
    SET c.id_tipo_cliente = t.id_tipo_cliente where id_cliente >= 1;

#Borro el campo tipoCliente
ALTER TABLE clientes
DROP COLUMN tipoCliente;

#Migracion fechaCreacion Varchar a fecha_creacion DATETIME
ALTER TABLE clientes
    ADD COLUMN fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP;

UPDATE clientes
SET fecha_creacion = STR_TO_DATE(fechaCreacion, '%d/%m/%Y %H:%i:%s') where id_cliente >= 0;

ALTER TABLE clientes
DROP COLUMN fechaCreacion;

#Cambio el nombre de la columna mora_1 a mora
ALTER TABLE clientes
    CHANGE COLUMN mora_1 mora BOOLEAN;

ALTER TABLE clientes
    ADD CONSTRAINT fk_clientes_tipo_cliente
        FOREIGN KEY (id_tipo_cliente)
            REFERENCES tipo_cliente(id_tipo_cliente);

alter table clientes
    add  INDEX idx_nombre_cliente (nombre_cliente);

alter table clientes
    add  INDEX idx_mora (mora);

alter table clientes
    add    INDEX idx_nombre_mora (nombre_cliente, mora);

ALTER TABLE clientes
    ADD COLUMN disabled BOOLEAN DEFAULT FALSE;

CREATE INDEX idx_clientes_dni    ON clientes (dni_cliente);
CREATE INDEX idx_clientes_tipo   ON clientes (id_tipo_cliente);
CREATE INDEX idx_clientes_fecha  ON clientes (fecha_creacion);
