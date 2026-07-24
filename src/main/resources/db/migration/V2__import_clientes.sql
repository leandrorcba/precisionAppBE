-- =============================================================
-- V2__import_clientes.sql
-- Importa clientes desde precisionschema (schema legado)
-- mora: 'SI'/'NO' -> 1/0
-- tipoCliente: varchar -> FK tipo_cliente
-- fechaCreacion: 'dd/MM/yyyy HH:mm:ss' -> timestamp
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Calculate fallback date as the first (minimum) date among clients that have a date
SET @default_date = (
    SELECT MIN(
        COALESCE(
            STR_TO_DATE(c2.`fechaCreacion`, '%d/%m/%Y %H:%i:%s'),
            STR_TO_DATE(c2.`fechaCreacion`, '%e/%c/%Y %H:%i:%s'),
            STR_TO_DATE(c2.`fechaCreacion`, '%d/%m/%y %H:%i:%s'),
            STR_TO_DATE(c2.`fechaCreacion`, '%e/%c/%y %H:%i:%s'),
            STR_TO_DATE(c2.`fechaCreacion`, '%Y-%m-%d %H:%i:%s')
        )
    )
    FROM `precisionschema`.`clientes` c2
    WHERE c2.`fechaCreacion` IS NOT NULL AND c2.`fechaCreacion` <> ''
);

-- If still null (no clients have dates), fallback to current timestamp
SET @default_date = COALESCE(@default_date, CURRENT_TIMESTAMP);

-- Crea tipos de cliente a partir de los valores distintos del legado
INSERT INTO `tipo_cliente` (`nombre_tipo`)
SELECT DISTINCT `tipoCliente`
FROM `precisionschema`.`clientes`
WHERE `tipoCliente` IS NOT NULL
ORDER BY `tipoCliente`;

-- Importa clientes mapeando todos los campos
INSERT INTO `clientes` (`id_cliente`,
                        `dni_cliente`,
                        `email_cliente`,
                        `nombre_cliente`,
                        `telefono_cliente`,
                        `precio_minuto_empresa`,
                        `mora`,
                        `id_tipo_cliente`,
                        `fecha_creacion`,
                        `disabled`)
SELECT c.`idCliente`,
       c.`dniCliente`,
       c.`emailCliente`,
       c.`nombreCliente`,
       c.`telefonoCliente`,
       c.`precioMinutoEmpresa`,
       CASE WHEN c.`mora` = 'SI' THEN 1 ELSE 0 END,
       tc.`id_tipo_cliente`,
       COALESCE(
           STR_TO_DATE(c.`fechaCreacion`, '%d/%m/%Y %H:%i:%s'),
           STR_TO_DATE(c.`fechaCreacion`, '%e/%c/%Y %H:%i:%s'),
           STR_TO_DATE(c.`fechaCreacion`, '%d/%m/%y %H:%i:%s'),
           STR_TO_DATE(c.`fechaCreacion`, '%e/%c/%y %H:%i:%s'),
           STR_TO_DATE(c.`fechaCreacion`, '%Y-%m-%d %H:%i:%s'),
           c.`fechaCreacion`,
           @default_date
       ),
       0
FROM `precisionschema`.`clientes` c
         LEFT JOIN `tipo_cliente` tc ON tc.`nombre_tipo` = c.`tipoCliente`;

SET FOREIGN_KEY_CHECKS = 1;