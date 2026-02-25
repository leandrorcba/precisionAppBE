package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MaterialDTO;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialesService {

    private final MaterialeRepository materialeRepository;

    public MaterialesService(MaterialeRepository materialeRepository) {
        this.materialeRepository = materialeRepository;
    }

    public List<MaterialDTO> getAllMateriales() {
        return materialeRepository.findAll().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<MaterialDTO> getSoloMateriales() {
        return materialeRepository.findByIsMaterialTrue().stream()
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
}
