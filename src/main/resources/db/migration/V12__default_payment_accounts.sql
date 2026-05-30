INSERT INTO `cuenta_bancaria` (`banco`, `alias_cbu`, `cbu`, `numero_cuenta`, `moneda`, `habilitada`) VALUES
('Banco Galicia', 'galicia.alias', '0070000000000000000001', '12345/6', 'ARS', 1),
('Banco Santander', 'santander.alias', '0720000000000000000001', '65432/1', 'ARS', 1);

INSERT INTO `tarjeta` (`nombre`) VALUES
('Visa Crédito'),
('Visa Débito'),
('Mastercard Crédito'),
('Mastercard Débito');

INSERT INTO `mercado_pago` (`titular`, `disabled`) VALUES
('Precision Cut MP', 0),
('Admin Personal MP', 0);
