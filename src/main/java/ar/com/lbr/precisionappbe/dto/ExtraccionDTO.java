package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Extraccione;
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
public class ExtraccionDTO {

    private Integer id;
    private Integer idUsuario;
    private BigDecimal montoExtraccion;
    private String motivoExtraccion;
    private Instant fechaExtraccion;

    public ExtraccionDTO(Extraccione e) {
        this.id = e.getId();
        this.idUsuario = e.getIdUsuario();
        this.montoExtraccion = e.getMontoExtraccion();
        this.motivoExtraccion = e.getMotivoExtraccion();
        this.fechaExtraccion = e.getFechaExtraccion();
    }

    public static ExtraccionDTO toDTO(Extraccione e) {
        if (e == null) {
            return null;
        }
        return new ExtraccionDTO(e);
    }
}
