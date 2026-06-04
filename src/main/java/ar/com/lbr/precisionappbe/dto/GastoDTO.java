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
    private BigDecimal montoGasto;
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
