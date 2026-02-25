package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.services.TrabajosService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class TrabajosController {

    private final TrabajosService trabajosService;

    public TrabajosController(TrabajosService trabajosService) {
        this.trabajosService = trabajosService;
    }

    @GetMapping("/{id}/trabajos")
    public ResponseEntity<ApiResponse<List<TrabajoPresupuestadoDTO>>> getTrabajosByPresupuesto(
            @PathVariable Integer id) {
        List<TrabajoPresupuestadoDTO> trabajoDTOs = trabajosService.getTrabajosByPresupuesto(id);

        return ResponseBuilder.ok("Trabajos obtenidos con éxito", trabajoDTOs, (long) trabajoDTOs.size());
    }
}
