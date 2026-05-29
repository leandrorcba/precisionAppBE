package ar.com.lbr.precisionappbe.model;

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
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "varios")
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

    @Column(name = "ajuste")
    private Integer ajuste;

    @Column(name = "precio_minuto_empresa", precision = 10, scale = 2)
    private BigDecimal precioMinutoEmpresa;

    @Column(name = "descuento_efectivo")
    private Integer descuentoEfectivo;

    @Column(name = "hora_inicio_fds")
    private LocalTime horaInicioFds;

    @Column(name = "hora_cierre_fds")
    private LocalTime horaCierreFds;

    @Column(name = "descuento_por_punto")
    private Integer descuentoPorPunto;

    @ColumnDefault("5")
    @Column(name = "minutos_por_punto")
    private Integer minutosPorPunto;

}