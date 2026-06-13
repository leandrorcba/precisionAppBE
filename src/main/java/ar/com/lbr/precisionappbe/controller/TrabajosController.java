package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.services.RemitoPdfService;
import ar.com.lbr.precisionappbe.services.TrabajosService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trabajos")
public class TrabajosController {

    private final TrabajosService trabajosService;
    private final RemitoPdfService remitoPdfService;

    public TrabajosController(TrabajosService trabajosService, RemitoPdfService remitoPdfService) {
        this.trabajosService = trabajosService;
        this.remitoPdfService = remitoPdfService;
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
        try {
            TrabajoPresupuestadoDTO updated = trabajosService.updateSeleccionado(idTrabajo, newValue);
            return ResponseBuilder.ok("Seleccionado actualizado con éxito", updated, 0L);
        } catch (IllegalArgumentException e) {
            return org.springframework.http.ResponseEntity.badRequest()
                    .body(new ar.com.lbr.precisionappbe.util.ApiResponse<>(false, e.getMessage(), null, 0L));
        }
    }

    @PatchMapping("/{idTrabajo}/estado")
    public ResponseEntity<ApiResponse<TrabajoPresupuestadoDTO>> updateEstado(
            @PathVariable Integer idTrabajo,
            @RequestParam("nuevo_estado") EstadoTrabajo nuevoEstado) {
        TrabajoPresupuestadoDTO updated = trabajosService.updateEstado(idTrabajo, nuevoEstado);
        return ResponseBuilder.ok("Estado actualizado con éxito", updated, 0L);
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

    @GetMapping("/{idPresupuesto}/remito")
    public ResponseEntity<byte[]> getRemito(@PathVariable Integer idPresupuesto) {
        byte[] pdf = remitoPdfService.generateRemito(idPresupuesto);
        String filename = "remito-" + idPresupuesto + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

}
