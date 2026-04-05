package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.TipoCostoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoCostoFijoDTO {

    private Integer id;
    private String codigo;
    private String nombre;
    private Boolean activo;

    public TipoCostoFijoDTO(TipoCostoFijo entity) {
        this.id = entity.getId();
        this.codigo = entity.getCodigo();
        this.nombre = entity.getNombre();
        this.activo = entity.getActivo();
    }

    public static TipoCostoFijoDTO toDTO(TipoCostoFijo entity) {
        if (entity == null) {
            return null;
        }
        return new TipoCostoFijoDTO(entity);
    }
}