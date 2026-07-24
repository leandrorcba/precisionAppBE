package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.services.PagosService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

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
        try {
            return ResponseBuilder.ok("Pago obtenido con éxito", pagosService.getPagoById(id), 1L);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoDTO>> createPago(@RequestBody PagoDTO dto) {
        try {
            return ResponseBuilder.ok("Pago creado con éxito", pagosService.createPago(dto), 1L);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoDTO>> updatePago(@PathVariable Integer id, @RequestBody PagoDTO dto) {
        try {
            return ResponseBuilder.ok("Pago actualizado con éxito", pagosService.updatePago(id, dto), 1L);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePago(@PathVariable Integer id) {
        try {
            pagosService.deletePago(id);
            return ResponseBuilder.ok("Pago deshabilitado con éxito", null, 0L);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
