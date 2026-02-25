ALTER TABLE preciomateriales
    CHANGE COLUMN idprecioMateriales id_precio_materiales INT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN idMateriales id_materiales INT,
    CHANGE COLUMN precioMaterial precio_material DECIMAL(10,2) NOT NULL,
    ADD COLUMN id_superficie TINYINT UNSIGNED NULL AFTER id_materiales,
    ADD COLUMN unidades TINYINT UNSIGNED NULL AFTER id_materiales;


-- agrego superficie a precios de materiales
UPDATE preciomateriales pm
    JOIN materiales m ON m.id_materiales = pm.id_materiales
    JOIN superficies s ON s.valor = pm.superficie
    SET pm.id_superficie = s.id_superficie
WHERE (pm.id_superficie IS NULL OR pm.id_superficie = 0)
  AND m.is_material = 1 and id_precio_materiales > 0;

ALTER TABLE preciomateriales
    RENAME TO  precio_materiales ;

-- seteo material no catalogado cuando no existe el material
SET @id_no_catalogado := (
  SELECT id_materiales
  FROM materiales
  WHERE materiales = 'NO CATALOGADO'
  ORDER BY id_materiales DESC
  LIMIT 1
);

UPDATE precio_materiales pm
    LEFT JOIN materiales m ON m.id_materiales = pm.id_materiales
    SET pm.id_materiales = @id_no_catalogado
WHERE pm.id_materiales IS NOT NULL
  AND m.id_materiales IS NULL;