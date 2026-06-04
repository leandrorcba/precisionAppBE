package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.GastoDTO;
import ar.com.lbr.precisionappbe.services.GastoService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GastoDTO>>> getAllGastos() {
        List<GastoDTO> gastos = gastoService.getAllGastos();
        return ResponseBuilder.ok("Gastos obtenidos con éxito", gastos, (long) gastos.size());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GastoDTO>> createGasto(@RequestBody GastoDTO dto) {
        GastoDTO created = gastoService.createGasto(dto);
        return ResponseBuilder.ok("Gasto creado con éxito", created, 1L);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGasto(@PathVariable Integer id) {
        gastoService.deleteGasto(id);
        return ResponseBuilder.ok("Gasto eliminado con éxito", null, 0L);
    }
}
