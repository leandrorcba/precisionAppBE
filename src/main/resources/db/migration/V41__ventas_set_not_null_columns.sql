-- V20260321_10__ventas_set_not_null_columns.sql

delete  from ventas where id_materiales is null;

ALTER TABLE ventas
    MODIFY COLUMN id_materiales INT NOT NULL,
    MODIFY COLUMN cantidad INT NOT NULL,
    MODIFY COLUMN precio_venta DECIMAL(10,2) NOT NULL;

