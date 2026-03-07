ALTER TABLE trabajo_presupuestado
    ADD COLUMN id_precio_materiales INT NULL,
  ADD COLUMN precio_material_snapshot_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN stale_precio TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE trabajo_presupuestado
    ADD INDEX idx_tp_id_materiales (id_materiales),
  ADD INDEX idx_tp_id_precio_materiales (id_precio_materiales);

ALTER TABLE trabajo_presupuestado
    ADD CONSTRAINT fk_tp_materiales
        FOREIGN KEY (id_materiales) REFERENCES materiales(id_materiales);

ALTER TABLE trabajo_presupuestado
    ADD CONSTRAINT fk_tp_precio_materiales
        FOREIGN KEY (id_precio_materiales) REFERENCES precio_materiales(id_precio_materiales);

ALTER TABLE precio_materiales
    ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE precio_materiales
    ADD CONSTRAINT fk_pm_material
        FOREIGN KEY (id_materiales) REFERENCES materiales(id_materiales);

ALTER TABLE precio_materiales
    ADD INDEX idx_pm_lookup (id_materiales, id_superficie, unidades);