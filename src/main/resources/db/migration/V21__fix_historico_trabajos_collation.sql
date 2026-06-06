-- =============================================================
-- V21__fix_historico_trabajos_collation.sql
-- =============================================================

ALTER TABLE `historico_trabajos` 
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
    MODIFY COLUMN `material` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL;
