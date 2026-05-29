package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "trabajo_presupuestado")
public class TrabajoPresupuestado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trabajo_presupuestado", nullable = false)
    private Integer id;

    @ColumnDefault("0")
    @Column(name = "seleccionado", nullable = false)
    private Boolean seleccionado = false;

    @Column(name = "archivo_cad")
    private String archivoCad;

    @Column(name = "archivo_original")
    private String archivoOriginal;

    @Column(name = "id_presupuesto", nullable = false)
    private Integer idPresupuesto;

    @Column(name = "notas")
    private String notas;

    @Column(name = "tiempo_de_corte", nullable = false)
    private Integer tiempoDeCorte;

    @Column(name = "id_materiales")
    private Integer idMateriales;

    @Column(name = "precio_material", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMaterial;

    @Column(name = "precio_trabajo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTrabajo;

    @ColumnDefault("0.00")
    @Column(name = "precio_corte", precision = 10, scale = 2)
    private BigDecimal precioCorte;

    @ColumnDefault("0.00")
    @Column(name = "vinilo", precision = 10, scale = 2)
    private BigDecimal vinilo;

    @ColumnDefault("0.00")
    @Column(name = "extra", precision = 10, scale = 2)
    private BigDecimal extra;

    @ColumnDefault("0.00")
    @Column(name = "vectorizado", precision = 10, scale = 2)
    private BigDecimal vectorizado;

    @ColumnDefault("0.00")
    @Column(name = "precio_minuto", precision = 10, scale = 2)
    private BigDecimal precioMinuto;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(name = "id_superficie")
    private Integer idSuperficie;

    @Column(name = "id_maquina")
    private Integer idMaquina;

    @ColumnDefault("0")
    @Column(name = "unidades")
    private Integer unidades = 0;

    @ColumnDefault("0")
    @Column(name = "grabado", columnDefinition = "TINYINT(1)")
    private Boolean grabado = false;

    @ColumnDefault("0")
    @Column(name = "cortes_especiales", columnDefinition = "TINYINT(1)")
    private Boolean cortesEspeciales = false;

    @ColumnDefault("0")
    @Column(name = "carteles", columnDefinition = "TINYINT(1)")
    private Boolean carteles = false;

    @ColumnDefault("0.0")
    @Column(name = "posicionador", precision = 12, scale = 2)
    private BigDecimal posicionador = BigDecimal.ZERO;

    @ColumnDefault("0")
    @Column(name = "trae_material", columnDefinition = "TINYINT(1)")
    private Boolean traeMaterial = false;

    @ColumnDefault("0.00")
    @Column(name = "precio_sin_descuento", precision = 10, scale = 2)
    private BigDecimal precioSinDescuento = BigDecimal.ZERO;

    @ColumnDefault("5")
    @Column(name = "minutos_por_punto")
    private Integer minutosPorPunto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoTrabajo estado;

}