UPDATE trabajo_presupuestado tp
    JOIN (
    SELECT
    tp2.id_trabajo_presupuestado,
    MIN(pm.id_superficie) AS id_superficie   -- ok porque HAVING COUNT(*)=1
    FROM trabajo_presupuestado tp2
    JOIN materiales m2
    ON m2.id_materiales = tp2.id_materiales
    AND m2.is_material = 1
    JOIN precio_materiales pm
    ON pm.id_materiales = tp2.id_materiales
    AND pm.precio_material = tp2.precio_material
    WHERE tp2.id_superficie IS NULL
    GROUP BY tp2.id_trabajo_presupuestado
    HAVING COUNT(*) = 1
    ) x ON x.id_trabajo_presupuestado = tp.id_trabajo_presupuestado
    SET tp.id_superficie = x.id_superficie;