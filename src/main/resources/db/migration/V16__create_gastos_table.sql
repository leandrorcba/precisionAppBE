CREATE TABLE `gastos` (
  `id_gasto` int NOT NULL AUTO_INCREMENT,
  `monto_gasto` decimal(12,2) NOT NULL,
  `motivo_gasto` varchar(250) NOT NULL,
  `id_usuario` int DEFAULT NULL,
  `responsable_gasto` varchar(100) NOT NULL,
  `fecha_gasto` timestamp NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id_gasto`),
  KEY `fk_gastos_user` (`id_usuario`),
  CONSTRAINT `fk_gastos_user` FOREIGN KEY (`id_usuario`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
