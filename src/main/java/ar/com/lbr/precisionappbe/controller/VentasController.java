package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.VentaDTO;
import ar.com.lbr.precisionappbe.services.VentaService;
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
@RequestMapping("/api/ventas")
public class VentasController {

    private final VentaService ventaService;

    public VentasController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VentaDTO>>> getAllVentas(
            @RequestParam(required = false) LocalDate fechaFrom,
            @RequestParam(required = false) LocalDate fechaTo,
            @RequestParam(required = false) Boolean hoy) {
        List<VentaDTO> ventas = ventaService.getAllVentas(fechaFrom, fechaTo, hoy);
        return ResponseBuilder.ok("Ventas obtenidas con éxito", ventas, (long) ventas.size());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaDTO>> getVentaById(@PathVariable Integer id) {
        VentaDTO venta = ventaService.getVentaById(id);
        return ResponseBuilder.ok("Venta obtenida con éxito", venta, 1L);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VentaDTO>> createVenta(@RequestBody VentaDTO dto) {
        VentaDTO created = ventaService.createVenta(dto);
        return ResponseBuilder.ok("Venta creada con éxito", created, 1L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaDTO>> updateVenta(@PathVariable Integer id,
            @RequestBody VentaDTO dto) {
        VentaDTO updated = ventaService.updateVenta(id, dto);
        return ResponseBuilder.ok("Venta actualizada con éxito", updated, 1L);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVenta(@PathVariable Integer id) {
        ventaService.deleteVenta(id);
        return ResponseBuilder.ok("Venta eliminada con éxito", null, 0L);
    }
}
