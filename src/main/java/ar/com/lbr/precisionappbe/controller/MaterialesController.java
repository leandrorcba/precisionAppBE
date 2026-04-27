package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.MaterialDTO;
import ar.com.lbr.precisionappbe.dto.PrecioMaterialDTO;
import ar.com.lbr.precisionappbe.dto.PrecioMaterialesDTO;
import ar.com.lbr.precisionappbe.services.MaterialesService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/materiales")
public class MaterialesController {

    private final MaterialesService materialesService;

    public MaterialesController(MaterialesService materialesService) {
        this.materialesService = materialesService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MaterialDTO>>> getAllMateriales(
            @RequestParam(required = false) String materiales,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "true") Boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MaterialDTO> result = materialesService.getAllMateriales(materiales, tipo, enabled, page, size);
        return ResponseBuilder.ok("Materiales obtenidos con éxito", result, result.getTotalElements());
    }

    @GetMapping("/solo-materiales")
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getSoloMateriales() {
        List<MaterialDTO> materiales = materialesService.getSoloMateriales();
        return ResponseBuilder.ok("Solo materiales obtenidos con éxito", materiales, (long) materiales.size());
    }

    @GetMapping("/solo-grabado")
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getSoloGrabado() {
        List<MaterialDTO> materiales = materialesService.getSoloGrabado();
        return ResponseBuilder.ok("Solo grabado obtenidos con éxito", materiales, (long) materiales.size());
    }

    @GetMapping("/precios")
    public ResponseEntity<ApiResponse<List<PrecioMaterialesDTO>>> getAllPreciosMateriales() {
        List<PrecioMaterialesDTO> precios = materialesService.getAllPreciosMateriales();
        return ResponseBuilder.ok("Precios de materiales obtenidos con éxito", precios, (long) precios.size());
    }

    @GetMapping("/calcular-precio")
    public ResponseEntity<ApiResponse<PrecioMaterialDTO>> calcularPrecio(
            @RequestParam Integer idMaterial,
            @RequestParam(required = false) Integer idSuperficie,
            @RequestParam(required = false) Integer cantidad) {
        PrecioMaterialDTO precio = materialesService.calcularPrecio(idMaterial, idSuperficie, cantidad);
        return ResponseBuilder.ok("Precio calculado con éxito", precio, 0L);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialDTO>> createMaterial(@RequestBody MaterialDTO dto) {
        MaterialDTO created = materialesService.createMaterial(dto);
        return ResponseBuilder.ok("Material creado con éxito", created, 1L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDTO>> updateMaterial(@PathVariable Integer id,
                                                                   @RequestBody MaterialDTO dto) {
        MaterialDTO updated = materialesService.updateMaterial(id, dto);
        return ResponseBuilder.ok("Material actualizado con éxito", updated, 1L);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Integer id) {
        materialesService.deleteMaterial(id);
        return ResponseBuilder.ok("Material deshabilitado con éxito", null, 0L);
    }

    @PutMapping("/{id}/rehabilitar")
    public ResponseEntity<ApiResponse<MaterialDTO>> rehabilitarMaterial(@PathVariable Integer id) {
        MaterialDTO rehabilitado = materialesService.rehabilitarMaterial(id);
        return ResponseBuilder.ok("Material rehabilitado con éxito", rehabilitado, 1L);
    }
}
