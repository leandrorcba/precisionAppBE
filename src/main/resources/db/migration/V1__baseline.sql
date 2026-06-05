-- =============================================================
-- V1__baseline.sql
-- Schema completo inicial — PrecisionApp v2
-- Generado desde dump 2026-05-28
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------------------------
-- Tablas sin dependencias
-- -------------------------------------------------------------

CREATE TABLE `users`
(
    `id_user`  int          NOT NULL AUTO_INCREMENT,
    `username` varchar(50)  NOT NULL,
    `password` varchar(255) NOT NULL,
    `role`     varchar(20)  NOT NULL,
    `enabled`  tinyint(1)   NOT NULL DEFAULT '1',
    PRIMARY KEY (`id_user`),
    UNIQUE KEY `username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `tipo_cliente`
(
    `id_tipo_cliente` int          NOT NULL AUTO_INCREMENT,
    `nombre_tipo`     varchar(100) NOT NULL,
    PRIMARY KEY (`id_tipo_cliente`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `tipo_pago`
(
    `id_tipo_pago` int         NOT NULL AUTO_INCREMENT,
    `tipo`         varchar(20) NOT NULL,
    PRIMARY KEY (`id_tipo_pago`),
    UNIQUE KEY `tipo` (`tipo`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `tipo_descuento`
(
    `id_tipo_descuento` int         NOT NULL AUTO_INCREMENT,
    `nombre`            varchar(50) NOT NULL,
    PRIMARY KEY (`id_tipo_descuento`),
    UNIQUE KEY `nombre` (`nombre`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `medio_pago`
(
    `id_medio_pago` int         NOT NULL AUTO_INCREMENT,
    `tipo`          varchar(20) NOT NULL,
    `descripcion`   varchar(50) NOT NULL,
    PRIMARY KEY (`id_medio_pago`),
    UNIQUE KEY `tipo` (`tipo`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `maquinas`
(
    `id_maquina`     int          NOT NULL AUTO_INCREMENT,
    `nombre_maquina` varchar(100) NOT NULL,
    `habilitada`     tinyint(1)   NOT NULL DEFAULT '1',
    `fecha_creacion` timestamp    NOT NULL DEFAULT (utc_timestamp()),
    PRIMARY KEY (`id_maquina`),
    UNIQUE KEY `uk_maquina_nombre` (`nombre_maquina`),
    KEY `idx_maquina_habilitada` (`habilitada`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `materiales`
(
    `id_materiales` int            NOT NULL AUTO_INCREMENT,
    `materiales`    varchar(45)             DEFAULT NULL,
    `is_material`   tinyint(1)              DEFAULT '0',
    `disabled`      tinyint(1)     NOT NULL DEFAULT '0',
    `stock`         decimal(12, 2) NOT NULL DEFAULT '0.00',
    `stock_minimo`  decimal(12, 2) NOT NULL DEFAULT '0.00',
    `is_grabado`    tinyint(1)              DEFAULT '0',
    PRIMARY KEY (`id_materiales`),
    UNIQUE KEY `uk_materiales_nombre` (`materiales`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `superficies`
(
    `id_superficie` int unsigned NOT NULL AUTO_INCREMENT,
    `valor`         varchar(4)   NOT NULL COMMENT 'Fracción: 1, 3/4, 1/2, 1/4',
    PRIMARY KEY (`id_superficie`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `tarjeta`
(
    `id_tarjeta` int         NOT NULL AUTO_INCREMENT,
    `nombre`     varchar(50) NOT NULL,
    PRIMARY KEY (`id_tarjeta`),
    UNIQUE KEY `nombre` (`nombre`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `cuenta_bancaria`
(
    `id_cuenta_bancaria` int          NOT NULL AUTO_INCREMENT,
    `banco`              varchar(100) NOT NULL,
    `alias_cbu`          varchar(30)           DEFAULT NULL,
    `cbu`                char(22)              DEFAULT NULL,
    `numero_cuenta`      varchar(34)           DEFAULT NULL,
    `moneda`             char(3)      NOT NULL DEFAULT 'ARS',
    `habilitada`         tinyint(1)   NOT NULL DEFAULT '1',
    PRIMARY KEY (`id_cuenta_bancaria`),
    UNIQUE KEY `alias_cbu` (`alias_cbu`),
    UNIQUE KEY `cbu` (`cbu`),
    UNIQUE KEY `numero_cuenta` (`numero_cuenta`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `mercado_pago`
(
    `id_mercado_pago` int          NOT NULL AUTO_INCREMENT,
    `titular`         varchar(100) NOT NULL,
    `disabled`        tinyint(1)   NOT NULL DEFAULT '0',
    PRIMARY KEY (`id_mercado_pago`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `varios`
(
    `id_varios`             int NOT NULL AUTO_INCREMENT,
    `precio_minuto`         decimal(10, 2) DEFAULT NULL,
    `hora_inicio`           time           DEFAULT NULL,
    `hora_cierre`           time           DEFAULT NULL,
    `ajuste`                int            DEFAULT NULL,
    `precio_minuto_empresa` decimal(10, 2) DEFAULT NULL,
    `descuento_efectivo`    int            DEFAULT NULL,
    `minutos_por_punto`     int            DEFAULT '5',
    `descuento_por_punto`   int            DEFAULT NULL,
    `hora_inicio_fds`       time           DEFAULT NULL,
    `hora_cierre_fds`       time           DEFAULT NULL,
    PRIMARY KEY (`id_varios`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `caja_ahorro`
(
    `id_caja_ahorro`       int            NOT NULL AUTO_INCREMENT,
    `nombre`               varchar(50)    NOT NULL DEFAULT 'Principal',
    `monto_caja_ahorro`    decimal(12, 2) NOT NULL DEFAULT '0.00',
    `ultima_actualizacion` timestamp      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_caja_ahorro`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `caja_mensual`
(
    `id_caja_mensual`    int            NOT NULL AUTO_INCREMENT,
    `superavit`          double                  DEFAULT NULL,
    `destinado_a_sueldo` decimal(12, 2) NOT NULL DEFAULT '0.00',
    `destinado_a_ahorro` decimal(12, 2) NOT NULL DEFAULT '0.00',
    `ahorro_definido`    decimal(12, 2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY (`id_caja_mensual`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tablas con dependencias de primer nivel
-- -------------------------------------------------------------

CREATE TABLE `clientes`
(
    `id_cliente`            int       NOT NULL AUTO_INCREMENT,
    `dni_cliente`           varchar(50)        DEFAULT NULL,
    `email_cliente`         varchar(100)       DEFAULT NULL,
    `nombre_cliente`        varchar(200)       DEFAULT NULL,
    `telefono_cliente`      varchar(100)       DEFAULT NULL,
    `precio_minuto_empresa` decimal(10, 2)     DEFAULT NULL,
    `mora`                  tinyint(1)         DEFAULT NULL,
    `id_tipo_cliente`       int                DEFAULT NULL,
    `fecha_creacion`        timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `disabled`              tinyint(1)         DEFAULT '0',
    PRIMARY KEY (`id_cliente`),
    KEY `idx_nombre_cliente` (`nombre_cliente`),
    KEY `idx_mora` (`mora`),
    KEY `idx_nombre_mora` (`nombre_cliente`, `mora`),
    KEY `idx_clientes_dni` (`dni_cliente`),
    KEY `idx_clientes_tipo` (`id_tipo_cliente`),
    KEY `idx_clientes_fecha` (`fecha_creacion`),
    CONSTRAINT `fk_clientes_tipo_cliente` FOREIGN KEY (`id_tipo_cliente`) REFERENCES `tipo_cliente` (`id_tipo_cliente`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `varios_historial`
(
    `id_varios_historial`   int       NOT NULL AUTO_INCREMENT,
    `id_varios`             int       NOT NULL,
    `precio_minuto`         decimal(10, 2)     DEFAULT NULL,
    `hora_inicio`           time               DEFAULT NULL,
    `hora_cierre`           time               DEFAULT NULL,
    `ajuste`                int                DEFAULT NULL,
    `precio_minuto_empresa` decimal(10, 2)     DEFAULT NULL,
    `descuento_efectivo`    int                DEFAULT NULL,
    `minutos_por_punto`     int                DEFAULT '5',
    `descuento_por_punto`   int                DEFAULT NULL,
    `hora_inicio_fds`       time               DEFAULT NULL,
    `hora_cierre_fds`       time               DEFAULT NULL,
    `fecha_cambio`          timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `id_user`               int                DEFAULT NULL,
    PRIMARY KEY (`id_varios_historial`),
    KEY `idx_varios_historial_varios` (`id_varios`),
    KEY `idx_varios_historial_fecha` (`fecha_cambio`),
    KEY `idx_varios_historial_user` (`id_user`),
    CONSTRAINT `fk_varios_historial_varios` FOREIGN KEY (`id_varios`) REFERENCES `varios` (`id_varios`),
    CONSTRAINT `fk_varios_historial_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `refresh_tokens`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `token`      varchar(255) NOT NULL,
    `id_user`    int          NOT NULL,
    `expires_at` datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refresh_token` (`token`),
    KEY `fk_refresh_token_user` (`id_user`),
    CONSTRAINT `fk_refresh_token_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `extracciones`
(
    `id_extraccion`          int       NOT NULL AUTO_INCREMENT,
    `id_usuario`             int                DEFAULT NULL,
    `monto_extraccion`       decimal(12, 2)     DEFAULT NULL,
    `motivo_extraccion`      varchar(255)       DEFAULT NULL,
    `responsable_extraccion` varchar(100)       DEFAULT NULL,
    `fecha_extraccion`       timestamp NOT NULL DEFAULT (utc_timestamp()),
    PRIMARY KEY (`id_extraccion`),
    KEY `fk_extracciones_user` (`id_usuario`),
    CONSTRAINT `fk_extracciones_user` FOREIGN KEY (`id_usuario`) REFERENCES `users` (`id_user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `cierre`
(
    `id_cierre`               int            NOT NULL AUTO_INCREMENT,
    `monto_inicial`           decimal(12, 2) NOT NULL DEFAULT '0.00',
    `monto_final`             decimal(12, 2) NOT NULL DEFAULT '0.00',
    `monto_extracciones`      decimal(12, 2) NOT NULL DEFAULT '0.00',
    `monto_presupuestos`      decimal(12, 2) NOT NULL DEFAULT '0.00',
    `descuento_efectivo`      decimal(12, 2)          DEFAULT NULL,
    `arqueo`                  decimal(12, 2) NOT NULL DEFAULT '0.00',
    `diferencia`              decimal(12, 2)          DEFAULT NULL,
    `responsable`             varchar(100)            DEFAULT NULL,
    `fecha_cierre`            timestamp      NOT NULL DEFAULT (utc_timestamp()),
    `senia`                   decimal(12, 2) NOT NULL DEFAULT '0.00',
    `ventas`                  decimal(12, 2) NOT NULL DEFAULT '0.00',
    `monto_compra_materiales` decimal(12, 2) NOT NULL DEFAULT '0.00',
    `mes_cierre`              varchar(45)             DEFAULT NULL,
    `id_user`                 int            NOT NULL,
    PRIMARY KEY (`id_cierre`),
    KEY `fk_cierre_user` (`id_user`),
    CONSTRAINT `fk_cierre_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `precio_materiales`
(
    `id_precio_materiales` int            NOT NULL AUTO_INCREMENT,
    `id_materiales`        int          DEFAULT NULL,
    `unidades`             int unsigned DEFAULT NULL,
    `id_superficie`        int unsigned DEFAULT NULL,
    `superficie`           varchar(45)  DEFAULT NULL,
    `precio_material`      decimal(10, 2) NOT NULL,
    PRIMARY KEY (`id_precio_materiales`),
    KEY `fk_precio_mat_materiales` (`id_materiales`),
    KEY `fk_precio_materiales_superficie` (`id_superficie`),
    CONSTRAINT `fk_precio_mat_materiales` FOREIGN KEY (`id_materiales`) REFERENCES `materiales` (`id_materiales`),
    CONSTRAINT `fk_precio_materiales_superficie` FOREIGN KEY (`id_superficie`) REFERENCES `superficies` (`id_superficie`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `ventas`
(
    `id_ventas`        int            NOT NULL AUTO_INCREMENT,
    `fecha_hora_venta` datetime       DEFAULT NULL,
    `material`         varchar(45)    DEFAULT NULL,
    `id_materiales`    int            NOT NULL,
    `superficie`       varchar(45)    DEFAULT NULL,
    `precio_material`  decimal(10, 2) DEFAULT NULL,
    `cantidad`         int            NOT NULL,
    `precio_venta`     decimal(10, 2) NOT NULL,
    PRIMARY KEY (`id_ventas`),
    KEY `idx_ventas_id_materiales` (`id_materiales`),
    CONSTRAINT `fk_ventas_materiales` FOREIGN KEY (`id_materiales`) REFERENCES `materiales` (`id_materiales`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `compra_materiales`
(
    `id_compra_materiales` int NOT NULL AUTO_INCREMENT,
    `id_materiales`        int            DEFAULT NULL,
    `material`             varchar(45)    DEFAULT NULL,
    `tipo`                 varchar(45)    DEFAULT NULL,
    `monto_unitario`       decimal(12, 2) DEFAULT NULL,
    `cantidad`             int            DEFAULT NULL,
    `monto_total`          decimal(12, 2) DEFAULT NULL,
    `fecha_hora_compra`    datetime       DEFAULT NULL,
    `caja`                 varchar(45)    DEFAULT NULL,
    `id_user`              int            DEFAULT NULL,
    PRIMARY KEY (`id_compra_materiales`),
    KEY `fk_compra_materiales_user` (`id_user`),
    KEY `fk_compra_materiales_maestro` (`id_materiales`),
    KEY `idx_compra_materiales_fecha` (`fecha_hora_compra`),
    CONSTRAINT `fk_compra_materiales_maestro` FOREIGN KEY (`id_materiales`) REFERENCES `materiales` (`id_materiales`),
    CONSTRAINT `fk_compra_materiales_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tablas con dependencias de segundo nivel
-- -------------------------------------------------------------

CREATE TABLE `presupuesto`
(
    `id_presupuesto`         int            NOT NULL AUTO_INCREMENT,
    `fecha_hora_presupuesto` datetime       NOT NULL,
    `id_cliente`             int            NOT NULL,
    `precio_cobrado`         decimal(10, 2) NOT NULL,
    `precio_sin_descuento`   decimal(10, 2) NOT NULL,
    `precio_minuto`          decimal(10, 2) DEFAULT NULL,
    `aprobado`               tinyint        NOT NULL,
    `realizado`              tinyint        NOT NULL,
    `cobrado`                tinyint        NOT NULL,
    `entregado`              tinyint        NOT NULL,
    `puntos_canjeados`       int            NOT NULL,
    `fecha_cobrado`          datetime       DEFAULT NULL,
    `fecha_realizado`        datetime       DEFAULT NULL,
    PRIMARY KEY (`id_presupuesto`),
    KEY `idx_presupuesto_id_cliente` (`id_cliente`),
    KEY `idx_presupuesto_fecha` (`fecha_hora_presupuesto`),
    CONSTRAINT `fk_presupuesto_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `puntos`
(
    `id_puntos`                   int NOT NULL AUTO_INCREMENT,
    `fecha_primer_punto`          date DEFAULT NULL,
    `id_cliente`                  int  DEFAULT NULL,
    `puntos_acumulados`           int NOT NULL,
    `puntos_acumulados_historico` int NOT NULL,
    PRIMARY KEY (`id_puntos`),
    KEY `idx_puntos_id_cliente` (`id_cliente`),
    CONSTRAINT `fk_puntos_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `pago_venta`
(
    `id_pago_venta`      int            NOT NULL AUTO_INCREMENT,
    `id_venta`           int            NOT NULL,
    `id_tipo_pago`       int            NOT NULL,
    `id_medio_pago`      int            NOT NULL,
    `monto`              decimal(10, 2) NOT NULL,
    `fecha_hora`         timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `id_tarjeta`         int                     DEFAULT NULL,
    `cuotas`             tinyint unsigned        DEFAULT NULL,
    `id_cuenta_bancaria` int                     DEFAULT NULL,
    `autorizacion`       varchar(45)             DEFAULT NULL,
    `notas`              varchar(255)            DEFAULT NULL,
    PRIMARY KEY (`id_pago_venta`),
    KEY `idx_pv_venta` (`id_venta`),
    KEY `idx_pv_tipo` (`id_tipo_pago`),
    KEY `idx_pv_fecha` (`fecha_hora`),
    KEY `fk_pv_medio_pago` (`id_medio_pago`),
    KEY `fk_pv_tarjeta` (`id_tarjeta`),
    KEY `fk_pv_cuenta` (`id_cuenta_bancaria`),
    CONSTRAINT `fk_pv_venta` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_ventas`),
    CONSTRAINT `fk_pv_tipo_pago` FOREIGN KEY (`id_tipo_pago`) REFERENCES `tipo_pago` (`id_tipo_pago`),
    CONSTRAINT `fk_pv_medio_pago` FOREIGN KEY (`id_medio_pago`) REFERENCES `medio_pago` (`id_medio_pago`),
    CONSTRAINT `fk_pv_tarjeta` FOREIGN KEY (`id_tarjeta`) REFERENCES `tarjeta` (`id_tarjeta`),
    CONSTRAINT `fk_pv_cuenta` FOREIGN KEY (`id_cuenta_bancaria`) REFERENCES `cuenta_bancaria` (`id_cuenta_bancaria`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- Tablas con dependencias de tercer nivel
-- -------------------------------------------------------------

CREATE TABLE `trabajo_presupuestado`
(
    `id_trabajo_presupuestado` int            NOT NULL AUTO_INCREMENT,
    `seleccionado`             tinyint(1)     NOT NULL DEFAULT '0',
    `archivo_cad`              varchar(255)            DEFAULT NULL,
    `archivo_original`         varchar(255)            DEFAULT NULL,
    `id_presupuesto`           int            NOT NULL,
    `notas`                    varchar(255)            DEFAULT NULL,
    `tiempo_de_corte`          int            NOT NULL,
    `id_materiales`            int                     DEFAULT NULL,
    `precio_material`          decimal(10, 2) NOT NULL,
    `precio_trabajo`           decimal(10, 2) NOT NULL,
    `precio_corte`             decimal(10, 2)          DEFAULT '0.00',
    `vinilo`                   decimal(10, 2)          DEFAULT '0.00',
    `extra`                    decimal(10, 2)          DEFAULT '0.00',
    `vectorizado`              decimal(10, 2)          DEFAULT '0.00',
    `minutos_por_punto`        int                     DEFAULT '5',
    `precio_minuto`            decimal(10, 2)          DEFAULT '0.00',
    `descuento`                decimal(10, 2)          DEFAULT NULL,
    `id_superficie`            int unsigned            DEFAULT NULL,
    `id_maquina`               int                     DEFAULT NULL,
    `unidades`                 int                     DEFAULT '0',
    `grabado`                  tinyint                 DEFAULT '0',
    `cortes_especiales`        tinyint                 DEFAULT '0',
    `carteles`                 tinyint                 DEFAULT '0',
    `posicionador`             decimal(12, 2)          DEFAULT '0.00',
    `trae_material`            tinyint                 DEFAULT '0',
    `precio_sin_descuento`     decimal(10, 2)          DEFAULT '0.00',
    `estado`                   varchar(20)             DEFAULT NULL,
    PRIMARY KEY (`id_trabajo_presupuestado`),
    KEY `idx_tp_presupuesto` (`id_presupuesto`),
    KEY `idx_tp_seleccionado` (`seleccionado`),
    KEY `idx_tp_material` (`id_materiales`),
    KEY `idx_tp_superficie` (`id_superficie`),
    KEY `fk_tp_maquina` (`id_maquina`),
    CONSTRAINT `fk_tp_presupuesto` FOREIGN KEY (`id_presupuesto`) REFERENCES `presupuesto` (`id_presupuesto`),
    CONSTRAINT `fk_tp_material` FOREIGN KEY (`id_materiales`) REFERENCES `materiales` (`id_materiales`),
    CONSTRAINT `fk_tp_superficie` FOREIGN KEY (`id_superficie`) REFERENCES `superficies` (`id_superficie`),
    CONSTRAINT `fk_tp_maquina` FOREIGN KEY (`id_maquina`) REFERENCES `maquinas` (`id_maquina`),
    CONSTRAINT `trabajo_presupuestado_chk_1` CHECK (`estado` IN ('PENDIENTE', 'REALIZADO', 'ENTREGADO'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `control_puntos`
(
    `id_control_puntos`                  int NOT NULL AUTO_INCREMENT,
    `id_presupuesto`                     int            DEFAULT NULL,
    `puntos_acumulados`                  int            DEFAULT '0',
    `puntos_acumulados_historicos`       int            DEFAULT '0',
    `puntos_por_corte_trabajos`          int            DEFAULT '0',
    `puntos_totales_trabajo`             int            DEFAULT '0',
    `minutos_disponibles`                int            DEFAULT '0',
    `minutos_canjeados`                  int            DEFAULT '0',
    `puntos_canjeados`                   int            DEFAULT '0',
    `puntos_acumulados_nuevos`           int            DEFAULT '0',
    `puntos_acumulados_historicos_nuevo` int            DEFAULT '0',
    `precio_minuto`                      decimal(10, 2) DEFAULT NULL,
    `fecha_canje_puntos`                 datetime       DEFAULT NULL,
    PRIMARY KEY (`id_control_puntos`),
    KEY `idx_control_puntos_presupuesto` (`id_presupuesto`),
    CONSTRAINT `fk_control_puntos_presupuesto` FOREIGN KEY (`id_presupuesto`) REFERENCES `presupuesto` (`id_presupuesto`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `pago_presupuesto`
(
    `id_pago_presupuesto` int            NOT NULL AUTO_INCREMENT,
    `id_presupuesto`      int            NOT NULL,
    `id_tipo_pago`        int            NOT NULL,
    `id_medio_pago`       int            NOT NULL,
    `monto`               decimal(10, 2) NOT NULL,
    `fecha_hora`          timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `id_tarjeta`          int                     DEFAULT NULL,
    `cuotas`              tinyint unsigned        DEFAULT NULL,
    `id_cuenta_bancaria`  int                     DEFAULT NULL,
    `autorizacion`        varchar(45)             DEFAULT NULL,
    `notas`               varchar(255)            DEFAULT NULL,
    `enabled`             tinyint(1)     NOT NULL DEFAULT '1',
    PRIMARY KEY (`id_pago_presupuesto`),
    KEY `idx_pp_presupuesto` (`id_presupuesto`),
    KEY `idx_pp_tipo` (`id_tipo_pago`),
    KEY `idx_pp_fecha` (`fecha_hora`),
    KEY `fk_pp_medio_pago` (`id_medio_pago`),
    KEY `fk_pp_tarjeta` (`id_tarjeta`),
    KEY `fk_pp_cuenta` (`id_cuenta_bancaria`),
    CONSTRAINT `fk_pp_presupuesto` FOREIGN KEY (`id_presupuesto`) REFERENCES `presupuesto` (`id_presupuesto`),
    CONSTRAINT `fk_pp_tipo_pago` FOREIGN KEY (`id_tipo_pago`) REFERENCES `tipo_pago` (`id_tipo_pago`),
    CONSTRAINT `fk_pp_medio_pago` FOREIGN KEY (`id_medio_pago`) REFERENCES `medio_pago` (`id_medio_pago`),
    CONSTRAINT `fk_pp_tarjeta` FOREIGN KEY (`id_tarjeta`) REFERENCES `tarjeta` (`id_tarjeta`),
    CONSTRAINT `fk_pp_cuenta` FOREIGN KEY (`id_cuenta_bancaria`) REFERENCES `cuenta_bancaria` (`id_cuenta_bancaria`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- Tablas con dependencias de cuarto nivel
-- -------------------------------------------------------------

CREATE TABLE `descuento`
(
    `id_descuento`             int            NOT NULL AUTO_INCREMENT,
    `id_presupuesto`           int            NOT NULL,
    `id_trabajo_presupuestado` int            DEFAULT NULL,
    `id_tipo_descuento`        int            NOT NULL,
    `monto`                    decimal(10, 2) NOT NULL,
    `minutos_por_punto`        int            DEFAULT NULL,
    `precio_minuto`            decimal(10, 2) DEFAULT NULL,
    `minutos_descontados`      int            DEFAULT NULL,
    PRIMARY KEY (`id_descuento`),
    UNIQUE KEY `uq_descuento_item` (`id_presupuesto`, `id_trabajo_presupuestado`),
    KEY `fk_descuento_tipo` (`id_tipo_descuento`),
    KEY `fk_descuento_trabajo` (`id_trabajo_presupuestado`),
    CONSTRAINT `fk_descuento_presupuesto` FOREIGN KEY (`id_presupuesto`) REFERENCES `presupuesto` (`id_presupuesto`),
    CONSTRAINT `fk_descuento_tipo` FOREIGN KEY (`id_tipo_descuento`) REFERENCES `tipo_descuento` (`id_tipo_descuento`),
    CONSTRAINT `fk_descuento_trabajo` FOREIGN KEY (`id_trabajo_presupuestado`) REFERENCES `trabajo_presupuestado` (`id_trabajo_presupuestado`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `events`
(
    `event_id`       int          NOT NULL AUTO_INCREMENT,
    `event_name`     varchar(127) NOT NULL,
    `start_date`     timestamp    NOT NULL DEFAULT (utc_timestamp()),
    `end_date`       timestamp    NOT NULL DEFAULT (utc_timestamp()),
    `details`        varchar(255)          DEFAULT NULL,
    `duracion`       int                   DEFAULT NULL,
    `id_presupuesto` int                   DEFAULT NULL,
    `id_trabajo`     int                   DEFAULT NULL,
    `status`         varchar(255)          DEFAULT NULL,
    `maquina`        varchar(4)            DEFAULT NULL,
    `id_maquina`     int          NOT NULL,
    `notas`          varchar(255)          DEFAULT NULL,
    PRIMARY KEY (`event_id`),
    KEY `fk_events_maquina` (`id_maquina`),
    KEY `fk_events_presupuesto` (`id_presupuesto`),
    KEY `fk_events_trabajo` (`id_trabajo`),
    CONSTRAINT `fk_events_maquina` FOREIGN KEY (`id_maquina`) REFERENCES `maquinas` (`id_maquina`),
    CONSTRAINT `fk_events_presupuesto` FOREIGN KEY (`id_presupuesto`) REFERENCES `presupuesto` (`id_presupuesto`),
    CONSTRAINT `fk_events_trabajo` FOREIGN KEY (`id_trabajo`) REFERENCES `trabajo_presupuestado` (`id_trabajo_presupuestado`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;