package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Superficie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuperficieDTO {

    private Integer id;
    private String valor;

    public SuperficieDTO(Superficie entity) {
        this.id = entity.getId();
        this.valor = entity.getValor();
    }

    public static SuperficieDTO toDTO(Superficie entity) {
        if (entity == null) {
            return null;
        }
        return new SuperficieDTO(entity);
    }
}