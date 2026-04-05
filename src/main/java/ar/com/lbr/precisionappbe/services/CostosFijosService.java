package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CostoFijoDTO;
import ar.com.lbr.precisionappbe.dto.CostoFijoDetalleDTO;
import ar.com.lbr.precisionappbe.dto.CostoFijoRequestDTO;
import ar.com.lbr.precisionappbe.dto.TipoCostoFijoDTO;
import ar.com.lbr.precisionappbe.model.CostoFijo;
import ar.com.lbr.precisionappbe.model.CostoFijoDetalle;
import ar.com.lbr.precisionappbe.model.TipoCostoFijo;
import ar.com.lbr.precisionappbe.repositories.CostoFijoDetalleRepository;
import ar.com.lbr.precisionappbe.repositories.CostoFijoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoCostoFijoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CostosFijosService {

    private final CostoFijoRepository costoFijoRepository;
    private final CostoFijoDetalleRepository costoFijoDetalleRepository;
    private final TipoCostoFijoRepository tipoCostoFijoRepository;

    public CostosFijosService(CostoFijoRepository costoFijoRepository,
                               CostoFijoDetalleRepository costoFijoDetalleRepository,
                               TipoCostoFijoRepository tipoCostoFijoRepository) {
        this.costoFijoRepository = costoFijoRepository;
        this.costoFijoDetalleRepository = costoFijoDetalleRepository;
        this.tipoCostoFijoRepository = tipoCostoFijoRepository;
    }

    public List<CostoFijoDTO> getAll(Integer anio) {
        int anioFinal = anio != null ? anio : LocalDate.now().getYear();
        LocalDate start = LocalDate.of(anioFinal, 1, 1);
        LocalDate end = LocalDate.of(anioFinal, 12, 31);

        Map<Month, CostoFijo> porMes = costoFijoRepository.findByPeriodoBetween(start, end)
                .stream()
                .collect(Collectors.toMap(cf -> cf.getPeriodo().getMonth(), cf -> cf));

        return Arrays.stream(Month.values())
                .map(mes -> {
                    CostoFijo cf = porMes.get(mes);
                    if (cf != null) {
                        List<CostoFijoDetalleDTO> detalles = costoFijoDetalleRepository.findByIdCostoFijo(cf)
                                .stream()
                                .map(CostoFijoDetalleDTO::toDTO)
                                .toList();
                        return CostoFijoDTO.toDTO(cf, detalles);
                    }
                    CostoFijoDTO vacio = new CostoFijoDTO();
                    vacio.setPeriodo(LocalDate.of(anioFinal, mes, 1));
                    vacio.setTotal(BigDecimal.ZERO);
                    vacio.setDetalles(List.of());
                    return vacio;
                })
                .collect(Collectors.toList());
    }

    public CostoFijoDTO getById(Integer id) {
        CostoFijo costoFijo = findOrThrow(id);
        List<CostoFijoDetalleDTO> detalles = costoFijoDetalleRepository.findByIdCostoFijo(costoFijo)
                .stream()
                .map(CostoFijoDetalleDTO::toDTO)
                .toList();
        return CostoFijoDTO.toDTO(costoFijo, detalles);
    }

    @Transactional
    public CostoFijoDTO create(CostoFijoRequestDTO request) {
        CostoFijo costoFijo = new CostoFijo();
        costoFijo.setPeriodo(request.getPeriodo());
        costoFijo.setFechaCambio(Instant.now());
        costoFijo.setTotal(BigDecimal.ZERO);
        costoFijo = costoFijoRepository.save(costoFijo);

        List<CostoFijoDetalle> detalles = buildDetalles(costoFijo, request);
        costoFijoDetalleRepository.saveAll(detalles);

        costoFijo.setTotal(calcularTotal(request));
        costoFijo = costoFijoRepository.save(costoFijo);

        List<CostoFijoDetalleDTO> detalleDTOs = detalles.stream()
                .map(CostoFijoDetalleDTO::toDTO)
                .toList();
        return CostoFijoDTO.toDTO(costoFijo, detalleDTOs);
    }

    @Transactional
    public CostoFijoDTO update(Integer id, CostoFijoRequestDTO request) {
        CostoFijo costoFijo = findOrThrow(id);
        costoFijo.setPeriodo(request.getPeriodo());
        costoFijo.setFechaCambio(Instant.now());
        costoFijo.setTotal(calcularTotal(request));

        costoFijoDetalleRepository.deleteByIdCostoFijo(costoFijo);

        List<CostoFijoDetalle> detalles = buildDetalles(costoFijo, request);
        costoFijoDetalleRepository.saveAll(detalles);
        costoFijo = costoFijoRepository.save(costoFijo);

        List<CostoFijoDetalleDTO> detalleDTOs = detalles.stream()
                .map(CostoFijoDetalleDTO::toDTO)
                .toList();
        return CostoFijoDTO.toDTO(costoFijo, detalleDTOs);
    }

    @Transactional
    public void delete(Integer id) {
        CostoFijo costoFijo = findOrThrow(id);
        costoFijoDetalleRepository.deleteByIdCostoFijo(costoFijo);
        costoFijoRepository.delete(costoFijo);
    }

    public List<TipoCostoFijoDTO> getTipos() {
        return tipoCostoFijoRepository.findByActivoTrue().stream()
                .map(TipoCostoFijoDTO::toDTO)
                .toList();
    }

    private CostoFijo findOrThrow(Integer id) {
        return costoFijoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "CostoFijo no encontrado: " + id));
    }

    private List<CostoFijoDetalle> buildDetalles(CostoFijo costoFijo, CostoFijoRequestDTO request) {
        if (request.getDetalles() == null) {
            return List.of();
        }
        return request.getDetalles().stream()
                .map(item -> {
                    TipoCostoFijo tipo = tipoCostoFijoRepository.findById(item.getIdTipoCostoFijo())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "TipoCostoFijo no encontrado: " + item.getIdTipoCostoFijo()));
                    CostoFijoDetalle detalle = new CostoFijoDetalle();
                    detalle.setIdCostoFijo(costoFijo);
                    detalle.setIdTipoCostoFijo(tipo);
                    detalle.setMonto(item.getMonto() != null ? item.getMonto() : BigDecimal.ZERO);
                    return detalle;
                })
                .toList();
    }

    private BigDecimal calcularTotal(CostoFijoRequestDTO request) {
        if (request.getDetalles() == null) {
            return BigDecimal.ZERO;
        }
        return request.getDetalles().stream()
                .filter(item -> item.getMonto() != null)
                .map(CostoFijoRequestDTO.DetalleItem::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
