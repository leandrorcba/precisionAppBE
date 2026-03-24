package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import java.math.BigDecimal;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrabajosService {

    private final TrabajoPresupuestadoRepository trabajosRepository;
    private final PresupuestoRepository presupuestoRepository;

    public TrabajosService(TrabajoPresupuestadoRepository trabajosRepository,
                           PresupuestoRepository presupuestoRepository) {
        this.trabajosRepository = trabajosRepository;
        this.presupuestoRepository = presupuestoRepository;
    }

    public TrabajoPresupuestadoDTO createTrabajo(TrabajoPresupuestadoDTO dto) {
        TrabajoPresupuestado entity = new TrabajoPresupuestado();
        entity.setIdPresupuesto(dto.getIdPresupuesto());
        entity.setSeleccionado(dto.getSeleccionado() != null ? dto.getSeleccionado() : false);
        entity.setArchivoCad(dto.getArchivoCad());
        entity.setArchivoOriginal(dto.getArchivoOriginal());
        entity.setMaterial(dto.getMaterial());
        entity.setNotas(dto.getNotas());
        entity.setTiempoDeCorte(dto.getTiempoDeCorte() != null ? dto.getTiempoDeCorte() : 0);
        entity.setIdMateriales(dto.getIdMateriales());
        entity.setPrecioMaterial(dto.getPrecioMaterial() != null ? dto.getPrecioMaterial() : BigDecimal.ZERO);
        entity.setPrecioTrabajo(dto.getPrecioTrabajo() != null ? dto.getPrecioTrabajo() : BigDecimal.ZERO);
        entity.setPrecioCorte(dto.getPrecioCorte() != null ? dto.getPrecioCorte() : BigDecimal.ZERO);
        entity.setVinilo(dto.getVinilo() != null ? dto.getVinilo() : BigDecimal.ZERO);
        entity.setExtra(dto.getExtra() != null ? dto.getExtra() : BigDecimal.ZERO);
        entity.setVectorizado(dto.getVectorizado() != null ? dto.getVectorizado() : BigDecimal.ZERO);
        entity.setPrecioMinuto(dto.getPrecioMinuto() != null ? dto.getPrecioMinuto() : BigDecimal.ZERO);
        entity.setDescuento(dto.getDescuento());
        entity.setIdSuperficie(dto.getIdSuperficie());

        TrabajoPresupuestado saved = trabajosRepository.save(entity);
        return TrabajoPresupuestadoDTO.toDTO(saved);
    }

    public TrabajoPresupuestadoDTO updateSeleccionado(Integer idTrabajo, Boolean newValue) {
        TrabajoPresupuestado entity = trabajosRepository.findById(idTrabajo)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado: " + idTrabajo));
        entity.setSeleccionado(newValue);
        return TrabajoPresupuestadoDTO.toDTO(trabajosRepository.save(entity));
    }

    public void confirmarPresupuesto(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));
        presupuesto.setAprobado(true);
        presupuestoRepository.save(presupuesto);
    }

    public List<TrabajoPresupuestadoDTO> getTrabajosByPresupuesto(Integer idPresupuesto) {
        List<TrabajoPresupuestado> trabajos = trabajosRepository.findByIdPresupuesto(idPresupuesto);

        return trabajos.stream()
                .map(TrabajoPresupuestadoDTO::toDTO)
                .collect(Collectors.toList());
    }
}
