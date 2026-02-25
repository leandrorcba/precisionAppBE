package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.MaquinaDTO;
import ar.com.lbr.precisionappbe.model.AnioCierre;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.Varios;
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
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/utils")
public class UtilsController {

    UtilsService utilsService;

    @Autowired
    public UtilsController(UtilsService utilsService) {
        this.utilsService = utilsService;
    }

    @GetMapping("/tipo-cliente")
    public ResponseEntity<ApiResponse<List<TipoCliente>>> getTipoCliente() {
        List<TipoCliente> tipoCliente = utilsService.getTipoCliente();
        return ResponseBuilder.ok("Listado obtenido con éxito", tipoCliente, 0L);
    }

    @GetMapping("/tipo-pago")
    public ResponseEntity<ApiResponse<List<TipoPago>>> getTipoPago() {
        List<TipoPago> tipoPago = utilsService.getTipoPago();
        return ResponseBuilder.ok("Listado obtenido con éxito", tipoPago, 0L);
    }

    @GetMapping("/varios")
    public ResponseEntity<ApiResponse<Varios>> getVarios() {
        Varios varios = utilsService.getVarios();
        return ResponseBuilder.ok("Listado obtenido con éxito", varios, 0L);
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
}
