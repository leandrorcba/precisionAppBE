DROP TABLE `precision_schema_v2`.`usuarios`;

CREATE TABLE precision_schema_v2.users (
                                           id_user INT AUTO_INCREMENT PRIMARY KEY,
                                           username VARCHAR(50) NOT NULL UNIQUE,
                                           password VARCHAR(255) NOT NULL,
                                           role VARCHAR(20) NOT NULL
);