package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.SuperficieDTO;
import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.AnioCierre;
import ar.com.lbr.precisionappbe.services.UtilsService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/utils")
public class UtilsController {

    UtilsService utilsService;

    @Autowired
    public UtilsController(UtilsService utilsService) {
        this.utilsService = utilsService;
    }

    @GetMapping("/tipo-cliente")
    public ResponseEntity<ApiResponse<List<TipoClienteDTO>>> getTipoCliente() {
        List<TipoClienteDTO> tipoCliente = utilsService.getTipoCliente();
        return ResponseBuilder.ok("Listado obtenido con éxito", tipoCliente, 0L);
    }

    @GetMapping("/tipo-pago")
    public ResponseEntity<ApiResponse<List<TipoPagoDTO>>> getTipoPago() {
        List<TipoPagoDTO> tipoPago = utilsService.getTipoPago();
        return ResponseBuilder.ok("Listado obtenido con éxito", tipoPago, 0L);
    }

    @GetMapping("/anio-cierre")
    public ResponseEntity<ApiResponse<List<AnioCierre>>> getAnios() {
        List<Integer> years = utilsService.getYears();

        List<AnioCierre> result = new ArrayList<>();

        for (int i = years.size() - 1; i >= 0; i--) {
            result.add(new AnioCierre(i, years.get(i)));
        }

        return ResponseBuilder.ok("Listado obtenido con éxito", result, 0L);
    }

    @GetMapping("/superficies")
    public ResponseEntity<ApiResponse<List<SuperficieDTO>>> getSuperficies() {
        List<SuperficieDTO> superficies = utilsService.getSuperficies();
        return ResponseBuilder.ok("Listado obtenido con éxito", superficies, 0L);
    }
}
