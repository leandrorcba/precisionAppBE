package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.CompraMaterialesDTO;
import ar.com.lbr.precisionappbe.services.CompraMaterialesService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compra-materiales")
public class CompraMaterialesController {

    private final CompraMaterialesService compraMaterialesService;

    public CompraMaterialesController(CompraMaterialesService compraMaterialesService) {
        this.compraMaterialesService = compraMaterialesService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompraMaterialesDTO>>> getAllCompras(
            @RequestParam(required = false) LocalDate fechaFrom,
            @RequestParam(required = false) LocalDate fechaTo,
            @RequestParam(required = false) Boolean hoy) {
        List<CompraMaterialesDTO> compras = compraMaterialesService.getAllCompras(fechaFrom, fechaTo, hoy);
        return ResponseBuilder.ok("Compras de materiales obtenidas con éxito", compras, (long) compras.size());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompraMaterialesDTO>> createCompra(
            @jakarta.validation.Valid @RequestBody CompraMaterialesDTO dto,
            Principal principal) {
        try {
            CompraMaterialesDTO created = compraMaterialesService.createCompra(dto, principal.getName());
            return ResponseBuilder.ok("Compra de material registrada con éxito", created, 1L);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompra(@PathVariable Integer id) {
        try {
            compraMaterialesService.deleteCompra(id);
            return ResponseBuilder.ok("Compra de material eliminada con éxito", null, 0L);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
