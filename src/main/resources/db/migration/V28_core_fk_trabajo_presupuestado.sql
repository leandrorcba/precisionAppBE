-- Vxxx__core_fk_trabajo_presupuestado.sql

ALTER TABLE trabajo_presupuestado
    ADD INDEX idx_tp_materiales (id_materiales),
  ADD INDEX idx_tp_superficie (id_superficie);

ALTER TABLE trabajo_presupuestado
    ADD CONSTRAINT fk_tp_materiales
        FOREIGN KEY (id_materiales) REFERENCES materiales(id_materiales),
  ADD CONSTRAINT fk_tp_superficie
    FOREIGN KEY (id_superficie) REFERENCES superficies(id_superficie);