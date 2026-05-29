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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pago_venta")
public class PagoVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_venta", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta idVenta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_pago", nullable = false)
    private TipoPago idTipoPago;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medio_pago", nullable = false)
    private MedioPago idMedioPago;

    @NotNull
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @NotNull
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

    @Size(max = 45)
    @Column(name = "autorizacion", length = 45)
    private String autorizacion;

    @Size(max = 255)
    @Column(name = "notas")
    private String notas;

}