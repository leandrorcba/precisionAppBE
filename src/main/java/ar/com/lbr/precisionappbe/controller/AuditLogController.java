package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.AuditLogDTO;
import ar.com.lbr.precisionappbe.model.AuditLog;
import ar.com.lbr.precisionappbe.services.AuditLogService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auditoria")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogDTO>>> getLogs(
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaHora"));

        Page<AuditLog> logsPage = auditLogService.getLogsFiltered(desde, hasta, modulo, usuario, accion, query, pageable);
        List<AuditLogDTO> dtos = logsPage.getContent().stream()
                .map(AuditLogDTO::toDTO)
                .collect(Collectors.toList());

        return ResponseBuilder.ok("Logs de auditoría obtenidos con éxito", dtos, logsPage.getTotalElements());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<List<String>>> getUsuarios() {
        List<String> usuarios = auditLogService.getUniqueUsers();
        return ResponseBuilder.ok("Usuarios de auditoría obtenidos con éxito", usuarios, (long) usuarios.size());
    }
}
