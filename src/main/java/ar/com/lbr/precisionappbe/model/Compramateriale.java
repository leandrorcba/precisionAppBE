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
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "compramateriales", schema = "precision_schema_v2")
public class Compramateriale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcompraMateriales", nullable = false)
    private Integer id;

    @Column(name = "material", length = 45)
    private String material;

    @Column(name = "tipo", length = 45)
    private String tipo;

    @Column(name = "montounitario", precision = 12, scale = 2)
    private BigDecimal montounitario;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "montototal", precision = 12, scale = 2)
    private BigDecimal montototal;

    @Column(name = "fechacompra")
    private LocalDate fechacompra;

    @Column(name = "mesCompra", length = 45)
    private String mesCompra;

    @Column(name = "caja", length = 45)
    private String caja;

    @Column(name = "horaCompra")
    private LocalTime horaCompra;

}