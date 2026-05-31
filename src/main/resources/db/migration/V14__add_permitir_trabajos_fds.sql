-- =============================================================
-- V14__add_permitir_trabajos_fds.sql
-- Agrega columna para habilitar/deshabilitar la asignación en fin de semana
-- =============================================================

ALTER TABLE `varios`
    ADD COLUMN `permitir_trabajos_fds` TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE `varios_historial`
    ADD COLUMN `permitir_trabajos_fds` TINYINT(1) DEFAULT 0;
