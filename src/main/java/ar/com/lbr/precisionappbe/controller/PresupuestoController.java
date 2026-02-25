package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.PresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.response.PresupuestoResponse;
import ar.com.lbr.precisionappbe.services.PresupuestoService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<ApiResponse<List<PresupuestoDTO>>> getPresupuestosByClienteId(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);

        PresupuestoResponse presupuestoResponse = presupuestoService.buscarPresupuestoByIdCliente(id, pageable);

        return ResponseBuilder.ok("Listado obtenido con éxito", presupuestoResponse.getPresupuestos(),
                presupuestoResponse.getTotal());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<PresupuestoDTO>>> getPresupuestoById(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);

        PresupuestoResponse presupuestoResponse = presupuestoService.buscarPresupuestoByIdPresupuesto(id, pageable);

        return ResponseBuilder.ok("Listado obtenido con éxito", presupuestoResponse.getPresupuestos(),
                presupuestoResponse.getTotal());
    }

    /*
     * @GetMapping("/tipo-clientes")
     * public ResponseEntity<ApiResponse<List<TipoCliente>>> getTipoCliente() {
     * List<TipoCliente> tipoCliente = presupuestoService.getTipoCliente();
     * return ResponseBuilder.ok("Listado obtenido con éxito", tipoCliente, 0L);
     * }
     */

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PresupuestoDTO>> createPresupuesto(@RequestBody PresupuestoDTO presupuesto) {
        PresupuestoDTO presupuestoDTO = presupuestoService.createPresupuesto(presupuesto);
        return ResponseBuilder.ok("Listado obtenido con éxito", presupuestoDTO, 0L);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<PresupuestoDTO>> updatePresupuesto(@RequestBody PresupuestoDTO presupuesto) {
        PresupuestoDTO presupuestoDTO = presupuestoService.updateCliente(presupuesto);
        return ResponseBuilder.ok("Listado obtenido con éxito", presupuestoDTO, 0L);
    }

}
