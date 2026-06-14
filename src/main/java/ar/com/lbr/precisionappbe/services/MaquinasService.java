package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MaquinaDTO;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaquinasService {

    private final MaquinasRepository maquinasRepository;

    public MaquinasService(MaquinasRepository maquinasRepository) {
        this.maquinasRepository = maquinasRepository;
    }

    public List<MaquinaDTO> getAllMaquinas() {
        return maquinasRepository.findAll().stream()
                .map(MaquinaDTO::toDTO)
                .collect(Collectors.toList());
    }

    public MaquinaDTO createMaquina(MaquinaDTO dto) {
        Maquina maquina = new Maquina();
        maquina.setNombreMaquina(dto.getTitle());
        maquina.setHabilitada(dto.getIsHabilitada() != null ? dto.getIsHabilitada() : true);
        maquina.setFechaCreacion(Instant.now());
        
        Maquina saved = maquinasRepository.save(maquina);
        dto.setId(saved.getId());
        return dto;
    }

    public MaquinaDTO updateMaquina(MaquinaDTO dto) {
        Maquina maquina = maquinasRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Maquina no encontrada: " + dto.getId()));
        
        maquina.setNombreMaquina(dto.getTitle());
        if (dto.getIsHabilitada() != null) {
            maquina.setHabilitada(dto.getIsHabilitada());
        }
        
        maquinasRepository.save(maquina);
        return dto;
    }
}
