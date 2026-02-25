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
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "costo_fijo", schema = "precision_schema_v2")
public class CostoFijo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_costo_fijo", nullable = false)
    private Integer id;

    @Column(name = "periodo", nullable = false)
    private LocalDate periodo;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_cambio", nullable = false)
    private Instant fechaCambio;

    @ColumnDefault("0.00")
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

}