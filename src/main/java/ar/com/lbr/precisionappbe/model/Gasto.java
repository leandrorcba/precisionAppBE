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
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "gastos")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto", nullable = false)
    private Integer id;

    @Column(name = "monto_gasto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoGasto;

    @Column(name = "motivo_gasto", nullable = false, length = 250)
    private String motivoGasto;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "responsable_gasto", nullable = false, length = 100)
    private String responsableGasto;

    @ColumnDefault("(UTC_TIMESTAMP())")
    @Column(name = "fecha_gasto", nullable = false)
    private Instant fechaGasto;

}
