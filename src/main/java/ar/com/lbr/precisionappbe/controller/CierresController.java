package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.CierreDTO;
import ar.com.lbr.precisionappbe.dto.response.CierreResponse;
import ar.com.lbr.precisionappbe.services.CierreService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cierres")
public class CierresController {

    private final CierreService cierreService;

    public CierresController(CierreService cierreService) {
        this.cierreService = cierreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CierreDTO>>> getCierres(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaCierre"));

        CierreResponse response = cierreService.getAllCierres(pageable);

        return ResponseBuilder.ok("Cierres obtenidos con éxito", response.getCierres(), response.getTotal());
    }

    @GetMapping("/mes")
    public ResponseEntity<ApiResponse<List<CierreDTO>>> getCierresByMes(
            @RequestParam String mesCierre,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int limit) {

        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaCierre"));

        CierreResponse response = cierreService.getAllCierresByMesCierre(mesCierre, pageable);

        return ResponseBuilder.ok("Cierres del mes obtenidos con éxito", response.getCierres(), response.getTotal());
    }
}
