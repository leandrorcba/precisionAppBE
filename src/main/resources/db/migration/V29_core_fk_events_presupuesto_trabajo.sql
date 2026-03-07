-- Vxxx__core_fk_events_presupuesto_trabajo.sql

ALTER TABLE events
    ADD INDEX idx_events_presupuesto (id_presupuesto),
  ADD INDEX idx_events_trabajo (id_trabajo),
  ADD CONSTRAINT fk_events_presupuesto
    FOREIGN KEY (id_presupuesto) REFERENCES presupuesto(id_presupuesto),
  ADD CONSTRAINT fk_events_trabajo
    FOREIGN KEY (id_trabajo) REFERENCES trabajo_presupuestado(id_trabajo_presupuestado);