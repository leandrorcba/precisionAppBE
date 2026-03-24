-- V20260321_01__ventas_add_typed_columns.sql

ALTER TABLE ventas
    ADD COLUMN fecha_venta DATE NULL AFTER fechaventa,
    ADD COLUMN hora_venta TIME NULL AFTER horaventa,
    ADD COLUMN precio_material DECIMAL(10,2) NULL AFTER preciomaterial,
    ADD COLUMN precio_venta DECIMAL(10,2) NULL AFTER precioventa;