package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.CostoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CostoFijoDTO {

    private Integer id;
    private LocalDate periodo;
    private Instant fechaCambio;
    private BigDecimal total;
    private List<CostoFijoDetalleDTO> detalles;

    public CostoFijoDTO(CostoFijo entity, List<CostoFijoDetalleDTO> detalles) {
        this.id = entity.getId();
        this.periodo = entity.getPeriodo();
        this.fechaCambio = entity.getFechaCambio();
        this.total = entity.getTotal();
        this.detalles = detalles;
    }

    public static CostoFijoDTO toDTO(CostoFijo entity, List<CostoFijoDetalleDTO> detalles) {
        if (entity == null) {
            return null;
        }
        return new CostoFijoDTO(entity, detalles);
    }
}