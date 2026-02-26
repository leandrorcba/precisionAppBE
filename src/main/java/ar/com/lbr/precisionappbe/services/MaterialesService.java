package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MaterialDTO;
import ar.com.lbr.precisionappbe.dto.PrecioMaterialDTO;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.PrecioMateriale;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PrecioMaterialRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialesService {

    private final MaterialeRepository materialeRepository;
    private final PrecioMaterialRepository precioMaterialRepository;

    public MaterialesService(MaterialeRepository materialeRepository, PrecioMaterialRepository precioMaterialRepository) {
        this.materialeRepository = materialeRepository;
        this.precioMaterialRepository = precioMaterialRepository;
    }

    public List<MaterialDTO> getAllMateriales() {
        return materialeRepository.findAll().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<MaterialDTO> getSoloMateriales() {
        return materialeRepository.findByIsMaterialTrueOrderByMaterialesAsc().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
    }

    public MaterialDTO createMaterial(MaterialDTO dto) {
        Material material = new Material();
        material.setMateriales(dto.getMateriales());
        material.setIsMaterial(dto.getIsMaterial());

        Material saved = materialeRepository.save(material);
        return MaterialDTO.toDTO(saved);
    }

    public MaterialDTO updateMaterial(Integer id, MaterialDTO dto) {
        Material material = materialeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        material.setMateriales(dto.getMateriales());
        material.setIsMaterial(dto.getIsMaterial());

        Material updated = materialeRepository.save(material);
        return MaterialDTO.toDTO(updated);
    }

    public void deleteMaterial(Integer id) {
        Material material = materialeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        materialeRepository.delete(material);
    }

    public PrecioMaterialDTO calcularPrecio(Integer idMaterial, Integer idSuperficie) {
        BigDecimal precio = precioMaterialRepository.findByIdMaterialesAndIdSuperficie(idMaterial, idSuperficie).getPrecioMaterial();
        return new PrecioMaterialDTO(precio);
    }
}
