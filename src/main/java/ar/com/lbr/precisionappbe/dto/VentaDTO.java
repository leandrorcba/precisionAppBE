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
    private String material;
    private Integer idMateriales;
    private String superficie;
    private BigDecimal precioMaterial;
    private Integer cantidad;
    private BigDecimal precioVenta;

    public VentaDTO(Venta v) {
        this.id = v.getId();
        this.fechaVenta = v.getFechaVenta();
        this.horaVenta = v.getHoraVenta();
        this.material = v.getMaterial();
        this.idMateriales = v.getIdMateriales() != null ? v.getIdMateriales().getId() : null;
        this.superficie = v.getSuperficie();
        this.precioMaterial = v.getPrecioMaterial();
        this.cantidad = v.getCantidad();
        this.precioVenta = v.getPrecioVenta();
    }

    public static VentaDTO toDTO(Venta v) {
        if (v == null) {
            return null;
        }
        return new VentaDTO(v);
    }
}
