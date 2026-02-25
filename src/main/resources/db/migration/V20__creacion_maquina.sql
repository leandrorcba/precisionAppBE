CREATE TABLE maquinas (
                          id_maquina INT NOT NULL AUTO_INCREMENT,
                          nombre_maquina VARCHAR(100) NOT NULL,
                          habilitada TINYINT(1) NOT NULL DEFAULT 1,
                          fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id_maquina),

    -- Evita duplicar nombres tipo "Maquina 1"
                          UNIQUE KEY uk_maquina_nombre (nombre_maquina),

    -- Para búsquedas rápidas por estado
                          KEY idx_maquina_habilitada (habilitada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into maquinas (nombre_maquina) values ('Maquina 1');
insert into maquinas (nombre_maquina) values ('Maquina 2');
insert into maquinas (nombre_maquina) values ('Maquina 3');
insert into maquinas (nombre_maquina) values ('Maquina 4');
insert into maquinas (nombre_maquina) values ('Maquina generica');

