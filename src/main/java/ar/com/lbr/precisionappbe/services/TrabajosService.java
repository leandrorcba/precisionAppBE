package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrabajosService {

    private final TrabajoPresupuestadoRepository trabajosRepository;

    public TrabajosService(TrabajoPresupuestadoRepository trabajosRepository) {
        this.trabajosRepository = trabajosRepository;
    }

    public List<TrabajoPresupuestadoDTO> getTrabajosByPresupuesto(Integer idPresupuesto) {
        List<TrabajoPresupuestado> trabajos = trabajosRepository.findByIdPresupuesto(idPresupuesto);

        return trabajos.stream()
                .map(TrabajoPresupuestadoDTO::toDTO)
                .collect(Collectors.toList());
    }
}
