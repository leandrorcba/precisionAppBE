package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "presupuesto", schema = "precision_schema_v2")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto", nullable = false)
    private Integer id;

    @Column(name = "fecha_hora_presupuesto", nullable = false)
    private LocalDateTime fechaHoraPresupuesto;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "precio_cobrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCobrado;

    @Column(name = "precio_sin_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioSinDescuento;

    @Column(name = "precio_minuto", precision = 10, scale = 2)
    private BigDecimal precioMinuto;

    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado;

    @Column(name = "realizado", nullable = false)
    private Boolean realizado;

    @Column(name = "cobrado", nullable = false)
    private Boolean cobrado;

    @Column(name = "entregado", nullable = false)
    private Boolean entregado;

    @Column(name = "puntos_canjeados")
    private Integer puntosCanjeados;

    @Column(name = "fecha_cobrado")
    private LocalDateTime fechaCobrado;

    @Column(name = "fecha_realizado")
    private LocalDateTime fechaRealizado;

}