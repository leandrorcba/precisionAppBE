-- Limpieza de huérfanos antes de agregar constraints

UPDATE `precision_schema_v2`.`events` e
LEFT JOIN `precision_schema_v2`.`presupuesto` p ON p.id_presupuesto = e.id_presupuesto
SET e.id_presupuesto = NULL
WHERE e.id_presupuesto IS NOT NULL
  AND p.id_presupuesto IS NULL;

UPDATE `precision_schema_v2`.`events` e
LEFT JOIN `precision_schema_v2`.`trabajo_presupuestado` tp ON tp.id_trabajo_presupuestado = e.id_trabajo
SET e.id_trabajo = NULL
WHERE e.id_trabajo IS NOT NULL
  AND tp.id_trabajo_presupuestado IS NULL;

UPDATE `precision_schema_v2`.`precio_materiales` pm
LEFT JOIN `precision_schema_v2`.`superficies` s ON s.id_superficie = pm.id_superficie
SET pm.id_superficie = NULL
WHERE pm.id_superficie IS NOT NULL
  AND s.id_superficie IS NULL;

UPDATE `precision_schema_v2`.`trabajo_presupuestado` tp
LEFT JOIN `precision_schema_v2`.`maquinas` m ON m.id_maquina = tp.id_maquina
SET tp.id_maquina = NULL
WHERE tp.id_maquina IS NOT NULL
  AND m.id_maquina IS NULL;

-- FKs faltantes

ALTER TABLE `precision_schema_v2`.`events`
    ADD CONSTRAINT `fk_events_presupuesto`
        FOREIGN KEY (`id_presupuesto`)
            REFERENCES `precision_schema_v2`.`presupuesto` (`id_presupuesto`);

ALTER TABLE `precision_schema_v2`.`events`
    ADD CONSTRAINT `fk_events_trabajo`
        FOREIGN KEY (`id_trabajo`)
            REFERENCES `precision_schema_v2`.`trabajo_presupuestado` (`id_trabajo_presupuestado`);

ALTER TABLE `precision_schema_v2`.`precio_materiales`
    ADD CONSTRAINT `fk_precio_materiales_superficie`
        FOREIGN KEY (`id_superficie`)
            REFERENCES `precision_schema_v2`.`superficies` (`id_superficie`);

ALTER TABLE `precision_schema_v2`.`trabajo_presupuestado`
    ADD CONSTRAINT `fk_tp_maquina`
        FOREIGN KEY (`id_maquina`)
            REFERENCES `precision_schema_v2`.`maquinas` (`id_maquina`);