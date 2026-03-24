-- V20260321_11__ventas_drop_legacy_columns.sql

ALTER TABLE ventas
DROP COLUMN fechaventa,
    DROP COLUMN horaventa,
    DROP COLUMN preciomaterial,
    DROP COLUMN precioventa;