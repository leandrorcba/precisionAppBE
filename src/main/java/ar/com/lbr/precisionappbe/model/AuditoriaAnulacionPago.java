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
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "auditoria_anulacion_pago")
public class AuditoriaAnulacionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria", nullable = false)
    private Integer id;

    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    @Column(name = "id_presupuesto", nullable = false)
    private Integer idPresupuesto;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "cliente_nombre", length = 150)
    private String clienteNombre;

    @Column(name = "usuario_creador", length = 50)
    private String usuarioCreador;

    @Column(name = "usuario_anulador", length = 50)
    private String usuarioAnulador;

    @Column(name = "fecha_hora_anulacion", nullable = false)
    private Instant fechaHoraAnulacion;

    @Column(name = "motivo", nullable = false, length = 250)
    private String motivo;

    @Column(name = "fecha_hora_pago")
    private Instant fechaHoraPago;

    @Column(name = "tipo_pago", length = 50)
    private String tipoPago;

    @Column(name = "medio_pago", length = 100)
    private String medioPago;
}
