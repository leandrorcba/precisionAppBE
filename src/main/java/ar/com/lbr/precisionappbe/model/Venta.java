package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ventas", schema = "precision_schema_v2")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ventas", nullable = false)
    private Integer id;

    @Column(name = "fecha_venta")
    private LocalDate fechaVenta;

    @Column(name = "hora_venta")
    private LocalTime horaVenta;

    @Size(max = 45)
    @Column(name = "material", length = 45)
    private String material;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_materiales", nullable = false)
    private Material idMateriales;

    @Size(max = 45)
    @Column(name = "superficie", length = 45)
    private String superficie;

    @Column(name = "precio_material", precision = 10, scale = 2)
    private BigDecimal precioMaterial;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

}