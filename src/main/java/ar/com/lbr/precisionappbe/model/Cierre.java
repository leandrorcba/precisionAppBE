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
@Table(name = "cierre", schema = "precision_schema_v2")
public class Cierre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cierre", nullable = false)
    private Integer id;

    @ColumnDefault("0.00")
    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial;

    @ColumnDefault("0.00")
    @Column(name = "monto_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoFinal;

    @ColumnDefault("0.00")
    @Column(name = "monto_extracciones", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoExtracciones;

    @ColumnDefault("0.00")
    @Column(name = "monto_presupuestos", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPresupuestos;

    @Column(name = "descuento_efectivo", precision = 12, scale = 2)
    private BigDecimal descuentoEfectivo;

    @ColumnDefault("0.00")
    @Column(name = "arqueo", nullable = false, precision = 12, scale = 2)
    private BigDecimal arqueo;

    @Column(name = "diferencia", precision = 12, scale = 2)
    private BigDecimal diferencia;

    @Column(name = "responsable", length = 100)
    private String responsable;

    @ColumnDefault("(UTC_TIMESTAMP())")
    @Column(name = "fecha_cierre", nullable = false)
    private Instant fechaCierre;

    @ColumnDefault("0.00")
    @Column(name = "senia", nullable = false, precision = 12, scale = 2)
    private BigDecimal senia;

    @ColumnDefault("0.00")
    @Column(name = "ventas", nullable = false, precision = 12, scale = 2)
    private BigDecimal ventas;

    @ColumnDefault("0.00")
    @Column(name = "monto_compra_materiales", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoCompraMateriales;

    @Column(name = "mes_cierre", length = 45)
    private String mesCierre;

    @Column(name = "id_user")
    private String idUsuario;

}