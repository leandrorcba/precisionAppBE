-- Migra start_date y end_date de la tabla events de horario Argentina (UTC-3) a UTC.
-- Se suman 3 horas a los valores existentes y se convierten las columnas a TIMESTAMP.

SET time_zone = '+00:00';

UPDATE `precision_schema_v2`.`events`
SET
    `start_date` = DATE_ADD(`start_date`, INTERVAL 3 HOUR),
    `end_date`   = DATE_ADD(`end_date`,   INTERVAL 3 HOUR);

ALTER TABLE `precision_schema_v2`.`events`
    MODIFY `start_date` TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP()),
    MODIFY `end_date`   TIMESTAMP NOT NULL DEFAULT (UTC_TIMESTAMP());
