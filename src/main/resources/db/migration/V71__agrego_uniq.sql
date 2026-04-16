DELETE
FROM precio_materiales
WHERE id_materiales NOT IN (SELECT id_materiales FROM materiales);

 -- 1. Agregamos el índice Único a la columna de materiales
 -- Esto fallará si aún quedan duplicados (pero ya los limpiamos)
 ALTER TABLE `materiales`
 ADD UNIQUE INDEX `uk_materiales_nombre` (`materiales`);

 -- 2. Vinculamos precio_materiales con materiales (Foreign Key)
 ALTER TABLE `precio_materiales`
 ADD CONSTRAINT `fk_precio_material_maestro`
   FOREIGN KEY (`id_materiales`) REFERENCES `materiales` (`id_materiales`)
     ON DELETE CASCADE;


-- 4. Limpieza de columnas obsoletas (Ya migradas a id_materiales)
-- ALTER TABLE `trabajo_presupuestado` DROP COLUMN IF EXISTS `material`;
-- ALTER TABLE `ventas` DROP COLUMN IF EXISTS `material`;