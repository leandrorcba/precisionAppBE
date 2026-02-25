package ar.com.lbr.precisionappbe.Mapper;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.model.Pago;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PagoMapper {


    public List<PagoDTO> map(List<Pago> pagoList) {
        return pagoList.stream()
                .map(PagoDTO::toDTO)
                .collect(Collectors.toList());
    }
}
