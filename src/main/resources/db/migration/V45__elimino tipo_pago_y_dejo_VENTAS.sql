INSERT INTO tipo_pago (tipo) VALUES ('VENTAS');


DELETE FROM tipo_pago
WHERE LOWER(tipo) IN ('venta insumos', 'venta materiales');