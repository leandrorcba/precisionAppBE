-- V20260321_08__ventas_add_indexes.sql

ALTER TABLE ventas
    ADD INDEX idx_ventas_id_materiales (id_materiales),
    ADD INDEX idx_ventas_fecha_venta (fecha_venta),
    ADD INDEX idx_ventas_fecha_venta_id_materiales (fecha_venta, id_materiales);