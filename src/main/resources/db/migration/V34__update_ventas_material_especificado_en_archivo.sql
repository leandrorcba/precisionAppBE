-- V__update_ventas_material_especificado_en_archivo.sql

UPDATE ventas
SET material = 'No catalogado'
WHERE material IS NOT NULL
  AND TRIM(LOWER(material)) = 'especificado en archivo';