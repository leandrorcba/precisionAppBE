package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "presupuesto")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "fecha_hora_presupuesto", nullable = false)
    private LocalDateTime fechaHoraPresupuesto;

    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @NotNull
    @Column(name = "precio_cobrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCobrado;

    @NotNull
    @Column(name = "precio_sin_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioSinDescuento;

    @Column(name = "precio_minuto", precision = 10, scale = 2)
    private BigDecimal precioMinuto;

    @NotNull
    @Column(name = "aprobado", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean aprobado;

    @NotNull
    @Column(name = "realizado", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean realizado;

    @NotNull
    @Column(name = "cobrado", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean cobrado;

    @NotNull
    @Column(name = "entregado", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean entregado;

    @NotNull
    @Column(name = "puntos_canjeados", nullable = false)
    private Integer puntosCanjeados;

    @Column(name = "fecha_cobrado")
    private LocalDateTime fechaCobrado;

    @Column(name = "fecha_realizado")
    private LocalDateTime fechaRealizado;

}