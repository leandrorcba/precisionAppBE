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
    private final AuditLogService auditLogService;

    public GastoService(GastoRepository gastoRepository, AuditLogService auditLogService) {
        this.gastoRepository = gastoRepository;
        this.auditLogService = auditLogService;
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
        GastoDTO result = GastoDTO.toDTO(saved);

        auditLogService.log("CREAR", "GASTOS", saved.getId().toString(),
                "Gasto registrado por $" + saved.getMontoGasto() + " - Responsable: " + saved.getResponsableGasto()
                + " (Motivo: " + saved.getMotivoGasto() + ")", result);

        return result;
    }

    public void deleteGasto(Integer id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + id));

        auditLogService.log("ELIMINAR", "GASTOS", id.toString(),
                "Gasto eliminado de $" + gasto.getMontoGasto() + " - Responsable: " + gasto.getResponsableGasto()
                + " (Motivo: " + gasto.getMotivoGasto() + ")", GastoDTO.toDTO(gasto));

        gastoRepository.delete(gasto);
    }
}
