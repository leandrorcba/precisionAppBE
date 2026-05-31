-- =============================================================
-- V13__add_directorio_raiz_carpetas.sql
-- Agrega columna para definir el directorio raíz de carpetas de clientes
-- =============================================================

ALTER TABLE `varios`
    ADD COLUMN `directorio_raiz_carpetas` VARCHAR(255) DEFAULT NULL;
