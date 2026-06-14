package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.CierreDTO;
import ar.com.lbr.precisionappbe.dto.ReporteDiarioDTO;
import ar.com.lbr.precisionappbe.dto.response.CierreResponse;
import ar.com.lbr.precisionappbe.services.CierreService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cierres")
public class CierresController {

    private final CierreService cierreService;

    public CierresController(CierreService cierreService) {
        this.cierreService = cierreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CierreDTO>>> getCierres(
            @RequestParam(required = false) String mesCierre,
            @RequestParam(required = false) String fechaCierre,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaCierre"));

        CierreResponse response = cierreService.getCierresFiltered(mesCierre, fechaCierre, pageable);

        return ResponseBuilder.ok("Cierres obtenidos con éxito", response.getCierres(), response.getTotal());
    }

    @GetMapping("/mes")
    public ResponseEntity<ApiResponse<List<CierreDTO>>> getCierresByMes(
            @RequestParam String mesCierre,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaCierre"));

        CierreResponse response = cierreService.getCierresFiltered(mesCierre, null, pageable);

        return ResponseBuilder.ok("Cierres del mes obtenidos con éxito", response.getCierres(), response.getTotal());
    }

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<CierreDTO>> createCierre(
            @RequestBody CierreDTO dto,
            Principal principal) {
        CierreDTO created = cierreService.createCierre(dto, principal.getName());
        return ResponseBuilder.ok("Cierre inicializado con éxito", created, 1L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CierreDTO>> updateCierre(
            @PathVariable Integer id,
            @RequestBody CierreDTO dto) {
        CierreDTO updated = cierreService.updateCierre(id, dto);
        return ResponseBuilder.ok("Cierre actualizado con éxito", updated, 1L);
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<ApiResponse<CierreDTO>> cerrarCierre(@PathVariable Integer id) {
        CierreDTO closed = cierreService.cerrarCierre(id);
        return ResponseBuilder.ok("Cierre de caja bloqueado con éxito", closed, 1L);
    }

    @GetMapping("/reporte-diario")
    public ResponseEntity<ApiResponse<ReporteDiarioDTO>> getReporteDiario(@RequestParam String fecha) {
        ReporteDiarioDTO report = cierreService.getReporteDiario(fecha);
        return ResponseBuilder.ok("Reporte diario obtenido con éxito", report, 1L);
    }
}
