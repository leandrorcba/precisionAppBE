package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.VentaDTO;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.Venta;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final MaterialeRepository materialeRepository;

    public VentaService(VentaRepository ventaRepository, MaterialeRepository materialeRepository) {
        this.ventaRepository = ventaRepository;
        this.materialeRepository = materialeRepository;
    }

    public List<VentaDTO> getAllVentas(LocalDate fechaFrom, LocalDate fechaTo, Boolean hoy) {
        List<Venta> ventas;

        if (Boolean.TRUE.equals(hoy)) {
            ventas = ventaRepository.findByFechaHoraVenta(LocalDate.now());
        } else if (fechaFrom != null && fechaTo != null) {
            ventas = ventaRepository.findByFechaHoraVentaBetween(fechaFrom, fechaTo);
        } else if (fechaFrom != null) {
            ventas = ventaRepository.findByFechaHoraVenta(fechaFrom);
        } else {
            ventas = ventaRepository.findAll();
        }

        return ventas.stream().map(VentaDTO::toDTO).collect(Collectors.toList());
    }

    public VentaDTO getVentaById(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return VentaDTO.toDTO(venta);
    }

    public VentaDTO createVenta(VentaDTO dto) {
        Venta venta = new Venta();
        mapDtoToEntity(dto, venta);
        return VentaDTO.toDTO(ventaRepository.save(venta));
    }

    public VentaDTO updateVenta(Integer id, VentaDTO dto) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        mapDtoToEntity(dto, venta);
        return VentaDTO.toDTO(ventaRepository.save(venta));
    }

    public void deleteVenta(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        ventaRepository.delete(venta);
    }

    private void mapDtoToEntity(VentaDTO dto, Venta venta) {
        venta.setMaterial(dto.getMaterial());
        venta.setSuperficie(dto.getSuperficie());
        venta.setPrecioMaterial(dto.getPrecioMaterial());
        venta.setCantidad(dto.getCantidad());
        venta.setPrecioVenta(dto.getPrecioVenta());

        if (dto.getIdMateriales() != null) {
            Material material = materialeRepository.findById(dto.getIdMateriales())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));
            venta.setIdMateriales(material);
        }
    }
}
