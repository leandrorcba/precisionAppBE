-- V58__agregar_stock_materiales.sql
--
-- Agrega control de stock a la tabla materiales:
--   stock         → cantidad actual disponible (DECIMAL para permitir fracciones, ej: 0.5 kg)
--   stock_minimo  → umbral de alerta de reposición
-- --------------------------------------------------------------------------

ALTER TABLE materiales
    ADD COLUMN stock         DECIMAL(12, 2) NOT NULL DEFAULT 0.00
        COMMENT 'Cantidad actual en stock' AFTER disabled,
    ADD COLUMN stock_minimo  DECIMAL(12, 2) NOT NULL DEFAULT 0.00
        COMMENT 'Cantidad mínima antes de alertar reposición' AFTER stock;