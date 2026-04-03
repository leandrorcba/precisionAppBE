/*-- ----actualizo todos los trabajos para que apunten a uno de los carton blanco
update trabajo_presupuestado
set id_presupuesto = 66
where id_materiales = 25;

-- delete
-- from materiales
-- where id_materiales = 25;*/

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
-- 153 ACRILICO 5mm

--insert into ma*/

idMaterila 55 Telgopol Alta Densidad
           62 Polyfan 4cm
           57 Polyfan 3cm
           58 Polyfan 5cm