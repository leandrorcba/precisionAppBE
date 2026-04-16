CREATE TABLE `precision_schema_v2`.`mercado_pago`
(
    `id_mercado_pago` INT          NOT NULL AUTO_INCREMENT,
    `titular`         VARCHAR(100) NOT NULL,
    `disabled`        TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (`id_mercado_pago`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;