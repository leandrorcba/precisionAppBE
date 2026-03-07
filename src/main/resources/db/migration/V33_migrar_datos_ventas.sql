INSERT INTO venta (
    fecha_hora_venta,
    id_materiales,
    id_superficie,
    cantidad,
    precio_unitario,
    precio_total
)
SELECT
    STR_TO_DATE(
            CONCAT(v.fechaventa,' ',v.horaventa),
            '%d/%m/%Y %H:%i:%s'
    ),
    m.id_materiales,
    s.id_superficie,
    v.cantidad,
    v.preciomaterial,
    v.precioventa
FROM ventas v
         LEFT JOIN materiales m
                   ON m.materiales = v.material
         LEFT JOIN superficies s
                   ON s.valor = v.superficie;