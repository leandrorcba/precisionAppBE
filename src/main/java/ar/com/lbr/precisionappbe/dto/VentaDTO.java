package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Venta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaDTO {

    private Integer id;
    private LocalDate fechaVenta;
    private LocalTime horaVenta;
    @jakarta.validation.constraints.NotNull(message = "El material es requerido")
    private Integer idMateriales;
    @jakarta.validation.constraints.NotNull(message = "El precio del material es requerido")
    @jakarta.validation.constraints.PositiveOrZero(message = "El precio del material debe ser mayor o igual a 0")
    private BigDecimal precioMaterial;
    @jakarta.validation.constraints.NotNull(message = "La cantidad es requerida")
    @jakarta.validation.constraints.Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    @jakarta.validation.constraints.NotNull(message = "El precio de venta es requerido")
    @jakarta.validation.constraints.PositiveOrZero(message = "El precio de venta debe ser mayor o igual a 0")
    private BigDecimal precioVenta;
    private String material;
    private String superficie;
    private BigDecimal montoAbonado;

    public VentaDTO(Venta v) {
        this.id = v.getId();
        this.idMateriales = v.getIdMateriales() != null ? v.getIdMateriales().getId() : null;
        this.precioMaterial = v.getPrecioMaterial();
        this.cantidad = v.getCantidad();
        this.precioVenta = v.getPrecioVenta();
        this.material = v.getIdMateriales() != null ? v.getIdMateriales().getMateriales() : null;
        this.superficie = v.getSuperficie();
        this.montoAbonado = BigDecimal.ZERO;
        if (v.getFechaHoraVenta() != null) {
            java.time.ZonedDateTime zdt = v.getFechaHoraVenta().atZone(java.time.ZoneId.of("America/Argentina/Buenos_Aires"));
            this.fechaVenta = zdt.toLocalDate();
            this.horaVenta = zdt.toLocalTime();
        }
    }

    public static VentaDTO toDTO(Venta v) {
        if (v == null) {
            return null;
        }
        return new VentaDTO(v);
    }
}
