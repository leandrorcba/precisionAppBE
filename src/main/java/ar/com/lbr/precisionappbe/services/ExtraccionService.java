package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ExtraccionDTO;
import ar.com.lbr.precisionappbe.model.Extraccione;
import ar.com.lbr.precisionappbe.repositories.ExtraccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ExtraccionService {

    private final ExtraccionRepository extraccionRepository;
    private final AuditLogService auditLogService;

    public ExtraccionService(ExtraccionRepository extraccionRepository, AuditLogService auditLogService) {
        this.extraccionRepository = extraccionRepository;
        this.auditLogService = auditLogService;
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

    @Transactional
    public ExtraccionDTO createExtraccion(ExtraccionDTO dto) {
        Extraccione extraccione = new Extraccione();
        extraccione.setIdUsuario(dto.getIdUsuario());
        extraccione.setMontoExtraccion(dto.getMontoExtraccion());
        extraccione.setMotivoExtraccion(dto.getMotivoExtraccion());
        extraccione.setFechaExtraccion(dto.getFechaExtraccion() != null ? dto.getFechaExtraccion() : Instant.now());

        Extraccione saved = extraccionRepository.save(extraccione);
        ExtraccionDTO result = ExtraccionDTO.toDTO(saved);

        auditLogService.log("CREAR", "EXTRACCIONES", saved.getId().toString(),
                "Extracción de caja chica #" + saved.getId() + " registrada por $" + saved.getMontoExtraccion()
                + " (Motivo: " + saved.getMotivoExtraccion() + ")", result);

        return result;
    }

    @Transactional
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
        ExtraccionDTO result = ExtraccionDTO.toDTO(updated);

        auditLogService.log("MODIFICAR", "EXTRACCIONES", updated.getId().toString(),
                "Extracción de caja chica #" + updated.getId() + " modificada (Monto: $" + updated.getMontoExtraccion()
                + ", Motivo: " + updated.getMotivoExtraccion() + ")", result);

        return result;
    }

    @Transactional
    public void deleteExtraccion(Integer id) {
        Extraccione extraccione = extraccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Extracción no encontrada"));

        auditLogService.log("ELIMINAR", "EXTRACCIONES", id.toString(),
                "Extracción de caja chica #" + id + " eliminada (Monto: $" + extraccione.getMontoExtraccion()
                + ", Motivo: " + extraccione.getMotivoExtraccion() + ")", ExtraccionDTO.toDTO(extraccione));

        extraccionRepository.delete(extraccione);
    }
}
