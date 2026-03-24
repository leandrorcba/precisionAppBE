ALTER TABLE pago_venta
ADD CONSTRAINT fk_pv_venta
FOREIGN KEY (id_venta)
REFERENCES ventas(id_ventas);