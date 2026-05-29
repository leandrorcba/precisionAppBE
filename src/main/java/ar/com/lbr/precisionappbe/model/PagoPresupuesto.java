package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pago_presupuesto")
public class PagoPresupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_presupuesto", nullable = false)
    private Integer id;

    @Column(name = "id_presupuesto", nullable = false)
    private Integer idPresupuesto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_pago", nullable = false)
    private TipoPago idTipoPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medio_pago", nullable = false)
    private MedioPago idMedioPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarjeta")
    private Tarjeta idTarjeta;

    @Column(name = "cuotas", columnDefinition = "tinyint UNSIGNED")
    private Short cuotas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_bancaria")
    private CuentaBancaria idCuentaBancaria;

    @Column(name = "autorizacion", length = 45)
    private String autorizacion;

    @Column(name = "notas")
    private String notas;

    @ColumnDefault("1")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

}