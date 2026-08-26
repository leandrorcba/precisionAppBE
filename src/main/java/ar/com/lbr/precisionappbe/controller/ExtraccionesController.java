package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.ExtraccionDTO;
import ar.com.lbr.precisionappbe.services.ExtraccionService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/extracciones")
public class ExtraccionesController {

    private final ExtraccionService extraccionService;

    public ExtraccionesController(ExtraccionService extraccionService) {
        this.extraccionService = extraccionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExtraccionDTO>>> getAllExtracciones(
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta) {
        List<ExtraccionDTO> extracciones = extraccionService.getAllExtracciones(fechaDesde, fechaHasta);
        return ResponseBuilder.ok("Extracciones obtenidas con éxito", extracciones, (long) extracciones.size());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExtraccionDTO>> createExtraccion(@jakarta.validation.Valid @RequestBody ExtraccionDTO dto) {
        ExtraccionDTO created = extraccionService.createExtraccion(dto);
        return ResponseBuilder.ok("Extracción creada con éxito", created, 1L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExtraccionDTO>> updateExtraccion(@PathVariable Integer id,
            @jakarta.validation.Valid @RequestBody ExtraccionDTO dto) {
        ExtraccionDTO updated = extraccionService.updateExtraccion(id, dto);
        return ResponseBuilder.ok("Extracción actualizada con éxito", updated, 1L);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExtraccion(@PathVariable Integer id) {
        extraccionService.deleteExtraccion(id);
        return ResponseBuilder.ok("Extracción eliminada con éxito", null, 0L);
    }
}
