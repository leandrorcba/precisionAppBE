package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Punto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PuntoDTO {

    private Integer id;
    private LocalDate fechaPrimerPunto;
    private Integer puntosAcumulados;
    private Integer puntosAcumuladosHistorico;

    public PuntoDTO(Punto p) {
        this.id = p.getId();
        this.fechaPrimerPunto = p.getFechaPrimerPunto();
        this.puntosAcumulados = p.getPuntosAcumulados();
        this.puntosAcumuladosHistorico = p.getPuntosAcumuladosHistorico();
    }

    public static PuntoDTO toDTO(Punto p) {
        if (p == null) return null;
        return new PuntoDTO(p);
    }
}
