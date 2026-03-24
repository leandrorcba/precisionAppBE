package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.MaquinaDTO;
import ar.com.lbr.precisionappbe.services.MaquinasService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/maquinas")
@Tag(name = "MaquinasController")
public class MaquinasController {

    MaquinasService maquinasService;

    public MaquinasController(MaquinasService maquinasService) {
        this.maquinasService = maquinasService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaquinaDTO>>> getAllMaquinas() {
        List<MaquinaDTO> materiales = maquinasService.getAllMaquinas();
        return ResponseBuilder.ok("Materiales obtenidos con éxito", materiales, (long) materiales.size());
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<MaquinaDTO>> createMaquina(@RequestBody MaquinaDTO maquina) {
        MaquinaDTO maquinaDTO = maquinasService.createMaquina(maquina);
        return ResponseBuilder.ok("Listado obtenido con éxito", maquinaDTO, 0L);
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<MaquinaDTO>> updateMaquina(@RequestBody MaquinaDTO maquina) {
        MaquinaDTO maquinaDTO = maquinasService.updateMaquina(maquina);
        return ResponseBuilder.ok("Listado obtenido con éxito", maquinaDTO, 0L);
    }

}
