-- =============================================================
-- V20__import_historico_metricas.sql
-- =============================================================

CREATE TABLE `historico_trabajos` (
    `id_historico_trabajo` INT AUTO_INCREMENT PRIMARY KEY,
    `fecha` DATETIME NOT NULL,
    `id_maquina` INT DEFAULT NULL,
    `tiempo_de_corte` INT NOT NULL,
    `precio_corte` DECIMAL(15, 2) DEFAULT '0.00',
    `material` VARCHAR(255) DEFAULT NULL,
    `precio_material` DECIMAL(15, 2) DEFAULT '0.00',
    `vectorizado` DECIMAL(15, 2) DEFAULT '0.00',
    `extra` DECIMAL(15, 2) DEFAULT '0.00',
    `vinilo` DECIMAL(15, 2) DEFAULT '0.00',
    INDEX `idx_hist_fecha` (`fecha`),
    INDEX `idx_hist_maquina` (`id_maquina`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Mapear y migrar trabajos desde el esquema legado
INSERT INTO `historico_trabajos` (
    `fecha`,
    `id_maquina`,
    `tiempo_de_corte`,
    `precio_corte`,
    `material`,
    `precio_material`,
    `vectorizado`,
    `extra`,
    `vinilo`
)
SELECT
    COALESCE(
        STR_TO_DATE(CONCAT(lp.fechaPresupuesto, ' ', lp.horaPresupuesto), '%d/%m/%Y %H:%i:%s'),
        STR_TO_DATE(CONCAT(lp.fechaPresupuesto, ' 00:00:00'), '%d/%m/%Y %H:%i:%s')
    ) AS `fecha`,
    CASE 
        WHEN lp.maquina REGEXP '^[0-9]+$' THEN CAST(lp.maquina AS UNSIGNED)
        ELSE NULL
    END AS `id_maquina`,
    ROUND(COALESCE(lt.tiempoDeCorte, 0) / 60) AS `tiempo_de_corte`,
    COALESCE(lt.precioCorte, 0.00) AS `precio_corte`,
    COALESCE(
        lm.materiales,
        CASE CAST(lt.material AS UNSIGNED)
            WHEN 11 THEN 'MDF 1mm'
            WHEN 12 THEN 'MDF 1,5mm'
            WHEN 13 THEN 'MDF 2mm'
            WHEN 17 THEN 'MDF 12mm'
            WHEN 18 THEN 'Carton Gris 0,5mm'
            WHEN 25 THEN 'Polyfan 4cm'
            WHEN 30 THEN 'Alto Impacto 3mm'
            WHEN 33 THEN 'Acrilico 4mm'
            WHEN 34 THEN 'Acrilico 5mm'
            WHEN 35 THEN 'Acrilico 6mm'
            WHEN 36 THEN 'Acrilico 7mm'
            WHEN 37 THEN 'Acrilico 8mm'
            WHEN 38 THEN 'Acrilico 9mm'
            WHEN 39 THEN 'Acrilico 10mm'
            WHEN 40 THEN 'Acrilico Color 2,4mm'
            WHEN 41 THEN 'Acrilico Color 3,2mm'
            WHEN 42 THEN 'Polipropileno'
            WHEN 43 THEN 'Material_id_43'
            WHEN 44 THEN 'Material_id_44'
            WHEN 45 THEN 'Terciado 3mm'
            WHEN 46 THEN 'Material_id_46'
            WHEN 47 THEN 'GomaEva 5mm'
            WHEN 48 THEN 'Material_id_48'
            WHEN 49 THEN 'Cuerina1'
            WHEN 50 THEN 'Cuerina2'
            WHEN 51 THEN 'Cartulina Encapada'
            WHEN 52 THEN 'Especificado en Archivo'
            WHEN 53 THEN 'Gotita'
            WHEN 54 THEN 'Gotita en gel'
            WHEN 55 THEN 'Telgopor Alta Densidad'
            WHEN 56 THEN 'Cinta Papel Auca 36 mm'
            WHEN 57 THEN 'Cinta Papel Stiko 18 mm'
            WHEN 58 THEN 'Cinta de Embalar'
            WHEN 59 THEN 'Cola Pritt'
            WHEN 60 THEN 'Unipox Chico'
            WHEN 61 THEN 'Unipox Grande'
            WHEN 71 THEN 'Material_id_71'
            WHEN 72 THEN 'Material_id_72'
            WHEN 73 THEN 'Material_id_73'
            WHEN 83 THEN 'Material_id_83'
            WHEN 84 THEN 'Material_id_84'
            WHEN 85 THEN 'Material_id_85'
            WHEN 99 THEN 'Material_id_99'
            WHEN 139 THEN 'ACRILICO NEGRO'
            ELSE CONCAT('Material ', lt.material)
        END,
        'Desconocido'
    ) AS `material`,
    COALESCE(lt.precioMaterial, 0.00) AS `precio_material`,
    COALESCE(lt.vectorizado, 0.00) AS `vectorizado`,
    COALESCE(lt.disenio, 0.00) AS `extra`,
    COALESCE(lt.vinilo, 0.00) AS `vinilo`
FROM `precision_schema`.`trabajopresupuestado` lt
JOIN `precision_schema`.`presupuesto` lp ON lt.idPResupuesto = lp.idPresupuesto
LEFT JOIN `precision_schema`.`materiales` lm ON CAST(lt.material AS UNSIGNED) = lm.idmateriales
WHERE lp.aprobado = 'si' AND lt.seleccionado = 1;
