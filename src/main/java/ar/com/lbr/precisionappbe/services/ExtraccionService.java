package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ExtraccionDTO;
import ar.com.lbr.precisionappbe.model.Extraccione;
import ar.com.lbr.precisionappbe.repositories.ExtraccionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExtraccionService {

    private final ExtraccionRepository extraccionRepository;

    public ExtraccionService(ExtraccionRepository extraccionRepository) {
        this.extraccionRepository = extraccionRepository;
    }

    public List<ExtraccionDTO> getAllExtracciones(LocalDate fechaDesde, LocalDate fechaHasta) {
        LocalDate desde = fechaDesde != null ? fechaDesde : LocalDate.now();
        LocalDate hasta = fechaHasta != null ? fechaHasta : desde;

        Instant desdeInstant = desde.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant hastaInstant = hasta.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        return extraccionRepository.findByFechaExtraccionBetween(desdeInstant, hastaInstant).stream()
                .map(ExtraccionDTO::toDTO)
                .collect(Collectors.toList());
    }

    public ExtraccionDTO createExtraccion(ExtraccionDTO dto) {
        Extraccione extraccione = new Extraccione();
        extraccione.setIdUsuario(dto.getIdUsuario());
        extraccione.setMontoExtraccion(dto.getMontoExtraccion());
        extraccione.setMotivoExtraccion(dto.getMotivoExtraccion());
        extraccione.setFechaExtraccion(dto.getFechaExtraccion() != null ? dto.getFechaExtraccion() : Instant.now());

        Extraccione saved = extraccionRepository.save(extraccione);
        return ExtraccionDTO.toDTO(saved);
    }

    public ExtraccionDTO updateExtraccion(Integer id, ExtraccionDTO dto) {
        Extraccione extraccione = extraccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Extracción no encontrada"));

        extraccione.setIdUsuario(dto.getIdUsuario());
        extraccione.setMontoExtraccion(dto.getMontoExtraccion());
        extraccione.setMotivoExtraccion(dto.getMotivoExtraccion());
        if (dto.getFechaExtraccion() != null) {
            extraccione.setFechaExtraccion(dto.getFechaExtraccion());
        }

        Extraccione updated = extraccionRepository.save(extraccione);
        return ExtraccionDTO.toDTO(updated);
    }

    public void deleteExtraccion(Integer id) {
        Extraccione extraccione = extraccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Extracción no encontrada"));

        extraccionRepository.delete(extraccione);
    }
}
