ALTER TABLE puntos
    MODIFY COLUMN idCliente INT NULL;

UPDATE puntos p
    LEFT JOIN clientes c
ON c.id_cliente = p.idCliente
    SET p.idCliente = NULL
WHERE p.idCliente IS NOT NULL
  AND c.id_cliente IS NULL;

ALTER TABLE puntos
    RENAME COLUMN idPuntos TO id_puntos,
    RENAME COLUMN fechaPrimerPunto TO fecha_primer_punto,
    RENAME COLUMN idCliente TO id_cliente,
    RENAME COLUMN puntosAcumulados TO puntos_acumulados,
    RENAME COLUMN puntosAcumuladosHistorico TO puntos_acumulados_historico;

ALTER TABLE puntos
    MODIFY COLUMN id_cliente INT NULL;

ALTER TABLE puntos
    MODIFY COLUMN fecha_primer_punto DATE NULL;

UPDATE puntos p
    LEFT JOIN clientes c
ON c.id_cliente = p.id_cliente
    SET p.id_cliente = NULL
WHERE p.id_cliente IS NOT NULL
  AND c.id_cliente IS NULL;

CREATE INDEX idx_puntos_id_cliente
    ON puntos (id_cliente);

ALTER TABLE puntos
    ADD CONSTRAINT fk_puntos_cliente
        FOREIGN KEY (id_cliente)
            REFERENCES clientes (id_cliente);

ALTER TABLE puntos
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;