
-- #2 inserto nueva columna id_maquina
ALTER TABLE `precision_schema_v2`.`events`
    ADD COLUMN id_maquina INT NOT NULL AFTER maquina;

update precision_schema_v2.events set id_maquina=5 where maquina is null;

-- #3 1) Actualizar cuando events.maquina NO es NULL (copiar el ID)
UPDATE events e
    JOIN maquinas m
ON m.id_maquina = CAST(e.maquina AS UNSIGNED)
    SET e.id_maquina = m.id_maquina
WHERE e.maquina IS NOT NULL
  AND e.maquina <> '';

-- #4 Setear “Maquina generica” cuando events.maquina es NULL (o vacío)
UPDATE events e
    JOIN maquinas mg
ON mg.nombre_maquina = 'Maquina generica'
    SET e.id_maquina = mg.id_maquina
WHERE e.maquina IS NULL
   OR e.maquina = '';

ALTER TABLE events
    ADD CONSTRAINT fk_events_maquina
        FOREIGN KEY (id_maquina)
            REFERENCES maquinas(id_maquina);

-- ALTER TABLE events DROP COLUMN maquina;

