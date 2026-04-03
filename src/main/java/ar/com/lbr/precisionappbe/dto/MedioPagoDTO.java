package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.MedioPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedioPagoDTO {
    private Integer id;
    private String tipo;
    private String descripcion;

    public MedioPagoDTO(MedioPago entity) {
        this.id = entity.getId();
        this.tipo = entity.getTipo();
        this.descripcion = entity.getDescripcion();
    }

    public static MedioPagoDTO toDTO(MedioPago entity) {
        if (entity == null) {
            return null;
        }
        return new MedioPagoDTO(entity);
    }
}
