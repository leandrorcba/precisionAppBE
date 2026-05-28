-- Limpieza de precios duplicados
-- Borra el registro más viejo (ID menor) cuando coinciden id_materiales, unidades Y id_superficie
DELETE p1 FROM precio_materiales p1
INNER JOIN precio_materiales p2
    ON p1.id_materiales = p2.id_materiales
    AND (p1.unidades = p2.unidades OR (p1.unidades IS NULL AND p2.unidades IS NULL))
    AND (p1.id_superficie = p2.id_superficie OR (p1.id_superficie IS NULL AND p2.id_superficie IS NULL))
WHERE p1.id_precio_materiales < p2.id_precio_materiales;