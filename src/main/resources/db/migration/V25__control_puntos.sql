START TRANSACTION;

UPDATE controlpuntos cp
    LEFT JOIN presupuesto p
ON p.id_presupuesto = cp.idPresupuesto
    SET cp.idPresupuesto = NULL
WHERE cp.idPresupuesto IS NOT NULL
  AND p.id_presupuesto IS NULL;

-- 1. Renombrar tabla
ALTER TABLE controlpuntos
    RENAME TO control_puntos;

-- 2. Renombrar columnas principales
ALTER TABLE control_puntos
    RENAME COLUMN idControlPuntos TO id_control_puntos,
    RENAME COLUMN idPresupuesto TO id_presupuesto,
    RENAME COLUMN puntosAcumulados TO puntos_acumulados,
    RENAME COLUMN puntosAcumuladosHistoricos TO puntos_acumulados_historicos,
    RENAME COLUMN puntosPorCorteTrabajos TO puntos_por_corte_trabajos,
    RENAME COLUMN puntosTotalesTrabajo TO puntos_totales_trabajo,
    RENAME COLUMN minutosDisponibles TO minutos_disponibles,
    RENAME COLUMN minutosCanjeados TO minutos_canjeados,
    RENAME COLUMN puntosCanjeados TO puntos_canjeados,
    RENAME COLUMN puntosAcumuladosNuevos TO puntos_acumulados_nuevos,
    RENAME COLUMN puntosAcumuladosHistoricosNuevo TO puntos_acumulados_historicos_nuevo,
    RENAME COLUMN fechaCanjePuntos TO fecha_canje_puntos_old,
    RENAME COLUMN horaCanjePuntos TO hora_canje_puntos_old,
    RENAME COLUMN precioMinuti TO precio_minuto;

-- 3. Ajustar tipos
ALTER TABLE control_puntos
    MODIFY COLUMN id_control_puntos INT NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN id_presupuesto INT NULL,
    MODIFY COLUMN puntos_acumulados INT DEFAULT 0,
    MODIFY COLUMN puntos_acumulados_historicos INT DEFAULT 0,
    MODIFY COLUMN puntos_por_corte_trabajos INT DEFAULT 0,
    MODIFY COLUMN puntos_totales_trabajo INT DEFAULT 0,
    MODIFY COLUMN minutos_disponibles INT DEFAULT 0,
    MODIFY COLUMN minutos_canjeados INT DEFAULT 0,
    MODIFY COLUMN puntos_canjeados INT DEFAULT 0,
    MODIFY COLUMN puntos_acumulados_nuevos INT DEFAULT 0,
    MODIFY COLUMN puntos_acumulados_historicos_nuevo INT DEFAULT 0,
    MODIFY COLUMN precio_minuto DECIMAL(10,2) NULL;

-- 4. Crear nueva columna datetime
ALTER TABLE control_puntos
    ADD COLUMN fecha_canje_puntos DATETIME NULL;

-- 5. Intentar migrar fecha + hora a DATETIME
-- Ajustá el formato según cómo estén realmente guardados los datos.

-- Opción B: fecha tipo '31/01/2025' y hora tipo '14:30'
UPDATE control_puntos
SET fecha_canje_puntos = STR_TO_DATE(
        CONCAT(fecha_canje_puntos_old, ' ', hora_canje_puntos_old),
        '%d/%m/%Y %H:%i:%s'
                         )
WHERE fecha_canje_puntos_old IS NOT NULL
  AND hora_canje_puntos_old IS NOT NULL
  AND fecha_canje_puntos IS NULL;

-- 6. Charset moderno
ALTER TABLE control_puntos
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 7. Índice para presupuesto
CREATE INDEX idx_control_puntos_presupuesto
    ON control_puntos (id_presupuesto);

-- 8. Foreign key
ALTER TABLE control_puntos
    ADD CONSTRAINT fk_control_puntos_presupuesto
        FOREIGN KEY (id_presupuesto)
            REFERENCES presupuesto (id_presupuesto);

COMMIT;
