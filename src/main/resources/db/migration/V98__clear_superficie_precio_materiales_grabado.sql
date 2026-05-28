-- Limpia superficie e id_superficie en precio_materiales para materiales con is_material=0
UPDATE precision_schema_v2.precio_materiales pm
    JOIN precision_schema_v2.materiales m ON m.id_materiales = pm.id_materiales
SET pm.superficie = NULL,
    pm.id_superficie = NULL
WHERE m.is_material = 0;