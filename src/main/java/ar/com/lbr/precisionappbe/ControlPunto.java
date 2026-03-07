package ar.com.lbr.precisionappbe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "control_puntos", schema = "precision_schema_v2")
public class ControlPunto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control_puntos", nullable = false)
    private Integer id;

    @Column(name = "id_presupuesto")
    private Integer idPresupuesto;

    @ColumnDefault("0")
    @Column(name = "puntos_acumulados")
    private Integer puntosAcumulados;

    @ColumnDefault("0")
    @Column(name = "puntos_acumulados_historicos")
    private Integer puntosAcumuladosHistoricos;

    @ColumnDefault("0")
    @Column(name = "puntos_por_corte_trabajos")
    private Integer puntosPorCorteTrabajos;

    @ColumnDefault("0")
    @Column(name = "puntos_totales_trabajo")
    private Integer puntosTotalesTrabajo;

    @ColumnDefault("0")
    @Column(name = "minutos_disponibles")
    private Integer minutosDisponibles;

    @ColumnDefault("0")
    @Column(name = "minutos_canjeados")
    private Integer minutosCanjeados;

    @ColumnDefault("0")
    @Column(name = "puntos_canjeados")
    private Integer puntosCanjeados;

    @ColumnDefault("0")
    @Column(name = "puntos_acumulados_nuevos")
    private Integer puntosAcumuladosNuevos;

    @ColumnDefault("0")
    @Column(name = "puntos_acumulados_historicos_nuevo")
    private Integer puntosAcumuladosHistoricosNuevo;

    @Column(name = "precio_minuto", precision = 10, scale = 2)
    private BigDecimal precioMinuto;

    @Column(name = "fecha_canje_puntos")
    private Instant fechaCanjePuntos;

}