package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.VentaDTO;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.PagoVenta;
import ar.com.lbr.precisionappbe.model.Venta;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.com.lbr.precisionappbe.services.AuditLogService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final VentaRepository ventaRepository;
    private final MaterialeRepository materialeRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final AuditLogService auditLogService;

    public VentaService(VentaRepository ventaRepository,
                        MaterialeRepository materialeRepository,
                        PagoVentaRepository pagoVentaRepository,
                        AuditLogService auditLogService) {
        this.ventaRepository = ventaRepository;
        this.materialeRepository = materialeRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.auditLogService = auditLogService;
    }

    public List<VentaDTO> getAllVentas(LocalDate fechaFrom, LocalDate fechaTo, Boolean hoy) {
        List<Venta> ventas;

        if (Boolean.TRUE.equals(hoy)) {
            Instant start = LocalDate.now().atStartOfDay(ZONE).toInstant();
            Instant end = LocalDate.now().atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            ventas = ventaRepository.findByFechaHoraVentaBetween(start, end);
        } else if (fechaFrom != null && fechaTo != null) {
            Instant start = fechaFrom.atStartOfDay(ZONE).toInstant();
            Instant end = fechaTo.atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            ventas = ventaRepository.findByFechaHoraVentaBetween(start, end);
        } else if (fechaFrom != null) {
            Instant start = fechaFrom.atStartOfDay(ZONE).toInstant();
            Instant end = fechaFrom.atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            ventas = ventaRepository.findByFechaHoraVentaBetween(start, end);
        } else {
            Instant start = LocalDate.now().minusDays(30).atStartOfDay(ZONE).toInstant();
            Instant end = LocalDate.now().atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            ventas = ventaRepository.findByFechaHoraVentaBetween(start, end);
        }

        List<Integer> idsVentas = ventas.stream().map(Venta::getId).collect(Collectors.toList());
        Map<Integer, BigDecimal> pagosMap = getMontoAbonadoPorVentas(idsVentas);

        return ventas.stream().map(v -> {
            VentaDTO dto = VentaDTO.toDTO(v);
            if (dto != null) {
                dto.setMontoAbonado(pagosMap.getOrDefault(v.getId(), BigDecimal.ZERO));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private Map<Integer, BigDecimal> getMontoAbonadoPorVentas(List<Integer> idsVentas) {
        if (idsVentas == null || idsVentas.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<PagoVenta> pagos = pagoVentaRepository.findByIdVenta_IdInAndEnabledTrue(idsVentas);
        return pagos.stream().collect(Collectors.groupingBy(
            p -> p.getIdVenta().getId(),
            Collectors.reducing(BigDecimal.ZERO, PagoVenta::getMonto, BigDecimal::add)
        ));
    }

    public VentaDTO getVentaById(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        VentaDTO dto = VentaDTO.toDTO(venta);
        if (dto != null) {
            BigDecimal montoAbonado = pagoVentaRepository.findByIdVenta_IdAndEnabledTrue(id).stream()
                    .map(PagoVenta::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setMontoAbonado(montoAbonado);
        }
        return dto;
    }

    public VentaDTO createVenta(VentaDTO dto) {
        Venta venta = new Venta();
        mapDtoToEntity(dto, venta);
        Venta saved = ventaRepository.save(venta);
        VentaDTO savedDto = VentaDTO.toDTO(saved);
        if (savedDto != null) {
            savedDto.setMontoAbonado(BigDecimal.ZERO);
        }

        auditLogService.log("CREAR", "VENTAS", saved.getId().toString(),
                "Venta directa de material #" + saved.getId() + " registrada por $" + saved.getPrecioVenta()
                + " (Material: " + (saved.getIdMateriales() != null ? saved.getIdMateriales().getMateriales() : dto.getIdMateriales())
                + ", Cantidad: " + saved.getCantidad() + ")");

        return savedDto;
    }

    public VentaDTO updateVenta(Integer id, VentaDTO dto) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        BigDecimal montoAbonado = pagoVentaRepository.findByIdVenta_IdAndEnabledTrue(id).stream()
                .map(PagoVenta::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (montoAbonado.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("No se puede editar una venta con pagos registrados");
        }

        mapDtoToEntity(dto, venta);
        Venta saved = ventaRepository.save(venta);
        VentaDTO savedDto = VentaDTO.toDTO(saved);
        if (savedDto != null) {
            savedDto.setMontoAbonado(montoAbonado);
        }

        auditLogService.log("MODIFICAR", "VENTAS", saved.getId().toString(),
                "Venta directa de material #" + saved.getId() + " modificada (Monto: $" + saved.getPrecioVenta() + ")");

        return savedDto;
    }

    @Transactional
    public void deleteVenta(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Delete associated payments first
        List<PagoVenta> pagos = pagoVentaRepository.findByIdVenta_Id(id);
        if (pagos != null && !pagos.isEmpty()) {
            pagoVentaRepository.deleteAll(pagos);
            for (PagoVenta p : pagos) {
                auditLogService.log("ELIMINAR", "PAGOS", p.getId().toString(),
                        "Pago #" + p.getId() + " de la Venta #" + id
                        + " deshabilitado por eliminación de la venta");
            }
        }

        auditLogService.log("ELIMINAR", "VENTAS", id.toString(),
                "Venta directa de material #" + id + " eliminada (Monto: $" + venta.getPrecioVenta()
                + ", Material: " + (venta.getIdMateriales() != null ? venta.getIdMateriales().getMateriales() : "Desconocido") + ")");

        ventaRepository.delete(venta);
    }

    private void mapDtoToEntity(VentaDTO dto, Venta venta) {
        venta.setPrecioMaterial(dto.getPrecioMaterial());
        venta.setCantidad(dto.getCantidad());
        venta.setPrecioVenta(dto.getPrecioVenta());
        venta.setSuperficie(dto.getSuperficie());

        if (dto.getFechaVenta() != null) {
            LocalDate date = dto.getFechaVenta();
            LocalTime time = dto.getHoraVenta() != null ? dto.getHoraVenta() : LocalTime.now(ZONE);
            venta.setFechaHoraVenta(date.atTime(time).atZone(ZONE).toInstant());
        } else {
            venta.setFechaHoraVenta(Instant.now());
        }

        if (dto.getIdMateriales() != null) {
            Material material = materialeRepository.findById(dto.getIdMateriales())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado"));
            venta.setIdMateriales(material);
        }
    }
}
