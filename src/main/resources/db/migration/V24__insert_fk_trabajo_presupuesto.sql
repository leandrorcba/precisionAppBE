DELETE tp
FROM trabajo_presupuestado tp
LEFT JOIN presupuesto p ON p.id_presupuesto = tp.id_presupuesto
WHERE p.id_presupuesto IS NULL;

ALTER TABLE trabajo_presupuestado
    ADD CONSTRAINT fk_tp_presupuesto
        FOREIGN KEY (id_presupuesto) REFERENCES presupuesto(id_presupuesto);