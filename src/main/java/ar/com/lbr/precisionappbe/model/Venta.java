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
@Table(name = "ventas", schema = "precision_schema_v2")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ventas", nullable = false)
    private Integer id;

    @Column(name = "material", length = 45)
    private String material;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "fecha_hora_venta")
    private Instant fechaHoraVenta;

    @Column(name = "id_material")
    private Integer idMaterial;

    @Column(name = "id_insumos")
    private Integer idInsumos;

    @Column(name = "precio_material", precision = 10, scale = 2)
    private BigDecimal precioMaterial;

    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;

}