package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.CostoFijoDetalle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CostoFijoDetalleDTO {

    private Integer id;
    private Integer idTipoCostoFijo;
    private String nombreTipo;
    private BigDecimal monto;

    public CostoFijoDetalleDTO(CostoFijoDetalle entity) {
        this.id = entity.getId();
        this.idTipoCostoFijo = entity.getIdTipoCostoFijo().getId();
        this.nombreTipo = entity.getIdTipoCostoFijo().getNombre();
        this.monto = entity.getMonto();
    }

    public static CostoFijoDetalleDTO toDTO(CostoFijoDetalle entity) {
        if (entity == null) {
            return null;
        }
        return new CostoFijoDetalleDTO(entity);
    }
}