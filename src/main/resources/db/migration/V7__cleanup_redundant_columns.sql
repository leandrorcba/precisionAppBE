-- =============================================================
-- V7__cleanup_redundant_columns.sql
-- Elimina columnas redundantes con FK equivalente
-- =============================================================

-- precio_materiales: superficie varchar redundante con id_superficie FK
ALTER TABLE `precio_materiales`
    DROP COLUMN `superficie`;

-- ventas: material varchar redundante con id_materiales FK
ALTER TABLE `ventas`
    DROP COLUMN `material`;

-- events: maquina varchar redundante con id_maquina FK
ALTER TABLE `events`
    DROP COLUMN `maquina`;

-- extracciones: responsable_extraccion varchar redundante con id_usuario FK
ALTER TABLE `extracciones`
    DROP COLUMN `responsable_extraccion`;
