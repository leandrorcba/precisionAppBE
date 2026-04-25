package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "compra_materiales", schema = "precision_schema_v2")
public class CompraMateriale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra_materiales", nullable = false)
    private Integer id;

    @Column(name = "id_materiales")
    private Integer idMateriales;

    @Size(max = 45)
    @Column(name = "material", length = 45)
    private String material;

    @Size(max = 45)
    @Column(name = "tipo", length = 45)
    private String tipo;

    @Column(name = "monto_unitario", precision = 12, scale = 2)
    private BigDecimal montoUnitario;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "monto_total", precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "fecha_hora_compra")
    private Instant fechaHoraCompra;

    @Size(max = 45)
    @Column(name = "caja", length = 45)
    private String caja;

    @Column(name = "id_user")
    private Integer idUser;

}