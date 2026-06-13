package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CompraMaterialesDTO;
import ar.com.lbr.precisionappbe.model.CompraMateriale;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.CompraMaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompraMaterialesService {

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final CompraMaterialeRepository compraMaterialeRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public CompraMaterialesService(CompraMaterialeRepository compraMaterialeRepository,
                                   UserRepository userRepository,
                                   AuditLogService auditLogService) {
        this.compraMaterialeRepository = compraMaterialeRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public List<CompraMaterialesDTO> getAllCompras(LocalDate fechaFrom, LocalDate fechaTo, Boolean hoy) {
        List<CompraMateriale> compras;

        if (Boolean.TRUE.equals(hoy)) {
            Instant start = LocalDate.now().atStartOfDay(ZONE).toInstant();
            Instant end = LocalDate.now().atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            compras = compraMaterialeRepository.findByFechaHoraCompraBetween(start, end);
        } else if (fechaFrom != null && fechaTo != null) {
            Instant start = fechaFrom.atStartOfDay(ZONE).toInstant();
            Instant end = fechaTo.atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            compras = compraMaterialeRepository.findByFechaHoraCompraBetween(start, end);
        } else if (fechaFrom != null) {
            Instant start = fechaFrom.atStartOfDay(ZONE).toInstant();
            Instant end = fechaFrom.atTime(LocalTime.MAX).atZone(ZONE).toInstant();
            compras = compraMaterialeRepository.findByFechaHoraCompraBetween(start, end);
        } else {
            compras = compraMaterialeRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHoraCompra"));
        }

        Map<Integer, String> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getUsername, (u1, u2) -> u1));

        return compras.stream()
                .sorted((a, b) -> {
                    if (a.getFechaHoraCompra() == null && b.getFechaHoraCompra() == null) {
                        return 0;
                    }
                    if (a.getFechaHoraCompra() == null) {
                        return 1;
                    }
                    if (b.getFechaHoraCompra() == null) {
                        return -1;
                    }
                    return b.getFechaHoraCompra().compareTo(a.getFechaHoraCompra());
                })
                .map(c -> {
                    CompraMaterialesDTO dto = CompraMaterialesDTO.toDTO(c);
                    if (dto != null && c.getIdUser() != null) {
                        dto.setUsername(userMap.getOrDefault(c.getIdUser(), "Desconocido"));
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    public CompraMaterialesDTO createCompra(CompraMaterialesDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        CompraMateriale compra = new CompraMateriale();
        compra.setMaterial(dto.getMaterial());
        compra.setMontoUnitario(dto.getMontoUnitario());
        compra.setCantidad(dto.getCantidad());

        // Calcular total: montoUnitario * cantidad
        if (dto.getMontoUnitario() != null && dto.getCantidad() != null) {
            compra.setMontoTotal(dto.getMontoUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));
        } else {
            compra.setMontoTotal(BigDecimal.ZERO);
        }

        compra.setFechaHoraCompra(Instant.now());
        compra.setIdUser(user.getId());

        // Columnas ignoradas en esta etapa
        compra.setIdMateriales(null);
        compra.setTipo(null);
        compra.setCaja(null);

        CompraMateriale saved = compraMaterialeRepository.save(compra);
        CompraMaterialesDTO savedDto = CompraMaterialesDTO.toDTO(saved);
        if (savedDto != null) {
            savedDto.setUsername(user.getUsername());
        }

        auditLogService.log("CREAR", "COMPRAS", saved.getId().toString(),
                "Compra de material '" + saved.getMaterial() + "' registrada por un total de $" + saved.getMontoTotal()
                        + " (Cantidad: " + saved.getCantidad() + ", Unitario: $" + saved.getMontoUnitario() + ")");

        return savedDto;
    }

    public void deleteCompra(Integer id) {
        CompraMateriale compra = compraMaterialeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra de material no encontrada con ID: " + id));

        auditLogService.log("ELIMINAR", "COMPRAS", id.toString(),
                "Compra de material #" + id + " ('" + compra.getMaterial() + "', Total: $" + compra.getMontoTotal() + ") eliminada");

        compraMaterialeRepository.delete(compra);
    }
}
