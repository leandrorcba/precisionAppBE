UPDATE `precision_schema_v2`.`materiales`
SET `is_grabado` = '1'
WHERE (`id_materiales` = '121');
UPDATE `precision_schema_v2`.`materiales`
SET `is_grabado` = '1'
WHERE (`id_materiales` = '122');
UPDATE `precision_schema_v2`.`materiales`
SET `is_grabado` = '1'
WHERE (`id_materiales` = '123');

INSERT INTO `precision_schema_v2`.`materiales` (`materiales`, `is_material`, `is_grabado`, `disabled`, `stock`,
                                                `stock_minimo`)
VALUES ('Mate Acero', '0', '1', '0', '0', '0');
INSERT INTO `precision_schema_v2`.`materiales` (`materiales`, `is_material`, `is_grabado`, `disabled`, `stock`,
                                                `stock_minimo`)
VALUES ('Mate Imperial', '0', '1', '0', '0', '0');
INSERT INTO `precision_schema_v2`.`materiales` (`materiales`, `is_material`, `is_grabado`, `disabled`, `stock`,
                                                `stock_minimo`)
VALUES ('Bombillas', '0', '1', '0', '0', '0');
INSERT INTO `precision_schema_v2`.`materiales` (`materiales`, `is_material`, `is_grabado`, `disabled`, `stock`,
                                                `stock_minimo`)
VALUES ('Matera', '0', '1', '0', '0', '0');
INSERT INTO `precision_schema_v2`.`materiales` (`materiales`, `is_material`, `is_grabado`, `disabled`, `stock`,
                                                `stock_minimo`)
VALUES ('Termo', '0', '1', '0', '0', '0');