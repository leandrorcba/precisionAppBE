package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.MaquinasMapper;
import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.MaquinaDTO;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaquinasService {

    private final MaquinasRepository maquinasRepository;
    private final MaquinasMapper maquinasMapper;

    public MaquinasService(MaquinasRepository maquinasRepository,
                           MaquinasMapper maquinasMapper) {
        this.maquinasRepository = maquinasRepository;
        this.maquinasMapper = maquinasMapper;
    }

    public List<MaquinaDTO> getAllMaquinas() {
        return maquinasRepository.findAll().stream()
                .map(MaquinaDTO::toDTO)
                .collect(Collectors.toList());
    }

    public MaquinaDTO createMaquina(MaquinaDTO dto) {

        Maquina clienteEntity = maquinasMapper.toEntity(dto, true);

        Maquina maquina = maquinasRepository.save(clienteEntity);

        dto.setId(maquina.getId());

        return dto;
    }

    public MaquinaDTO updateMaquina(MaquinaDTO dto) {

        Maquina clienteEntity = maquinasMapper.toEntity(dto, false);

        Maquina maquina = maquinasRepository.save(clienteEntity);

        dto.setId(maquina.getId());

        return dto;
    }
}
