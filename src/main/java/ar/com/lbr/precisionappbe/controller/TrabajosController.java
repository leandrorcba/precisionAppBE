package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.services.TrabajosService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/trabajos")
public class TrabajosController {

    private final TrabajosService trabajosService;

    public TrabajosController(TrabajosService trabajosService) {
        this.trabajosService = trabajosService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TrabajoPresupuestadoDTO>> createTrabajo(
            @RequestBody TrabajoPresupuestadoDTO dto) {
        TrabajoPresupuestadoDTO created = trabajosService.createTrabajo(dto);
        return ResponseBuilder.ok("Trabajo creado con éxito", created, 0L);
    }

    @PatchMapping("/{idTrabajo}/seleccion_presupuesto")
    public ResponseEntity<ApiResponse<TrabajoPresupuestadoDTO>> updateSeleccionado(
            @PathVariable Integer idTrabajo,
            @RequestParam("new_value") Boolean newValue) {
        TrabajoPresupuestadoDTO updated = trabajosService.updateSeleccionado(idTrabajo, newValue);
        return ResponseBuilder.ok("Seleccionado actualizado con éxito", updated, 0L);
    }

    @PatchMapping("/{idPresupuesto}/confirmar_presupuesto")
    public ResponseEntity<ApiResponse<Void>> confirmarPresupuesto(
            @PathVariable Integer idPresupuesto) {
        trabajosService.confirmarPresupuesto(idPresupuesto);
        return ResponseBuilder.ok("Presupuesto confirmado con éxito", null, 0L);
    }

    @GetMapping("/{idPresupuesto}")
    public ResponseEntity<ApiResponse<List<TrabajoPresupuestadoDTO>>> getTrabajosByPresupuesto(
            @PathVariable Integer idPresupuesto) {
        List<TrabajoPresupuestadoDTO> trabajoDTOs = trabajosService.getTrabajosByPresupuesto(idPresupuesto);

        return ResponseBuilder.ok("Trabajos obtenidos con éxito", trabajoDTOs, (long) trabajoDTOs.size());
    }

}
