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
    private Integer idMateriales;
    private BigDecimal precioMaterial;
    private Integer cantidad;
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
