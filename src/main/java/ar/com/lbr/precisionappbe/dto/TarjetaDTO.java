package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Tarjeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaDTO {
    private Integer id;
    private String nombre;

    public static TarjetaDTO toDTO(Tarjeta t) {
        if (t == null) {
            return null;
        }
        return new TarjetaDTO(t.getId(), t.getNombre());
    }
}
