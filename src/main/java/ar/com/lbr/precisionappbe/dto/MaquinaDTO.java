package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Maquina;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaquinaDTO {
    private Integer id;
    private String title;
    private Boolean isHabilitada;

    public MaquinaDTO(Maquina maquina) {
        this.id = maquina.getId();
        this.title = maquina.getNombreMaquina();
        this.isHabilitada = maquina.getHabilitada();
    }

    public static MaquinaDTO toDTO(Maquina maquina) {
        if (maquina == null)
            return null;
        return new MaquinaDTO(maquina);
    }
}
