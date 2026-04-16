package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MaterialDTO;
import ar.com.lbr.precisionappbe.dto.PrecioMaterialDTO;
import ar.com.lbr.precisionappbe.dto.PrecioMaterialesDTO;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.PrecioMateriale;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PrecioMaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public Page<MaterialDTO> getAllMateriales(String materiales, Boolean enabled, int page, int size) {
        boolean disabled = Boolean.FALSE.equals(enabled);
        boolean hasFilter = materiales != null && !materiales.isBlank();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("materiales").ascending());

        Page<Material> result;
        if (hasFilter) {
            result = disabled
                    ? materialeRepository.findByMaterialesContainingIgnoreCaseAndDisabledTrue(materiales, pageable)
                    : materialeRepository.findByMaterialesContainingIgnoreCaseAndDisabledFalse(materiales, pageable);
        } else {
            result = disabled
                    ? materialeRepository.findByDisabledTrue(pageable)
                    : materialeRepository.findByDisabledFalse(pageable);
        }
        return result.map(MaterialDTO::toDTO);
    }

    public List<MaterialDTO> getSoloMateriales() {
        return materialeRepository.findByIsMaterialTrueAndDisabledFalseOrderByMaterialesAsc().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<MaterialDTO> getSoloGrabado() {
        return materialeRepository.findByIsGrabadoTrueAndDisabledFalseOrderByMaterialesAsc().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
    }

    public MaterialDTO createMaterial(MaterialDTO dto) {
        Material material = new Material();
        material.setMateriales(dto.getMateriales());
        material.setIsMaterial(dto.getIsMaterial());
        material.setIsMaterial(dto.getIsGrabado());

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
        material.setDisabled(true);
        materialeRepository.save(material);
    }

    public MaterialDTO rehabilitarMaterial(Integer id) {
        Material material = materialeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));
        material.setDisabled(false);
        return MaterialDTO.toDTO(materialeRepository.save(material));
    }


    public List<PrecioMaterialesDTO> getAllPreciosMateriales() {
        return precioMaterialRepository.findAllWithNombreMaterial().stream()
                .map(row -> PrecioMaterialesDTO.toDTO((PrecioMateriale) row[0], (String) row[1]))
                .collect(Collectors.toList());
    }

    public PrecioMaterialDTO calcularPrecio(Integer idMaterial, Integer idSuperficie, Integer cantidad) {
        BigDecimal precio;
        if (cantidad != null) {
            PrecioMateriale precioMateriale = precioMaterialRepository.findFirstByIdMateriales(idMaterial);
            if (precioMateriale == null) {
                throw new RuntimeException("No se encontró precio para el material " + idMaterial);
            }
            BigDecimal escala = BigDecimal.valueOf(precioMateriale.getUnidades());
            BigDecimal precioEscala = precioMateriale.getPrecioMaterial();
            precio = BigDecimal.valueOf(cantidad)
                    .divide(escala, 10, RoundingMode.HALF_UP)
                    .multiply(precioEscala)
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            PrecioMateriale precioMateriale = precioMaterialRepository.findByIdMaterialesAndIdSuperficie(idMaterial, idSuperficie);
            if (precioMateriale == null) {
                throw new RuntimeException("No se encontró precio para el material " + idMaterial + " con superficie " + idSuperficie);
            }
            precio = precioMateriale.getPrecioMaterial();
        }
        return new PrecioMaterialDTO(precio);
    }


}
