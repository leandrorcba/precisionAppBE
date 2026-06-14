package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.AuditLog;
import ar.com.lbr.precisionappbe.repositories.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private interface HibernateMixIn {
    }

    public AuditLogService(AuditLogRepository auditLogRepository,
                           @Autowired(required = false) ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        ObjectMapper mapper = objectMapper != null ? objectMapper : new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.addMixIn(Object.class, HibernateMixIn.class);
        this.objectMapper = mapper;
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
        log(accion, modulo, registroId, detalles, null);
    }

    @Transactional
    public void log(String accion, String modulo, String registroId, String detalles, Object payload) {
        AuditLog entry = new AuditLog();
        entry.setFechaHora(Instant.now());
        entry.setUsuario(getCurrentUsername());
        entry.setAccion(accion);
        entry.setModulo(modulo);
        entry.setRegistroId(registroId);
        entry.setDetalles(detalles);

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                entry.setUri(request.getRequestURI());
            }
        } catch (Exception e) {
            log.debug("No web context available for audit URI capture: {}", e.getMessage());
        }

        if (payload != null) {
            try {
                entry.setJson(objectMapper.writeValueAsString(payload));
            } catch (Exception e) {
                entry.setJson("{\"error\": \"No se pudo serializar el payload: " + e.getMessage() + "\"}");
            }
        }

        auditLogRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsFiltered(Instant desde, Instant hasta, String modulo, String usuario,
                                          String accion, String query, Pageable pageable) {
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
