#Crear tabla superficie
CREATE TABLE superficies
(
    id_superficie INT UNSIGNED NOT NULL AUTO_INCREMENT,
    valor         VARCHAR(4)   NOT NULL COMMENT 'Fracción: 1, 3/4, 1/2, 1/4',
    PRIMARY KEY (id_superficie)
);

INSERT INTO superficies (valor)
VALUES ('1'),
       ('3/4'),
       ('1/2'),
       ('1/4');
