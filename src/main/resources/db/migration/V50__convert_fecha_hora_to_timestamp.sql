-- V50__convert_fecha_hora_to_timestamp.sql
--
-- Cambia los campos que representan instantes auto-generados de DATETIME a TIMESTAMP.
-- TIMESTAMP en MySQL siempre almacena UTC internamente y convierte al timezone de
-- sesión en la lectura, por lo que DEFAULT CURRENT_TIMESTAMP funciona correctamente
-- sin depender de la configuración de zona horaria de la sesión o el servidor.
--
-- PRE-REQUISITOS:
--   1. V49 ya debe haber ejecutado: los valores en los campos DATETIME son UTC.
--   2. Este script debe correr con la sesión MySQL en UTC (serverTimezone=UTC en la
--      URL JDBC, o SET time_zone = '+00:00' antes de ejecutar).
--      Si la sesión no es UTC, MySQL va a re-interpretar los valores UTC como ART
--      al momento del ALTER y los desplazará 3 horas incorrectamente.
--
-- POR QUÉ TIMESTAMP Y NO DATETIME:
--   DATETIME almacena el valor literal que recibe. DEFAULT CURRENT_TIMESTAMP evalúa
--   usando el timezone de sesión → si la sesión es ART, guarda ART; si es UTC, guarda UTC.
--   TIMESTAMP siempre convierte session → UTC al escribir y UTC → session al leer.
--   DEFAULT CURRENT_TIMESTAMP en un TIMESTAMP siempre guarda UTC, sin importar la sesión.
-- --------------------------------------------------------------------------

-- Asegurar sesión en UTC antes de los ALTERs
SET time_zone = '+00:00';

-- pago_presupuesto.fecha_hora
-- Representa el instante en que se registró el pago. Es un timestamp absoluto.
ALTER TABLE pago_presupuesto
    MODIFY fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- pago_venta.fecha_hora
-- Idem para pagos de ventas directas.
ALTER TABLE pago_venta
    MODIFY fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- varios_historial.fecha_cambio
-- Registro de auditoría: cuándo se modificó la configuración del sistema.
ALTER TABLE varios_historial
    MODIFY fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- clientes.fecha_creacion
-- Instante de alta del cliente, auto-generado. Tiene DEFAULT CURRENT_TIMESTAMP.
-- Nota: V49 ya convirtió los valores históricos a UTC.
ALTER TABLE clientes
    MODIFY fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- costo_fijo.fecha_cambio
-- Registro de cuándo se actualizó el costo fijo del período.
ALTER TABLE costo_fijo
    MODIFY fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- --------------------------------------------------------------------------
-- CAMPOS QUE NO SE CONVIERTEN A TIMESTAMP:
--
--   cierre.fecha_cierre (DATETIME)
--     → Es una fecha de negocio (día de cierre de caja), no un instante de sistema.
--       Conceptualmente es un DATE; fue extendido a DATETIME solo para compatibilidad
--       con DEFAULT CURRENT_TIMESTAMP. Se mantiene como DATETIME.
--
--   clientes.fecha_creacion (DATETIME)
--   presupuesto.fecha_hora_presupuesto, fecha_cobrado, fecha_realizado (DATETIME)
--   control_puntos.fecha_canje_puntos (DATETIME)
--     → Son instantes históricos enviados explícitamente por Hibernate.
--       Con spring.jpa.properties.hibernate.jdbc.time_zone=UTC configurado,
--       Hibernate siempre envía valores UTC. DATETIME los almacena tal cual.
--       Cambiarlos a TIMESTAMP es opcional pero no imprescindible si la config
--       de aplicación es correcta.
-- --------------------------------------------------------------------------