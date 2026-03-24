package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoClienteDTO {

    private Integer id;
    private String nombreTipo;

    public TipoClienteDTO(TipoCliente entity) {
        this.id = entity.getId();
        this.nombreTipo = entity.getNombreTipo();
    }

    public static TipoClienteDTO toDTO(TipoCliente entity) {
        if (entity == null) {
            return null;
        }
        return new TipoClienteDTO(entity);
    }
}