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
@Table(name = "extracciones")
public class Extraccione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_extraccion", nullable = false)
    private Integer id;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "monto_extraccion", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoExtraccion;

    @Column(name = "motivo_extraccion")
    private String motivoExtraccion;

    @ColumnDefault("(UTC_TIMESTAMP())")
    @Column(name = "fecha_extraccion", nullable = false)
    private Instant fechaExtraccion;

}