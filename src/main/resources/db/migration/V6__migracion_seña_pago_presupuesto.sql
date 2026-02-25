INSERT INTO pago_presupuesto (id_presupuesto, id_tipo_pago, id_medio_pago, monto, fecha_hora, notas)
SELECT
    p.id_presupuesto,
    (SELECT id_tipo_pago FROM tipo_pago WHERE tipo='SENIA' LIMIT 1) AS id_tipo_pago,
  (SELECT id_medio_pago FROM medio_pago WHERE tipo='PREVIO_MIGRACION' LIMIT 1) AS id_medio_pago,
  CAST(p.senia AS DECIMAL(10,2)) AS monto,
  COALESCE(
    -- si fechaSenia ya viene como 'YYYY-MM-DD HH:MM:SS'
    STR_TO_DATE(p.fechaSenia, '%Y-%m-%d %H:%i:%s'),
    -- si viene como 'DD/MM/YYYY'
    STR_TO_DATE(p.fechaSenia, '%d/%m/%Y'),
    p.fecha_hora_presupuesto
  ) AS fecha_hora,
  'Migrado desde presupuesto.senia/fechaSenia' AS notas
FROM presupuesto p
WHERE p.senia IS NOT NULL
  AND p.senia <> 0;



ALTER TABLE presupuesto
DROP COLUMN senia,
  DROP COLUMN fechaSenia;