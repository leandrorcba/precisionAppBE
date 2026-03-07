-- Vxxx__core_fk_cierre_user.sql

ALTER TABLE cierre
    ADD INDEX idx_cierre_user (id_user),
  ADD CONSTRAINT fk_cierre_user
    FOREIGN KEY (id_user) REFERENCES users(id_user);

