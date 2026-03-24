ALTER TABLE `precision_schema_v2`.`materiales`
    ADD COLUMN `is_material` TINYINT(1) NULL DEFAULT 0 AFTER `materiales`;


UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '14');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '15');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '16');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '19');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '20');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '21');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '22');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '23');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '24');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '25');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '26');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '27');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '28');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '29');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '31');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '32');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '62');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '63');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '64');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '65');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '66');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '91');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '92');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '93');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '100');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '101');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '117');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '148');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '149');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '150');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '152');
UPDATE `precision_schema_v2`.`materiales` SET `is_material` = '1' WHERE (`idmateriales` = '153');



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

#Creacion de la tabla tipo_cliente
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

