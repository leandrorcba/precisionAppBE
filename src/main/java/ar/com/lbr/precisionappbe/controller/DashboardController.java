package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.DashboardDTO;
import ar.com.lbr.precisionappbe.services.DashboardService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardDTO>> getStats(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "origen", required = false, defaultValue = "ACTIVO") String origen) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        DashboardDTO stats = dashboardService.getStats(year, origen);
        return ResponseBuilder.ok("Estadísticas obtenidas con éxito", stats, 0L);
    }
}
