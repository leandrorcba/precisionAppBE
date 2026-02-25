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

@Getter
@Setter
@Entity
@Table(name = "precio_materiales", schema = "precision_schema_v2")
public class PrecioMateriale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_precio_materiales", nullable = false)
    private Integer id;

    @Column(name = "id_materiales")
    private Integer idMateriales;

    @Column(name = "unidades", columnDefinition = "tinyint UNSIGNED")
    private Short unidades;

    @Column(name = "id_superficie", columnDefinition = "tinyint UNSIGNED")
    private Short idSuperficie;

    @Column(name = "superficie", length = 45)
    private String superficie;

    @Column(name = "precio_material", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMaterial;

}