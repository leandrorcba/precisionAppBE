-- =============================================================
-- V2__import_clientes.sql
-- Importa clientes desde precision_schema (schema legado)
-- mora: 'SI'/'NO' -> 1/0
-- tipoCliente: varchar -> FK tipo_cliente
-- fechaCreacion: 'dd/MM/yyyy HH:mm:ss' -> timestamp
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Crea tipos de cliente a partir de los valores distintos del legado
INSERT INTO `tipo_cliente` (`nombre_tipo`)
SELECT DISTINCT `tipoCliente`
FROM `precision_schema`.`clientes`
WHERE `tipoCliente` IS NOT NULL
ORDER BY `tipoCliente`;

-- Importa clientes mapeando todos los campos
INSERT INTO `clientes` (
  `id_cliente`,
  `dni_cliente`,
  `email_cliente`,
  `nombre_cliente`,
  `telefono_cliente`,
  `precio_minuto_empresa`,
  `mora`,
  `id_tipo_cliente`,
  `fecha_creacion`,
  `disabled`
)
SELECT
  c.`idCliente`,
  c.`dniCliente`,
  c.`emailCliente`,
  c.`nombreCliente`,
  c.`telefonoCliente`,
  c.`precioMinutoEmpresa`,
  CASE WHEN c.`mora` = 'SI' THEN 1 ELSE 0 END,
  tc.`id_tipo_cliente`,
  COALESCE(STR_TO_DATE(c.`fechaCreacion`, '%d/%m/%Y %H:%i:%s'), CURRENT_TIMESTAMP),
  0
FROM `precision_schema`.`clientes` c
LEFT JOIN `tipo_cliente` tc ON tc.`nombre_tipo` = c.`tipoCliente`;

SET FOREIGN_KEY_CHECKS = 1;