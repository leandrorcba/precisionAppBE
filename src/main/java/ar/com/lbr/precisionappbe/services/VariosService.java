package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.model.VariosHistorial;
import ar.com.lbr.precisionappbe.repositories.VariosHistorialRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.com.lbr.precisionappbe.services.AuditLogService;

@Service
@Transactional(readOnly = true)
public class VariosService {

    private final VariosRepository variosRepository;
    private final VariosHistorialRepository variosHistorialRepository;
    private final AuditLogService auditLogService;

    public VariosService(VariosRepository variosRepository,
                         VariosHistorialRepository variosHistorialRepository,
                         AuditLogService auditLogService) {
        this.variosRepository = variosRepository;
        this.variosHistorialRepository = variosHistorialRepository;
        this.auditLogService = auditLogService;
    }

    public VariosDTO getVarios() {
        Varios varios = variosRepository.findAll().stream().findFirst().orElse(null);
        return VariosDTO.toDTO(varios);
    }

    @Transactional
    public VariosDTO updateVarios(VariosDTO dto) {
        Varios varios = variosRepository.findAll().stream().findFirst().orElse(new Varios());
        varios.setPrecioMinuto(dto.getPrecioMinuto());
        varios.setHoraInicio(dto.getHoraInicio());
        varios.setHoraCierre(dto.getHoraCierre());
        varios.setAjuste(dto.getAjuste());
        varios.setPrecioMinutoEmpresa(dto.getPrecioMinutoEmpresa());
        varios.setDescuentoEfectivo(dto.getDescuentoEfectivo());
        varios.setHoraInicioFds(dto.getHoraInicioFds());
        varios.setHoraCierreFds(dto.getHoraCierreFds());
        varios.setMinutosPorPunto(dto.getMinutosPorPunto());
        varios.setDescuentoPorPunto(dto.getDescuentoPorPunto());
        varios.setDirectorioRaizCarpetas(dto.getDirectorioRaizCarpetas());
        varios.setPermitirTrabajosFds(dto.getPermitirTrabajosFds() != null ? dto.getPermitirTrabajosFds() : false);
        varios.setDescuentoEstudiante(dto.getDescuentoEstudiante());
        varios.setHabilitarCarpetas(dto.getHabilitarCarpetas() != null ? dto.getHabilitarCarpetas() : true);

        Varios updatedVarios = variosRepository.save(varios);

        auditLogService.log("CONFIGURAR", "PARAMETROS", updatedVarios.getId().toString(),
                "Parámetros globales del local actualizados (Precio Minuto: $" + updatedVarios.getPrecioMinuto()
                + ", Ajuste: " + updatedVarios.getAjuste() + "%, Descuento Efectivo: " + updatedVarios.getDescuentoEfectivo() + "%)");

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
        historial.setMinutosPorPunto(updatedVarios.getMinutosPorPunto());
        historial.setDescuentoPorPunto(updatedVarios.getDescuentoPorPunto());
        historial.setPermitirTrabajosFds(updatedVarios.getPermitirTrabajosFds());
        historial.setDescuentoEstudiante(updatedVarios.getDescuentoEstudiante());
        historial.setHabilitarCarpetas(updatedVarios.getHabilitarCarpetas());

        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (principal instanceof User) {
            historial.setUser((User) principal);
        }

        variosHistorialRepository.save(historial);

        return VariosDTO.toDTO(updatedVarios);
    }
}
