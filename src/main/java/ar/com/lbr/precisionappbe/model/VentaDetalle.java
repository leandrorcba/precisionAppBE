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
@Table(name = "venta_detalle", schema = "precision_schema_v2")
public class VentaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta_detalle", nullable = false)
    private Integer id;

    @Column(name = "id_venta", nullable = false)
    private Integer idVenta;

    @Column(name = "material", nullable = false, length = 45)
    private String material;

    @Column(name = "superficie", length = 45)
    private String superficie;

    @Column(name = "precio_material", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMaterial;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

}