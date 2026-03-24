#Creo una nueva columna is_grabado
ALTER TABLE materiales
    ADD COLUMN is_grabado BOOLEAN DEFAULT FALSE;