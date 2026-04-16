-- 1. Asegurar integridad en la tabla de precios
ALTER TABLE `precio_materiales`
    ADD CONSTRAINT `fk_precio_mat_materiales`
        FOREIGN KEY (`id_materiales`) REFERENCES `materiales` (`id_materiales`);

-- 2. Evitar que se repitan nombres de materiales (El "remedio" contra los duplicados)
-- Nota: Solo funcionará si ya corriste la unificación y no quedan nombres repetidos.
ALTER TABLE `materiales`
    ADD UNIQUE INDEX `idx_materiales_nombre_unico` (`materiales`);