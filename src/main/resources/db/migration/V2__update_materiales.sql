ALTER TABLE materiales
    ADD COLUMN disabled BOOLEAN NOT NULL DEFAULT FALSE;

-- Materiales que estaban antes
INSERT INTO `materiales`
VALUES (11, 'MDF 1mm', 1),
       (12, 'MDF 1,5mm', 1),
       (13, 'MDF 2mm', 1),
       (17, 'MDF 12mm', 1),
       (18, 'Carton Gris 0,5mm', 1),
       (30, 'Alto Impacto 3mm', 1),
       (33, 'Acrilico 4mm', 1),
       (34, 'Acrilico 5mm', 1),
       (35, 'Acrilico 6mm', 1),
       (36, 'Acrilico 7mm', 1),
       (37, 'Acrilico 8mm', 1),
       (38, 'Acrilico 9mm', 1),
       (39, 'Acrilico 10mm', 1),
       (40, 'Acrilico Color 2,4mm', 1),
       (41, 'Acrilico Color 3,2mm', 1),
       (42, 'Polipropileno', 1),
       (45, 'Terciado 3mm', 1),
       (47, 'GomaEva 5mm', 1),
       (49, 'Cuerina1', 1),
       (50, 'Cuerina2', 1),
       (51, 'Cartulina Encapada', 1),
       (52, 'Especificado en Archivo', 1),
       (53, 'Gotita', 1),
       (54, 'Gotita en gel', 1),
       (55, 'Telgopor Alta Densidad', 1),
       (56, 'Cinta Papel Auca 36 mm', 1),
       (57, 'Cinta Papel Stiko 18 mm', 1),
       (58, 'Cinta de Embalar', 1),
       (59, 'Cola Pritt', 1),
       (60, 'Unipox Chico', 1),
       (61, 'Unipox Grande', 1);


-- Materiales que tienen precio pero no material
INSERT INTO `materiales`
VALUES (43, 'Material_id_43', 1),
       (44, 'Material_id_44', 1),
       (46, 'Material_id_46', 1),
       (48, 'Material_id_48', 1),
       (67, 'Material_id_67', 1),
       (68, 'Material_id_68', 1),
       (69, 'Material_id_69', 1),
       (70, 'Material_id_70', 1),
       (71, 'Material_id_71', 1),
       (72, 'Material_id_72', 1),
       (73, 'Material_id_73', 1),
       (74, 'Material_id_74', 1),
       (75, 'Material_id_75', 1),
       (76, 'Material_id_76', 1),
       (77, 'Material_id_77', 1),
       (78, 'Material_id_78', 1),
       (79, 'Material_id_79', 1),
       (80, 'Material_id_80', 1),
       (81, 'Material_id_81', 1),
       (82, 'Material_id_82', 1),
       (83, 'Material_id_83', 1),
       (84, 'Material_id_84', 1),
       (85, 'Material_id_85', 1),
       (86, 'Material_id_86', 1),
       (87, 'Material_id_87', 1),
       (88, 'Material_id_88', 1),
       (89, 'Material_id_89', 1),
       (90, 'Material_id_90', 1),
       (94, 'Material_id_94', 1),
       (95, 'Material_id_95', 1),
       (96, 'Material_id_96', 1),
       (97, 'Material_id_97', 1),
       (98, 'Material_id_98', 1),
       (99, 'Material_id_99', 1),
       (102, 'Material_id_102', 1),
       (103, 'Material_id_103', 1),
       (104, 'Material_id_104', 1),
       (105, 'Material_id_105', 1),
       (106, 'Material_id_106', 1),
       (107, 'Material_id_107', 1),
       (108, 'Material_id_108', 1),
       (109, 'Material_id_109', 1),
       (110, 'Material_id_110', 1),
       (111, 'Material_id_111', 1),
       (112, 'Material_id_112', 1),
       (113, 'Material_id_113', 1),
       (114, 'Material_id_114', 1),
       (115, 'Material_id_115', 1),
       (116, 'Material_id_116', 1),
       (136, 'Material_id_136', 1);


ALTER TABLE `precision_schema_v2`.`materiales`
    ADD COLUMN `is_material` TINYINT(1) NULL DEFAULT 0 AFTER `materiales`;

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '14');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '15');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '16');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '19');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '20');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '21');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '22');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '23');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '24');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '25');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '26');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '27');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '28');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '29');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '31');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '32');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '62');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '63');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '64');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '65');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '66');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '91');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '92');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '93');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '100');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '101');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '117');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '148');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '149');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '150');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '152');

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '137'); -- 137 Carton Marron 1,5

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '138'); -- 138 Carton Marron 1 mm

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '139'); -- 139 ACRILICO NEGRO

UPDATE `precision_schema_v2`.`materiales`
SET `is_material` = '1'
WHERE (`idmateriales` = '153');


-- Crear una tabla temporal para almacenar los idTrabajoPresupuestado relevantes
CREATE TEMPORARY TABLE temp_trabajos_presupuestados AS
SELECT idTrabajoPresupuestado
FROM trabajopresupuestado
WHERE material = '25';

-- Actualizar la tabla original usando la tabla temporal
UPDATE trabajopresupuestado
SET material = '66'
WHERE idTrabajoPresupuestado IN (SELECT idTrabajoPresupuestado
                                 FROM temp_trabajos_presupuestados);

-- Eliminar la tabla temporal después de usarla
DROP TEMPORARY TABLE temp_trabajos_presupuestados;