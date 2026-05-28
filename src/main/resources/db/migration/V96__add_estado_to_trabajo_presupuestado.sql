ALTER TABLE precision_schema_v2.trabajo_presupuestado
    ADD COLUMN estado VARCHAR(20) NULL
        CHECK (estado IN ('PENDIENTE', 'REALIZADO', 'ENTREGADO'));

UPDATE precision_schema_v2.trabajo_presupuestado tp
LEFT JOIN precision_schema_v2.presupuesto p ON p.id_presupuesto = tp.id_presupuesto
SET tp.estado = CASE
    WHEN tp.seleccionado = 0 THEN NULL
    WHEN tp.seleccionado = 1 AND p.realizado = 1 THEN 'REALIZADO'
    WHEN tp.seleccionado = 1 AND p.realizado = 0 THEN 'PENDIENTE'
    ELSE NULL
END;
