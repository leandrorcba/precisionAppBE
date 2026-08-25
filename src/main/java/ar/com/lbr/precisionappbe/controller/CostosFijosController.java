package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.CostoFijoDTO;
import ar.com.lbr.precisionappbe.dto.CostoFijoRequestDTO;
import ar.com.lbr.precisionappbe.dto.TipoCostoFijoDTO;
import ar.com.lbr.precisionappbe.services.CostosFijosService;
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

import java.util.List;

@RestController
@RequestMapping("/api/costos-fijos")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class CostosFijosController {

    private final CostosFijosService costosFijosService;

    public CostosFijosController(CostosFijosService costosFijosService) {
        this.costosFijosService = costosFijosService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CostoFijoDTO>>> getAll(
            @RequestParam(required = false) Integer anio) {
        List<CostoFijoDTO> lista = costosFijosService.getAll(anio);
        return ResponseBuilder.ok("Listado obtenido con éxito", lista, (long) lista.size());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CostoFijoDTO>> getById(@PathVariable Integer id) {
        return ResponseBuilder.ok("CostoFijo obtenido con éxito", costosFijosService.getById(id), 0L);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CostoFijoDTO>> create(@RequestBody CostoFijoRequestDTO request) {
        return ResponseBuilder.ok("CostoFijo creado con éxito", costosFijosService.create(request), 0L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CostoFijoDTO>> update(
            @PathVariable Integer id,
            @RequestBody CostoFijoRequestDTO request) {
        return ResponseBuilder.ok("CostoFijo actualizado con éxito", costosFijosService.update(id, request), 0L);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        costosFijosService.delete(id);
        return ResponseBuilder.ok("CostoFijo eliminado con éxito", null, 0L);
    }

    @GetMapping("/tipos")
    public ResponseEntity<ApiResponse<List<TipoCostoFijoDTO>>> getTipos(
            @RequestParam(required = false) Boolean soloActivos) {
        List<TipoCostoFijoDTO> tipos = costosFijosService.getTipos(soloActivos);
        return ResponseBuilder.ok("Tipos obtenidos con éxito", tipos, (long) tipos.size());
    }

    @PostMapping("/tipos")
    public ResponseEntity<ApiResponse<TipoCostoFijoDTO>> createTipo(@RequestBody TipoCostoFijoDTO dto) {
        TipoCostoFijoDTO created = costosFijosService.createTipo(dto);
        return ResponseBuilder.ok("Tipo de Costo Fijo creado con éxito", created, 0L);
    }

    @PutMapping("/tipos/{id}")
    public ResponseEntity<ApiResponse<TipoCostoFijoDTO>> updateTipo(
            @PathVariable Integer id,
            @RequestBody TipoCostoFijoDTO dto) {
        TipoCostoFijoDTO updated = costosFijosService.updateTipo(id, dto);
        return ResponseBuilder.ok("Tipo de Costo Fijo actualizado con éxito", updated, 0L);
    }

    @DeleteMapping("/tipos/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTipo(@PathVariable Integer id) {
        costosFijosService.deleteTipo(id);
        return ResponseBuilder.ok("Tipo de Costo Fijo inactivado con éxito", null, 0L);
    }
}
