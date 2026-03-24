ALTER TABLE ventas
    ADD CONSTRAINT fk_ventas_materiales
        FOREIGN KEY (id_materiales)
            REFERENCES materiales(id_materiales);