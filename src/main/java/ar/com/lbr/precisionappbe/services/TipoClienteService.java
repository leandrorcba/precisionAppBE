package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TipoClienteService {

    private TipoClienteRepository tipoClienteRepository;

    public TipoClienteService(TipoClienteRepository tipoClienteRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
    }

    TipoClienteDTO getTipoClienteById(Integer id) {
        return TipoClienteDTO.toDTO(tipoClienteRepository.findById(id).orElse(null));
    }
}
