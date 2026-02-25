INSERT INTO pago_presupuesto (id_presupuesto, id_tipo_pago, id_medio_pago, monto, fecha_hora, notas)
SELECT
    p.id_presupuesto,
    (SELECT id_tipo_pago FROM tipo_pago WHERE tipo='PRESUPUESTO' LIMIT 1),
  (SELECT id_medio_pago FROM medio_pago WHERE tipo='PREVIO_MIGRACION' LIMIT 1),
  p.precio_cobrado,
  COALESCE(p.fecha_cobrado, p.fecha_hora_presupuesto),
  'Migrado desde presupuesto.precio_cobrado/fecha_cobrado' AS notas
FROM presupuesto p
WHERE p.precio_cobrado IS NOT NULL
  AND p.precio_cobrado <> 0
  AND (p.cobrado = 1 OR p.fecha_cobrado IS NOT NULL);