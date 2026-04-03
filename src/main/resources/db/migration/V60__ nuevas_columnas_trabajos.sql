#Creo una nueva columna mora_1
ALTER TABLE trabajo_presupuestado
    ADD COLUMN unidades INT DEFAULT 0;

ALTER TABLE trabajo_presupuestado
    ADD COLUMN grabado TINYINT DEFAULT 0;

ALTER TABLE trabajo_presupuestado
    ADD COLUMN cortes_especiales TINYINT DEFAULT 0;

ALTER TABLE trabajo_presupuestado
    ADD COLUMN carteles TINYINT DEFAULT 0;

ALTER TABLE trabajo_presupuestado
    ADD COLUMN posicionador DECIMAL(12,2) DEFAULT 0.0;