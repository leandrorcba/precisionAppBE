package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.MaterialDTO;
import ar.com.lbr.precisionappbe.services.MaterialesService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getAllMateriales() {
        List<MaterialDTO> materiales = materialesService.getAllMateriales();
        return ResponseBuilder.ok("Materiales obtenidos con éxito", materiales, (long) materiales.size());
    }

    @GetMapping("/solo-materiales")
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getSoloMateriales() {
        List<MaterialDTO> materiales = materialesService.getSoloMateriales();
        return ResponseBuilder.ok("Solo materiales obtenidos con éxito", materiales, (long) materiales.size());
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
        return ResponseBuilder.ok("Material eliminado con éxito", null, 0L);
    }
}
