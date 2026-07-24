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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MaterialesService {

    private final MaterialeRepository materialeRepository;
    private final PrecioMaterialRepository precioMaterialRepository;

    public MaterialesService(MaterialeRepository materialeRepository, PrecioMaterialRepository precioMaterialRepository) {
        this.materialeRepository = materialeRepository;
        this.precioMaterialRepository = precioMaterialRepository;
    }

    public Page<MaterialDTO> getAllMateriales(String materiales, String tipo, Boolean enabled, int page, int size) {
        boolean disabled = Boolean.FALSE.equals(enabled);
        boolean hasFilter = materiales != null && !materiales.isBlank();
        boolean esCorte = "corte".equalsIgnoreCase(tipo);
        boolean esGrabado = "grabado".equalsIgnoreCase(tipo);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("materiales").ascending());

        Page<Material> result;
        if (esCorte) {
            result = hasFilter
                    ? (disabled
                        ? materialeRepository.findByMaterialesContainingIgnoreCaseAndIsMaterialTrueAndDisabledTrue(materiales, pageable)
                        : materialeRepository.findByMaterialesContainingIgnoreCaseAndIsMaterialTrueAndDisabledFalse(materiales, pageable))
                    : (disabled
                        ? materialeRepository.findByIsMaterialTrueAndDisabledTrue(pageable)
                        : materialeRepository.findByIsMaterialTrueAndDisabledFalse(pageable));
        } else if (esGrabado) {
            result = hasFilter
                    ? (disabled
                        ? materialeRepository.findByMaterialesContainingIgnoreCaseAndIsGrabadoTrueAndDisabledTrue(materiales, pageable)
                        : materialeRepository.findByMaterialesContainingIgnoreCaseAndIsGrabadoTrueAndDisabledFalse(materiales, pageable))
                    : (disabled
                        ? materialeRepository.findByIsGrabadoTrueAndDisabledTrue(pageable)
                        : materialeRepository.findByIsGrabadoTrueAndDisabledFalse(pageable));
        } else {
            result = hasFilter
                    ? (disabled
                        ? materialeRepository.findByMaterialesContainingIgnoreCaseAndDisabledTrue(materiales, pageable)
                        : materialeRepository.findByMaterialesContainingIgnoreCaseAndDisabledFalse(materiales, pageable))
                    : (disabled
                        ? materialeRepository.findByDisabledTrue(pageable)
                        : materialeRepository.findByDisabledFalse(pageable));
        }
        Page<MaterialDTO> dtos = result.map(MaterialDTO::toDTO);
        populatePrices(dtos.getContent());
        return dtos;
    }

    public List<MaterialDTO> getSoloMateriales() {
        List<MaterialDTO> dtos = materialeRepository.findByIsMaterialTrueAndDisabledFalseOrderByMaterialesAsc().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
        populatePrices(dtos);
        return dtos;
    }

    public List<MaterialDTO> getSoloGrabado() {
        List<MaterialDTO> dtos = materialeRepository.findByIsGrabadoTrueAndDisabledFalseOrderByMaterialesAsc().stream()
                .map(MaterialDTO::toDTO)
                .collect(Collectors.toList());
        populatePrices(dtos);
        return dtos;
    }

    public MaterialDTO createMaterial(MaterialDTO dto) {
        Material material = new Material();
        material.setMateriales(dto.getMateriales());
        material.setIsMaterial(dto.getIsMaterial() != null ? dto.getIsMaterial() : false);
        material.setIsGrabado(dto.getIsGrabado() != null ? dto.getIsGrabado() : false);
        material.setStock(dto.getStock() != null ? dto.getStock() : java.math.BigDecimal.ZERO);
        material.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : java.math.BigDecimal.ZERO);
        material.setDisabled(dto.getDisabled() != null ? dto.getDisabled() : false);

        Material saved = materialeRepository.save(material);
        saveOrUpdatePrices(saved, dto);

        MaterialDTO result = MaterialDTO.toDTO(saved);
        populatePrices(List.of(result));
        return result;
    }

    public MaterialDTO updateMaterial(Integer id, MaterialDTO dto) {
        Material material = materialeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        material.setMateriales(dto.getMateriales());
        material.setIsMaterial(dto.getIsMaterial() != null ? dto.getIsMaterial() : false);
        material.setIsGrabado(dto.getIsGrabado() != null ? dto.getIsGrabado() : false);
        material.setStock(dto.getStock() != null ? dto.getStock() : java.math.BigDecimal.ZERO);
        material.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : java.math.BigDecimal.ZERO);
        material.setDisabled(dto.getDisabled() != null ? dto.getDisabled() : false);

        Material updated = materialeRepository.save(material);
        saveOrUpdatePrices(updated, dto);

        MaterialDTO result = MaterialDTO.toDTO(updated);
        populatePrices(List.of(result));
        return result;
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
        MaterialDTO result = MaterialDTO.toDTO(materialeRepository.save(material));
        populatePrices(List.of(result));
        return result;
    }

    private void populatePrices(List<MaterialDTO> dtos) {
        if (dtos.isEmpty()) return;
        List<Integer> ids = dtos.stream().map(MaterialDTO::getId).collect(Collectors.toList());
        List<PrecioMateriale> prices = precioMaterialRepository.findByIdMaterialesIn(ids);
        Map<Integer, List<PrecioMateriale>> pricesByMaterial = prices.stream()
                .collect(Collectors.groupingBy(PrecioMateriale::getIdMateriales));

        for (MaterialDTO dto : dtos) {
            List<PrecioMateriale> materialPrices = pricesByMaterial.get(dto.getId());
            if (materialPrices != null) {
                for (PrecioMateriale pm : materialPrices) {
                    if (pm.getIdSuperficie() == null) {
                        dto.setPrecioPorUnidad(pm.getPrecioMaterial());
                        dto.setUnidades(pm.getUnidades());
                    } else {
                        if (pm.getIdSuperficie() == 4) {
                            dto.setPrecioSup1(pm.getPrecioMaterial());
                        } else if (pm.getIdSuperficie() == 3) {
                            dto.setPrecioSup3_4(pm.getPrecioMaterial());
                        } else if (pm.getIdSuperficie() == 2) {
                            dto.setPrecioSup1_2(pm.getPrecioMaterial());
                        } else if (pm.getIdSuperficie() == 1) {
                            dto.setPrecioSup1_4(pm.getPrecioMaterial());
                        }
                    }
                }
            }
        }
    }

    private void saveOrUpdatePrices(Material material, MaterialDTO dto) {
        if (Boolean.TRUE.equals(material.getIsMaterial())) {
            // Material de corte: eliminar precio sin superficie (unidad)
            List<PrecioMateriale> existing = precioMaterialRepository.findByIdMaterialesIn(List.of(material.getId()));
            List<PrecioMateriale> toDelete = existing.stream()
                    .filter(pm -> pm.getIdSuperficie() == null)
                    .collect(Collectors.toList());
            if (!toDelete.isEmpty()) {
                precioMaterialRepository.deleteAll(toDelete);
            }

            // Guardar/actualizar precios por superficie
            saveOrUpdateSurfacePrice(material.getId(), 4, dto.getPrecioSup1());
            saveOrUpdateSurfacePrice(material.getId(), 3, dto.getPrecioSup3_4());
            saveOrUpdateSurfacePrice(material.getId(), 2, dto.getPrecioSup1_2());
            saveOrUpdateSurfacePrice(material.getId(), 1, dto.getPrecioSup1_4());
        } else {
            // Material no de corte: eliminar precios por superficie
            List<PrecioMateriale> existing = precioMaterialRepository.findByIdMaterialesIn(List.of(material.getId()));
            List<PrecioMateriale> toDelete = existing.stream()
                    .filter(pm -> pm.getIdSuperficie() != null)
                    .collect(Collectors.toList());
            if (!toDelete.isEmpty()) {
                precioMaterialRepository.deleteAll(toDelete);
            }

            // Guardar/actualizar precio por unidad (unidades por defecto = 1)
            Short units = dto.getUnidades() != null ? dto.getUnidades() : (short) 1;
            BigDecimal price = dto.getPrecioPorUnidad() != null ? dto.getPrecioPorUnidad() : BigDecimal.ZERO;

            PrecioMateriale pm = existing.stream()
                    .filter(p -> p.getIdSuperficie() == null)
                    .findFirst()
                    .orElse(new PrecioMateriale());
            pm.setIdMateriales(material.getId());
            pm.setIdSuperficie(null);
            pm.setUnidades(units);
            pm.setPrecioMaterial(price);
            precioMaterialRepository.save(pm);
        }
    }

    private void saveOrUpdateSurfacePrice(Integer idMaterial, Integer idSuperficie, BigDecimal price) {
        BigDecimal finalPrice = price != null ? price : BigDecimal.ZERO;
        PrecioMateriale pm = precioMaterialRepository.findByIdMaterialesAndIdSuperficie(idMaterial, idSuperficie);
        if (pm == null) {
            pm = new PrecioMateriale();
            pm.setIdMateriales(idMaterial);
            pm.setIdSuperficie(idSuperficie);
            pm.setUnidades(null);
        }
        pm.setPrecioMaterial(finalPrice);
        precioMaterialRepository.save(pm);
    }


    public List<PrecioMaterialesDTO> getAllPreciosMateriales() {
        return precioMaterialRepository.findAllWithNombreMaterial().stream()
                .map(row -> PrecioMaterialesDTO.toDTO((PrecioMateriale) row[0], (String) row[1]))
                .collect(Collectors.toList());
    }

    public PrecioMaterialesDTO updatePrecioMaterial(Integer id, PrecioMaterialDTO dto) {
        PrecioMateriale precioMateriale = precioMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Precio de material no encontrado con id: " + id));
        precioMateriale.setPrecioMaterial(dto.getPrecio());
        PrecioMateriale saved = precioMaterialRepository.save(precioMateriale);
        Material material = materialeRepository.findById(saved.getIdMateriales()).orElse(null);
        String nombreMaterial = material != null ? material.getMateriales() : null;
        return PrecioMaterialesDTO.toDTO(saved, nombreMaterial);
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
