package ar.com.lbr.precisionappbe.Mapper;

import ar.com.lbr.precisionappbe.dto.MaquinaDTO;
import ar.com.lbr.precisionappbe.model.Maquina;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaquinasMapper {

    public List<MaquinaDTO> map(List<Maquina> maquinas) {

        return maquinas.stream()
                .map(MaquinaDTO::new)
                .collect(Collectors.toList());
    }

    public Maquina toEntity(MaquinaDTO dto, boolean nuevo) {
        Maquina maquina = new Maquina();
        if (!nuevo) {
            maquina.setId(dto.getId()); // solo si estás editando
        }
        maquina.setNombreMaquina(dto.getTitle());
        maquina.setHabilitada(dto.getIsHabilitada());

        return maquina;
    }
}
