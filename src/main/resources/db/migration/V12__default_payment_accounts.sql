INSERT INTO `cuenta_bancaria` (`banco`, `alias_cbu`, `cbu`, `numero_cuenta`, `moneda`, `habilitada`)
VALUES ('Cuenta Banco Negocio', 'Negocio.alias', '0070000000000000000001', '12345/1', 'ARS', 1),
       ('Cuenta Banco Bruno', 'Bruno.alias', '0070000000000000000002', '12345/2', 'ARS', 1),
       ('Cuenta Banco Sofi', 'Sofi.alias', '0070000000000000000003', '12345/3', 'ARS', 1),
       ('Cuenta Banco Ro', 'Ro.alias', '0070000000000000000004', '12345/4', 'ARS', 1),
       ('Cuenta Banco Lucho', 'Lucho.alias', '0720000000000000000005', '65432/5', 'ARS', 1);

INSERT INTO `tarjeta` (`nombre`)
VALUES ('Visa Crédito'),
       ('Visa Débito'),
       ('Mastercard Crédito'),
       ('Mastercard Débito');

INSERT INTO `mercado_pago` (`titular`, `disabled`)
VALUES ('Precision MP', 0)
