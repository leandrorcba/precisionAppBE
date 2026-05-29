package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.CuentaBancaria;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.PagoVenta;
import ar.com.lbr.precisionappbe.model.Tarjeta;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.Venta;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PagosService {

    private static final Set<String> TIPOS_PRESUPUESTO = Set.of("SENIA", "PRESUPUESTO");

    private final PagoPresupuestoRepository pagoPresupuestoRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final VentaRepository ventaRepository;

    public PagosService(PagoPresupuestoRepository pagoPresupuestoRepository,
                        PagoVentaRepository pagoVentaRepository,
                        TipoPagoRepository tipoPagoRepository,
                        MedioPagoRepository medioPagoRepository,
                        VentaRepository ventaRepository) {
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.tipoPagoRepository = tipoPagoRepository;
        this.medioPagoRepository = medioPagoRepository;
        this.ventaRepository = ventaRepository;
    }

    // ---------------------------------------------------------------
    // GET
    // ---------------------------------------------------------------

    public List<PagoDTO> getPagosByPresupuesto(Integer idPresupuesto) {
        return pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(idPresupuesto)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PagoDTO> getPagosByVenta(Integer idVenta) {
        return pagoVentaRepository.findByIdVenta_Id(idVenta)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PagoDTO getPagoById(Integer id) {
        return pagoPresupuestoRepository.findByIdAndEnabledTrue(id)
                .map(this::toDTO)
                .orElseGet(() -> pagoVentaRepository.findById(id)
                        .map(this::toDTO)
                        .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id)));
    }

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------

    public PagoDTO createPago(PagoDTO dto) {
        TipoPago tipoPago = resolveTipoPago(dto);
        if (TIPOS_PRESUPUESTO.contains(tipoPago.getTipo())) {
            return createPagoPresupuesto(dto, tipoPago);
        }
        return createPagoVenta(dto, tipoPago);
    }

    private PagoDTO createPagoPresupuesto(PagoDTO dto, TipoPago tipoPago) {
        PagoPresupuesto pago = new PagoPresupuesto();
        mapCommonFields(dto, tipoPago, pago);
        pago.setIdPresupuesto(dto.getIdPresupuesto());
        pago.setFechaHora(Instant.now());
        pago.setEnabled(true);
        return toDTO(pagoPresupuestoRepository.save(pago));
    }

    private PagoDTO createPagoVenta(PagoDTO dto, TipoPago tipoPago) {
        Venta venta = ventaRepository.findById(dto.getIdVenta())
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada: " + dto.getIdVenta()));
        PagoVenta pago = new PagoVenta();
        mapCommonFields(dto, tipoPago, pago);
        pago.setIdVenta(venta);
        pago.setFechaHora(Instant.now());
        return toDTO(pagoVentaRepository.save(pago));
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------

    public PagoDTO updatePago(Integer id, PagoDTO dto) {
        TipoPago tipoPago = resolveTipoPago(dto);
        if (TIPOS_PRESUPUESTO.contains(tipoPago.getTipo())) {
            return updatePagoPresupuesto(id, dto, tipoPago);
        }
        return updatePagoVenta(id, dto, tipoPago);
    }

    private PagoDTO updatePagoPresupuesto(Integer id, PagoDTO dto, TipoPago tipoPago) {
        PagoPresupuesto pago = pagoPresupuestoRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        mapCommonFields(dto, tipoPago, pago);
        pago.setIdPresupuesto(dto.getIdPresupuesto());
        return toDTO(pagoPresupuestoRepository.save(pago));
    }

    private PagoDTO updatePagoVenta(Integer id, PagoDTO dto, TipoPago tipoPago) {
        PagoVenta pago = pagoVentaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        mapCommonFields(dto, tipoPago, pago);
        return toDTO(pagoVentaRepository.save(pago));
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    public void deletePago(Integer id) {
        if (pagoPresupuestoRepository.findByIdAndEnabledTrue(id).isPresent()) {
            PagoPresupuesto pago = pagoPresupuestoRepository.findByIdAndEnabledTrue(id).get();
            pago.setEnabled(false);
            pagoPresupuestoRepository.save(pago);
        } else {
            PagoVenta pago = pagoVentaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
            pagoVentaRepository.delete(pago);
        }
    }

    // ---------------------------------------------------------------
    // Helpers privados
    // ---------------------------------------------------------------

    private TipoPago resolveTipoPago(PagoDTO dto) {
        Integer id = dto.getTipoPago() != null ? dto.getTipoPago().getId() : dto.getIdTipoPago();
        if (id == null) {
            throw new EntityNotFoundException("TipoPago no especificado");
        }
        return tipoPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoPago no encontrado: " + id));
    }

    private MedioPago resolveMedioPago(PagoDTO dto) {
        Integer id = dto.getMedioPago() != null ? dto.getMedioPago().getId() : dto.getIdMedioPago();
        if (id == null) {
            throw new EntityNotFoundException("MedioPago no especificado");
        }
        return medioPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MedioPago no encontrado: " + id));
    }

    /** Mapea los campos comunes a ambos tipos de pago. */
    private void mapCommonFields(PagoDTO dto, TipoPago tipoPago, PagoPresupuesto pago) {
        pago.setMonto(dto.getMonto());
        pago.setCuotas(dto.getCuotas());
        pago.setAutorizacion(dto.getAutorizacion());
        pago.setNotas(dto.getNotas());
        pago.setIdTipoPago(tipoPago);
        pago.setIdMedioPago(resolveMedioPago(dto));
        pago.setIdTarjeta(dto.getIdTarjeta() != null ? buildTarjeta(dto.getIdTarjeta()) : null);
        pago.setIdCuentaBancaria(dto.getIdCuentaBancaria() != null ? buildCuenta(dto.getIdCuentaBancaria()) : null);
    }

    private void mapCommonFields(PagoDTO dto, TipoPago tipoPago, PagoVenta pago) {
        pago.setMonto(dto.getMonto());
        pago.setCuotas(dto.getCuotas());
        pago.setAutorizacion(dto.getAutorizacion());
        pago.setNotas(dto.getNotas());
        pago.setIdTipoPago(tipoPago);
        pago.setIdMedioPago(resolveMedioPago(dto));
        pago.setIdTarjeta(dto.getIdTarjeta() != null ? buildTarjeta(dto.getIdTarjeta()) : null);
        pago.setIdCuentaBancaria(dto.getIdCuentaBancaria() != null ? buildCuenta(dto.getIdCuentaBancaria()) : null);
    }

    private Tarjeta buildTarjeta(Integer id) {
        Tarjeta t = new Tarjeta();
        t.setId(id);
        return t;
    }

    private CuentaBancaria buildCuenta(Integer id) {
        CuentaBancaria c = new CuentaBancaria();
        c.setId(id);
        return c;
    }

    // ---------------------------------------------------------------
    // toDTO
    // ---------------------------------------------------------------

    private PagoDTO toDTO(PagoPresupuesto p) {
        PagoDTO dto = new PagoDTO();
        dto.setId(p.getId());
        dto.setIdPresupuesto(p.getIdPresupuesto());
        dto.setMonto(p.getMonto());
        dto.setFechaHora(p.getFechaHora());
        dto.setCuotas(p.getCuotas());
        dto.setAutorizacion(p.getAutorizacion());
        dto.setNotas(p.getNotas());
        dto.setEnabled(p.getEnabled());
        dto.setTipoPago(buildTipoPagoDTO(p.getIdTipoPago()));
        dto.setMedioPago(buildMedioPagoDTO(p.getIdMedioPago()));
        if (p.getIdTarjeta() != null) {
            dto.setIdTarjeta(p.getIdTarjeta().getId());
        }
        if (p.getIdCuentaBancaria() != null) {
            dto.setIdCuentaBancaria(p.getIdCuentaBancaria().getId());
        }
        return dto;
    }

    private PagoDTO toDTO(PagoVenta p) {
        PagoDTO dto = new PagoDTO();
        dto.setId(p.getId());
        dto.setIdVenta(p.getIdVenta() != null ? p.getIdVenta().getId() : null);
        dto.setMonto(p.getMonto());
        dto.setFechaHora(p.getFechaHora());
        dto.setCuotas(p.getCuotas());
        dto.setAutorizacion(p.getAutorizacion());
        dto.setNotas(p.getNotas());
        dto.setTipoPago(buildTipoPagoDTO(p.getIdTipoPago()));
        dto.setMedioPago(buildMedioPagoDTO(p.getIdMedioPago()));
        if (p.getIdTarjeta() != null) {
            dto.setIdTarjeta(p.getIdTarjeta().getId());
        }
        if (p.getIdCuentaBancaria() != null) {
            dto.setIdCuentaBancaria(p.getIdCuentaBancaria().getId());
        }
        return dto;
    }

    private TipoPagoDTO buildTipoPagoDTO(TipoPago tipoPago) {
        if (tipoPago == null) {
            return null;
        }
        return new TipoPagoDTO(tipoPago);
    }

    private MedioPagoDTO buildMedioPagoDTO(MedioPago medioPago) {
        if (medioPago == null) {
            return null;
        }
        MedioPagoDTO dto = new MedioPagoDTO();
        dto.setId(medioPago.getId());
        dto.setTipo(medioPago.getTipo());
        dto.setDescripcion(medioPago.getDescripcion());
        return dto;
    }
}
