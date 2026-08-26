package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.CompraMateriale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraMaterialesDTO {

    private Integer id;
    private Integer idMateriales;
    private String material;
    private String tipo;
    @jakarta.validation.constraints.NotNull(message = "El monto unitario es requerido")
    @jakarta.validation.constraints.PositiveOrZero(message = "El monto unitario debe ser mayor o igual a 0")
    private BigDecimal montoUnitario;
    @jakarta.validation.constraints.NotNull(message = "La cantidad es requerida")
    @jakarta.validation.constraints.Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    @jakarta.validation.constraints.NotNull(message = "El monto total es requerido")
    @jakarta.validation.constraints.PositiveOrZero(message = "El monto total debe ser mayor o igual a 0")
    private BigDecimal montoTotal;
    private Instant fechaHoraCompra;
    private String caja;
    private Integer idUser;
    private String username;

    public CompraMaterialesDTO(CompraMateriale c) {
        this.id = c.getId();
        this.idMateriales = c.getIdMateriales();
        this.material = c.getMaterial();
        this.tipo = c.getTipo();
        this.montoUnitario = c.getMontoUnitario();
        this.cantidad = c.getCantidad();
        this.montoTotal = c.getMontoTotal();
        this.fechaHoraCompra = c.getFechaHoraCompra();
        this.caja = c.getCaja();
        this.idUser = c.getIdUser();
    }

    public static CompraMaterialesDTO toDTO(CompraMateriale c) {
        if (c == null) {
            return null;
        }
        return new CompraMaterialesDTO(c);
    }
}
