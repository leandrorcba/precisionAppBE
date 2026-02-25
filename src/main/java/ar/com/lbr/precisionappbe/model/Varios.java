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
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "varios", schema = "precision_schema_v2")
public class Varios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_varios", nullable = false)
    private Integer id;

    @Column(name = "precio_minuto", precision = 10, scale = 2)
    private BigDecimal precioMinuto;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    @Column(name = "ajuste", precision = 10, scale = 2)
    private BigDecimal ajuste;

    @Column(name = "descuento_efectivo", precision = 10, scale = 2)
    private BigDecimal descuentoEfectivo;

    @Column(name = "hora_inicio_fds")
    private LocalTime horaInicioFds;

    @Column(name = "hora_cierre_fds")
    private LocalTime horaCierreFds;

}