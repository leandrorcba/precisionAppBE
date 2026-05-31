package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.CuentaBancariaDTO;
import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.MercadoPagoDTO;
import ar.com.lbr.precisionappbe.dto.SuperficieDTO;
import ar.com.lbr.precisionappbe.dto.TarjetaDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

    @GetMapping("/medio-pago")
    public ResponseEntity<ApiResponse<List<MedioPagoDTO>>> getMediosPago() {
        List<MedioPagoDTO> mediosPago = utilsService.getMediosPago();
        return ResponseBuilder.ok("Listado obtenido con éxito", mediosPago, 0L);
    }

    @GetMapping("/minuto_empresa")
    public ResponseEntity<ApiResponse<BigDecimal>> getPrecioMinutoEmpresa() {
        BigDecimal precioMinutoEmpresa = utilsService.getPrecioMinutoEmpresa();
        return ResponseBuilder.ok("Listado obtenido con éxito", precioMinutoEmpresa, 0L);
    }

    @GetMapping("/cuentas-bancarias")
    public ResponseEntity<ApiResponse<List<CuentaBancariaDTO>>> getCuentasBancarias() {
        List<CuentaBancariaDTO> cuentas = utilsService.getCuentasBancarias();
        return ResponseBuilder.ok("Cuentas bancarias obtenidas con éxito", cuentas, (long) cuentas.size());
    }

    @GetMapping("/tarjetas")
    public ResponseEntity<ApiResponse<List<TarjetaDTO>>> getTarjetas() {
        List<TarjetaDTO> tarjetas = utilsService.getTarjetas();
        return ResponseBuilder.ok("Tarjetas obtenidas con éxito", tarjetas, (long) tarjetas.size());
    }

    @GetMapping("/cuentas-mp")
    public ResponseEntity<ApiResponse<List<MercadoPagoDTO>>> getMercadoPagos() {
        List<MercadoPagoDTO> mps = utilsService.getMercadoPagos();
        return ResponseBuilder.ok("MercadoPago obtenido con éxito", mps, (long) mps.size());
    }

    @GetMapping("/browse-directories")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> browseDirectories(
            @RequestParam(required = false) String path) {

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.List<String> directories = new java.util.ArrayList<>();

        if (path == null || path.trim().isEmpty()) {
            java.io.File[] roots = java.io.File.listRoots();
            if (roots != null) {
                for (java.io.File root : roots) {
                    directories.add(root.getAbsolutePath());
                }
            }
            result.put("currentPath", "");
            result.put("parentPath", null);
        } else {
            java.io.File currentDir = new java.io.File(path);
            if (currentDir.exists() && currentDir.isDirectory()) {
                java.io.File[] files = currentDir.listFiles();
                if (files != null) {
                    for (java.io.File file : files) {
                        if (file.isDirectory() && !file.isHidden()) {
                            directories.add(file.getName());
                        }
                    }
                }
                result.put("currentPath", currentDir.getAbsolutePath());
                result.put("parentPath", currentDir.getParent() != null ? currentDir.getParent() : "");
            } else {
                return ResponseBuilder.error("El directorio no existe o no es válido",
                        org.springframework.http.HttpStatus.BAD_REQUEST);
            }
        }

        directories.sort(String.CASE_INSENSITIVE_ORDER);
        result.put("directories", directories);

        return ResponseBuilder.ok("Directorios obtenidos con éxito", result, (long) directories.size());
    }
}
