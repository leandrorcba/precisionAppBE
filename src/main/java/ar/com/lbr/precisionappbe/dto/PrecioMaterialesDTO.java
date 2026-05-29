package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.PrecioMateriale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrecioMaterialesDTO {

    private Integer id;
    private Integer idMateriales;
    private String nombreMaterial;
    private Short unidades;
    private Integer idSuperficie;
    private BigDecimal precioMaterial;

    public PrecioMaterialesDTO(PrecioMateriale entity, String nombreMaterial) {
        this.id = entity.getId();
        this.idMateriales = entity.getIdMateriales();
        this.nombreMaterial = nombreMaterial;
        this.unidades = entity.getUnidades();
        this.idSuperficie = entity.getIdSuperficie();
        this.precioMaterial = entity.getPrecioMaterial();
    }

    public static PrecioMaterialesDTO toDTO(PrecioMateriale entity, String nombreMaterial) {
        if (entity == null) {
            return null;
        }
        return new PrecioMaterialesDTO(entity, nombreMaterial);
    }
}
