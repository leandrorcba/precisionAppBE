package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.services.PagosService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {

    private final PagosService pagosService;

    public PagosController(PagosService pagosService) {
        this.pagosService = pagosService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoDTO>>> getAllPagos(
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String medio) {
        List<PagoDTO> pagos = pagosService.getAllPagos(desde, hasta, tipo, medio);
        return ResponseBuilder.ok("Pagos obtenidos con éxito", pagos, (long) pagos.size());
    }

    @GetMapping("/presupuesto/{idPresupuesto}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> getPagosByPresupuesto(@PathVariable Integer idPresupuesto) {
        List<PagoDTO> pagos = pagosService.getPagosByPresupuesto(idPresupuesto);
        return ResponseBuilder.ok("Pagos obtenidos con éxito", pagos, (long) pagos.size());
    }

    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> getPagosByVenta(@PathVariable Integer idVenta) {
        List<PagoDTO> pagos = pagosService.getPagosByVenta(idVenta);
        return ResponseBuilder.ok("Pagos obtenidos con éxito", pagos, (long) pagos.size());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoDTO>> getPagoById(@PathVariable Integer id) {
        return ResponseBuilder.ok("Pago obtenido con éxito", pagosService.getPagoById(id), 1L);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoDTO>> createPago(@jakarta.validation.Valid @RequestBody PagoDTO dto) {
        return ResponseBuilder.ok("Pago creado con éxito", pagosService.createPago(dto), 1L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoDTO>> updatePago(@PathVariable Integer id, @jakarta.validation.Valid @RequestBody PagoDTO dto) {
        return ResponseBuilder.ok("Pago actualizado con éxito", pagosService.updatePago(id, dto), 1L);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePago(@PathVariable Integer id) {
        pagosService.deletePago(id);
        return ResponseBuilder.ok("Pago deshabilitado con éxito", null, 0L);
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<ApiResponse<PagoDTO>> anularPago(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, String> requestBody) {
        String motivo = requestBody.get("motivo");
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la anulación es requerido");
        }
        PagoDTO dto = pagosService.anularPago(id, motivo);
        return ResponseBuilder.ok("Pago anulado con éxito", dto, 1L);
    }

    @GetMapping("/auditoria-anulaciones")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ar.com.lbr.precisionappbe.model.AuditoriaAnulacionPago>>> getAuditoriaAnulaciones() {
        List<ar.com.lbr.precisionappbe.model.AuditoriaAnulacionPago> list = pagosService.getAuditoriaAnulaciones();
        return ResponseBuilder.ok("Auditoría de anulaciones obtenida con éxito", list, (long) list.size());
    }
}
