package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.PagoMapper;
import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.dto.response.ClienteResponse;
import ar.com.lbr.precisionappbe.dto.response.PagosResponse;
import ar.com.lbr.precisionappbe.model.Pago;
import ar.com.lbr.precisionappbe.repositories.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagosService {

    PagoRepository pagoRepository;
    PagoMapper pagoMapper;

    public PagosService(PagoRepository pagoRepository, PagoMapper pagoMapper) {
        this.pagoRepository = pagoRepository;
        this.pagoMapper = pagoMapper;
    }

    public PagosResponse gepPagosByIDPresupuesto(Integer idPresupuesto) {
        List<Pago> pagoList = pagoRepository.findByIdOrigenPago(idPresupuesto);

        List<PagoDTO> pagoDTOS = pagoMapper.map(pagoList);
        return new PagosResponse(pagoDTOS, Long.valueOf(pagoList.size()));
    }
}
