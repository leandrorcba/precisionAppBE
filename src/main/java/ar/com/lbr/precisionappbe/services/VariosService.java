package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.model.VariosHistorial;
import ar.com.lbr.precisionappbe.repositories.VariosHistorialRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class VariosService {

    private final VariosRepository variosRepository;
    private final VariosHistorialRepository variosHistorialRepository;

    public VariosService(VariosRepository variosRepository, VariosHistorialRepository variosHistorialRepository) {
        this.variosRepository = variosRepository;
        this.variosHistorialRepository = variosHistorialRepository;
    }

    public Varios getVarios() {
        return variosRepository.findAll().stream().findFirst().orElse(null);
    }

    public Varios updateVarios(Varios varios) {
        Varios updatedVarios = variosRepository.save(varios);

        VariosHistorial historial = new VariosHistorial();
        historial.setVarios(updatedVarios);
        historial.setPrecioMinuto(updatedVarios.getPrecioMinuto());
        historial.setHoraInicio(updatedVarios.getHoraInicio());
        historial.setHoraCierre(updatedVarios.getHoraCierre());
        historial.setAjuste(updatedVarios.getAjuste());
        historial.setPrecioMinutoEmpresa(updatedVarios.getPrecioMinutoEmpresa());
        historial.setDescuentoEfectivo(updatedVarios.getDescuentoEfectivo());
        historial.setHoraInicioFds(updatedVarios.getHoraInicioFds());
        historial.setHoraCierreFds(updatedVarios.getHoraCierreFds());

        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (principal instanceof User) {
            historial.setUser((User) principal);
        }

        variosHistorialRepository.save(historial);

        return updatedVarios;
    }
}
