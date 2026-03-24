package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import ar.com.lbr.precisionappbe.services.VariosService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/varios")
public class VariosController {

    VariosService variosService;

    @Autowired
    public VariosController(VariosService variosService) {
        this.variosService = variosService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<VariosDTO>> getVarios() {
        VariosDTO varios = variosService.getVarios();
        return ResponseBuilder.ok("Listado obtenido con éxito", varios, 0L);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<VariosDTO>> updateVarios(@RequestBody VariosDTO varios) {
        VariosDTO updatedVarios = variosService.updateVarios(varios);
        return ResponseBuilder.ok("Configuración actualizada con éxito", updatedVarios, 0L);
    }
}
