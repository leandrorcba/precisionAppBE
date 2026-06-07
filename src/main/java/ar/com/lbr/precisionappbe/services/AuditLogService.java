package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.AuditLog;
import ar.com.lbr.precisionappbe.repositories.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    @Transactional
    public void log(String accion, String modulo, String registroId, String detalles) {
        AuditLog log = new AuditLog();
        log.setFechaHora(Instant.now());
        log.setUsuario(getCurrentUsername());
        log.setAccion(accion);
        log.setModulo(modulo);
        log.setRegistroId(registroId);
        log.setDetalles(detalles);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsFiltered(Instant desde, Instant hasta, String modulo, String usuario, String accion, String query, Pageable pageable) {
        Specification<AuditLog> spec = Specification.where((root, q, cb) -> cb.conjunction());

        if (desde != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("fechaHora"), desde));
        }
        if (hasta != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("fechaHora"), hasta));
        }
        if (modulo != null && !modulo.trim().isEmpty() && !"todos".equalsIgnoreCase(modulo)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("modulo"), modulo.trim().toUpperCase()));
        }
        if (usuario != null && !usuario.trim().isEmpty() && !"todos".equalsIgnoreCase(usuario)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("usuario"), usuario.trim()));
        }
        if (accion != null && !accion.trim().isEmpty() && !"todos".equalsIgnoreCase(accion)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("accion"), cb.upper(cb.literal(accion.trim()))));
        }
        if (query != null && !query.trim().isEmpty()) {
            String searchPattern = "%" + query.trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("detalles")), searchPattern),
                cb.like(cb.lower(root.get("registroId")), searchPattern)
            ));
        }

        return auditLogRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getUniqueUsers() {
        return auditLogRepository.findDistinctUsuarios();
    }
}
