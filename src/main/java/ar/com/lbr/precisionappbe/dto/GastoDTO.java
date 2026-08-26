package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Gasto;
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
public class GastoDTO {

    private Integer id;
    @jakarta.validation.constraints.NotNull(message = "El monto del gasto es requerido")
    @jakarta.validation.constraints.Positive(message = "El monto del gasto debe ser mayor a 0")
    private BigDecimal montoGasto;
    @jakarta.validation.constraints.NotBlank(message = "El motivo del gasto no puede estar vacío")
    private String motivoGasto;
    private Integer idUsuario;
    private String responsableGasto;
    private Instant fechaGasto;

    public GastoDTO(Gasto g) {
        this.id = g.getId();
        this.montoGasto = g.getMontoGasto();
        this.motivoGasto = g.getMotivoGasto();
        this.idUsuario = g.getIdUsuario();
        this.responsableGasto = g.getResponsableGasto();
        this.fechaGasto = g.getFechaGasto();
    }

    public static GastoDTO toDTO(Gasto g) {
        if (g == null) {
            return null;
        }
        return new GastoDTO(g);
    }
}
