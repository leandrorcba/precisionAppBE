package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.GastoDTO;
import ar.com.lbr.precisionappbe.model.Gasto;
import ar.com.lbr.precisionappbe.repositories.GastoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoService {

    private final GastoRepository gastoRepository;

    public GastoService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    public List<GastoDTO> getAllGastos() {
        return gastoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaGasto")).stream()
                .map(GastoDTO::toDTO)
                .collect(Collectors.toList());
    }

    public GastoDTO createGasto(GastoDTO dto) {
        Gasto gasto = new Gasto();
        gasto.setMontoGasto(dto.getMontoGasto());
        gasto.setMotivoGasto(dto.getMotivoGasto());
        gasto.setIdUsuario(dto.getIdUsuario());
        gasto.setResponsableGasto(dto.getResponsableGasto());
        gasto.setFechaGasto(dto.getFechaGasto() != null ? dto.getFechaGasto() : Instant.now());

        Gasto saved = gastoRepository.save(gasto);
        return GastoDTO.toDTO(saved);
    }

    public void deleteGasto(Integer id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + id));
        gastoRepository.delete(gasto);
    }
}
