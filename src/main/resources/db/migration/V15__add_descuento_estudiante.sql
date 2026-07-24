ALTER TABLE varios ADD COLUMN descuento_estudiante DECIMAL(10,2) DEFAULT 10.00;
ALTER TABLE varios_historial ADD COLUMN descuento_estudiante DECIMAL(10,2) DEFAULT 10.00;
UPDATE varios SET descuento_estudiante = 10.00;
