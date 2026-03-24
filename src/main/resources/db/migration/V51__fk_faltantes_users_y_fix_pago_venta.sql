-- V51__fk_faltantes_users_y_fix_pago_venta.sql
--
-- 1) Agrega FKs a users que fueron omitidas en scripts anteriores:
--      - cierre.id_user    (columna creada en V23 sin FK)
--      - extracciones.id_usuario (columna creada en V12 sin FK)
--
-- 2) Corrige los registros de pago_venta migrados por V46 que usaban
--    IDs hardcodeados (5 y 7) para id_tipo_pago e id_medio_pago.
--    Esos valores pueden no coincidir si los catálogos se insertaron
--    en orden diferente.
-- --------------------------------------------------------------------------

-- ======================================================
-- 1a) FK cierre.id_user → users
-- ======================================================
-- V23 agregó la columna como NOT NULL sin DEFAULT. En tablas con datos existentes
-- MySQL pudo haber usado 0 como valor implícito. Los normalizamos a id_user = 1
-- (usuario administrador inicial) antes de crear el constraint.

UPDATE cierre
SET id_user = 1
WHERE id_user = 0 OR id_user IS NULL;

ALTER TABLE cierre
    ADD CONSTRAINT fk_cierre_user
        FOREIGN KEY (id_user) REFERENCES users(id_user);

-- ======================================================
-- 1b) FK extracciones.id_usuario → users
-- ======================================================
-- La columna es nullable (INT NULL), por lo que no hay que normalizar valores.
-- Las extracciones previas a la migración quedan con id_usuario = NULL.

ALTER TABLE extracciones
    ADD CONSTRAINT fk_extracciones_user
        FOREIGN KEY (id_usuario) REFERENCES users(id_user);

-- ======================================================
-- 2) Corrección de pago_venta migrado por V46
-- ======================================================
-- V46 insertó pago_venta con id_tipo_pago = 5 e id_medio_pago = 7 hardcodeados.
-- El valor correcto se busca por nombre en el catálogo para evitar dependencia
-- del orden de inserción.
--
-- tipo esperado:  'VENTAS'           (creado en V45)
-- medio esperado: 'PREVIO_MIGRACION' (creado en V5)

UPDATE pago_venta pv
    JOIN tipo_pago tp   ON tp.tipo  = 'VENTAS'
    JOIN medio_pago mp  ON mp.tipo  = 'PREVIO_MIGRACION'
SET pv.id_tipo_pago  = tp.id_tipo_pago,
    pv.id_medio_pago = mp.id_medio_pago
WHERE pv.notas = 'Migrado desde ventas.precio_venta'
  AND (pv.id_tipo_pago  <> tp.id_tipo_pago
    OR pv.id_medio_pago <> mp.id_medio_pago);