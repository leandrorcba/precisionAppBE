INSERT INTO pago_venta (
    id_venta,
    id_tipo_pago,
    id_medio_pago,
    monto,
    fecha_hora,
    notas
)
SELECT
    v.id_ventas,
    5, -- reemplazar por el id real
    7, -- reemplazar por el id real
    v.precio_venta,
    COALESCE(TIMESTAMP(v.fecha_venta, v.hora_venta), NOW()),
    'Migrado desde ventas.precio_venta'
FROM ventas v
WHERE v.precio_venta IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM pago_venta pv
    WHERE pv.id_venta = v.id_ventas
);