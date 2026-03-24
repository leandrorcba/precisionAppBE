-- V49__convert_datetimes_to_utc.sql
--
-- Convierte los campos DATETIME de horario Argentina (ART, UTC-3) a UTC.
-- Argentina no aplica horario de verano desde 2008, por lo que el offset
-- es constante: UTC = ART + 3 horas.
--
-- Solo se convierten campos DATETIME que tienen componente horaria real (HH:mm:ss)
-- proveniente de los datos originales. Campos DATE-only y TIME operativos
-- no se modifican (ver MIGRACION.md sección 7.6.2).
--
-- PRE-REQUISITO: Los scripts V3, V4, V8, V27, V36-V37 ya convirtieron los
-- varchars de fecha a tipos nativos DATETIME, almacenando valores en ART.
-- Este script aplica la corrección de zona horaria sobre esos valores.
-- --------------------------------------------------------------------------

-- clientes.fecha_creacion
-- Origen: fechaCreacion varchar(45) con formato 'dd/MM/yyyy HH:mm:ss' (ART)
UPDATE clientes
SET fecha_creacion = DATE_ADD(fecha_creacion, INTERVAL 3 HOUR)
WHERE fecha_creacion IS NOT NULL;

-- presupuesto.fecha_hora_presupuesto
-- Origen: fechaPresupuesto + horaPresupuesto varchar combinados (ART)
UPDATE presupuesto
SET fecha_hora_presupuesto = DATE_ADD(fecha_hora_presupuesto, INTERVAL 3 HOUR)
WHERE fecha_hora_presupuesto IS NOT NULL;

-- presupuesto.fecha_cobrado
-- Origen: fechaCobrado varchar con formato 'dd/MM/yyyy HH:mm:ss' (ART)
UPDATE presupuesto
SET fecha_cobrado = DATE_ADD(fecha_cobrado, INTERVAL 3 HOUR)
WHERE fecha_cobrado IS NOT NULL;

-- presupuesto.fecha_realizado
-- Origen: fechaRealizado varchar con formato 'dd/MM/yyyy HH:mm:ss' (ART)
UPDATE presupuesto
SET fecha_realizado = DATE_ADD(fecha_realizado, INTERVAL 3 HOUR)
WHERE fecha_realizado IS NOT NULL;

-- pago_presupuesto.fecha_hora
-- Origen: copiado desde presupuesto.fecha_cobrado en V8 (misma zona horaria ART)
UPDATE pago_presupuesto
SET fecha_hora = DATE_ADD(fecha_hora, INTERVAL 3 HOUR)
WHERE fecha_hora IS NOT NULL;

-- control_puntos.fecha_canje_puntos
-- Origen: fechaCanjePuntos + horaCanjePuntos varchar combinados (ART)
UPDATE control_puntos
SET fecha_canje_puntos = DATE_ADD(fecha_canje_puntos, INTERVAL 3 HOUR)
WHERE fecha_canje_puntos IS NOT NULL;

-- --------------------------------------------------------------------------
-- CAMPOS NO CONVERTIDOS (documentado):
--
--   ventas.fecha_venta  (DATE)   → sin componente horaria; ver V50 pendiente
--   ventas.hora_venta   (TIME)   → tiempo operativo, no instante absoluto
--   cierre.fecha_cierre (DATETIME) → derivado de DATE original; hora = 00:00:00;
--                                    la fecha de negocio no varía con +3h
--   extracciones.fecha_extraccion (DATETIME) → derivado de DATE original; ídem cierre
--   puntos.fecha_primer_punto (DATE) → fecha pura
--   varios.hora_inicio/cierre (TIME) → horarios operativos del local, son locales por definición
-- --------------------------------------------------------------------------