package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.CuentaBancaria;
import ar.com.lbr.precisionappbe.model.Descuento;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.PagoVenta;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.Tarjeta;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.model.Venta;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.AuditoriaAnulacionPago;
import ar.com.lbr.precisionappbe.model.Cierre;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import ar.com.lbr.precisionappbe.repositories.AuditoriaAnulacionPagoRepository;
import ar.com.lbr.precisionappbe.repositories.CierreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PagosService {

    private static final Set<String> TIPOS_PRESUPUESTO = Set.of("SENIA", "PRESUPUESTO", "AJUSTE");

    private final PagoPresupuestoRepository pagoPresupuestoRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final VentaRepository ventaRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final VariosRepository variosRepository;
    private final DescuentoRepository descuentoRepository;
    private final PresupuestoService presupuestoService;
    private final AuditLogService auditLogService;
    private final ClienteRepository clienteRepository;
    private final AuditoriaAnulacionPagoRepository auditoriaAnulacionPagoRepository;
    private final CierreRepository cierreRepository;

    public PagosService(PagoPresupuestoRepository pagoPresupuestoRepository,
                        PagoVentaRepository pagoVentaRepository,
                        TipoPagoRepository tipoPagoRepository,
                        MedioPagoRepository medioPagoRepository,
                        VentaRepository ventaRepository,
                        PresupuestoRepository presupuestoRepository,
                        VariosRepository variosRepository,
                        DescuentoRepository descuentoRepository,
                        PresupuestoService presupuestoService,
                        AuditLogService auditLogService,
                        ClienteRepository clienteRepository,
                        AuditoriaAnulacionPagoRepository auditoriaAnulacionPagoRepository,
                        CierreRepository cierreRepository) {
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.tipoPagoRepository = tipoPagoRepository;
        this.medioPagoRepository = medioPagoRepository;
        this.ventaRepository = ventaRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.variosRepository = variosRepository;
        this.descuentoRepository = descuentoRepository;
        this.presupuestoService = presupuestoService;
        this.auditLogService = auditLogService;
        this.clienteRepository = clienteRepository;
        this.auditoriaAnulacionPagoRepository = auditoriaAnulacionPagoRepository;
        this.cierreRepository = cierreRepository;
    }

    // ---------------------------------------------------------------
    // GET
    // ---------------------------------------------------------------

    public List<PagoDTO> getPagosByPresupuesto(Integer idPresupuesto) {
        return pagoPresupuestoRepository.findByIdPresupuesto(idPresupuesto)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PagoDTO> getPagosByVenta(Integer idVenta) {
        return pagoVentaRepository.findByIdVenta_Id(idVenta)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PagoDTO> getAllPagos() {
        return getAllPagos(null, null, null, null);
    }

    public List<PagoDTO> getAllPagos(Instant desde, Instant hasta, String tipo, String medio) {
        Instant effectiveDe = desde != null ? desde
                : java.time.LocalDate.now().minusDays(30)
                        .atStartOfDay(java.time.ZoneId.of("America/Argentina/Buenos_Aires")).toInstant();
        Instant effectiveHasta = hasta != null ? hasta : Instant.now();

        List<PagoDTO> pagosPresupuesto = pagoPresupuestoRepository
                .findByFechaHoraBetween(effectiveDe, effectiveHasta)
                .stream().map(this::toDTO).collect(Collectors.toList());
        List<PagoDTO> pagosVenta = pagoVentaRepository
                .findByFechaHoraBetween(effectiveDe, effectiveHasta)
                .stream().map(this::toDTO).collect(Collectors.toList());

        List<PagoDTO> allPagos = new ArrayList<>();
        allPagos.addAll(pagosPresupuesto);
        allPagos.addAll(pagosVenta);

        List<PagoDTO> filteredPagos = allPagos.stream()
                .filter(p -> {
                    if (tipo != null && !tipo.isBlank() && !"todos".equalsIgnoreCase(tipo)) {
                        if (p.getTipoPago() == null || !tipo.equalsIgnoreCase(p.getTipoPago().getTipo())) {
                            return false;
                        }
                    }
                    if (medio != null && !medio.isBlank() && !"todos".equalsIgnoreCase(medio)) {
                        if (p.getMedioPago() == null) {
                            return false;
                        }
                        String mVal = p.getMedioPago().getDescripcion() != null
                                ? p.getMedioPago().getDescripcion() : p.getMedioPago().getTipo();
                        if (mVal == null || !medio.equalsIgnoreCase(mVal)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        filteredPagos.sort((p1, p2) -> {
            if (p1.getFechaHora() == null) {
                return p2.getFechaHora() == null ? 0 : 1;
            }
            if (p2.getFechaHora() == null) {
                return -1;
            }
            return p2.getFechaHora().compareTo(p1.getFechaHora());
        });

        return filteredPagos;
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

    @Transactional
    public PagoDTO createPago(PagoDTO dto) {
        TipoPago tipoPago = resolveTipoPago(dto);
        if (TIPOS_PRESUPUESTO.contains(tipoPago.getTipo())) {
            return createPagoPresupuesto(dto, tipoPago);
        }
        return createPagoVenta(dto, tipoPago);
    }

    private PagoDTO createPagoPresupuesto(PagoDTO dto, TipoPago tipoPago) {
        List<PagoPresupuesto> existing = pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(dto.getIdPresupuesto());

        BigDecimal totalPresupuesto = presupuestoRepository.findById(dto.getIdPresupuesto())
                .map(Presupuesto::getPrecioSinDescuento).orElse(BigDecimal.ZERO);


        if (tipoPago.getTipo().equalsIgnoreCase("SENIA")) {
            if (dto.getMonto().compareTo(totalPresupuesto) >= 0) {
                throw new IllegalArgumentException("La seña debe ser menor que el monto total del presupuesto");
            }
        }

        PagoPresupuesto pago = new PagoPresupuesto();
        mapCommonFields(dto, tipoPago, pago);
        pago.setIdPresupuesto(dto.getIdPresupuesto());
        pago.setFechaHora(Instant.now());
        pago.setEnabled(true);
        pago.setAnulado(false);
        pago.setUsuarioCreador(getCurrentUsername());

        if (tipoPago.getTipo().equalsIgnoreCase("PRESUPUESTO")) {
            boolean allPreviousCash = existing.stream()
                    .allMatch(p -> p.getIdTipoPago().getTipo().equalsIgnoreCase("SENIA")
                            || p.getIdMedioPago().getTipo().equalsIgnoreCase("EFECTIVO"));

            if (allPreviousCash) {
                MedioPago medioPago = resolveMedioPago(dto);
                if (medioPago.getTipo().equalsIgnoreCase("EFECTIVO")) {
                    Varios varios = variosRepository.findFirstByOrderByIdAsc();
                    if (varios != null && varios.getDescuentoEfectivo() != null && varios.getDescuentoEfectivo() > 0) {
                        BigDecimal totalAbonadoPrevio = existing.stream()
                                .map(PagoPresupuesto::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal discountPercentage = BigDecimal.valueOf(varios.getDescuentoEfectivo());
                        BigDecimal discountAmount = totalPresupuesto
                                .multiply(discountPercentage)
                                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

                        BigDecimal expectedNetRemaining = totalPresupuesto.subtract(totalAbonadoPrevio).subtract(discountAmount);

                        if (dto.getMonto().subtract(expectedNetRemaining).abs().compareTo(BigDecimal.valueOf(0.1)) < 0) {
                            Descuento discount = new Descuento();
                            discount.setIdPresupuesto(dto.getIdPresupuesto());
                            discount.setIdTipoDescuento(1); // 1 = EFECTIVO
                            discount.setMonto(discountAmount);
                            descuentoRepository.save(discount);
                        }
                    }
                }
            }
        }

        PagoPresupuesto saved = pagoPresupuestoRepository.save(pago);
        updatePresupuestoCobradoStatus(dto.getIdPresupuesto());

        PagoDTO resultDto = toDTO(saved);
        auditLogService.log("CREAR", "PAGOS", saved.getId().toString(),
                "Pago registrado para Presupuesto #" + saved.getIdPresupuesto() + " por $" + saved.getMonto()
                        + " (Tipo: " + tipoPago.getTipo() + " / Medio: " + saved.getIdMedioPago().getTipo() + ")", resultDto);

        return resultDto;
    }

    private PagoDTO createPagoVenta(PagoDTO dto, TipoPago tipoPago) {
        Venta venta = ventaRepository.findById(dto.getIdVenta())
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada: " + dto.getIdVenta()));
        PagoVenta pago = new PagoVenta();
        mapCommonFields(dto, tipoPago, pago);
        pago.setIdVenta(venta);
        pago.setFechaHora(Instant.now());
        pago.setEnabled(true);
        pago.setAnulado(false);
        pago.setUsuarioCreador(getCurrentUsername());
        PagoVenta saved = pagoVentaRepository.save(pago);

        PagoDTO resultDto = toDTO(saved);
        auditLogService.log("CREAR", "PAGOS", saved.getId().toString(),
                "Pago registrado para Venta #" + venta.getId() + " por $" + saved.getMonto()
                        + " (Medio: " + saved.getIdMedioPago().getTipo() + ")", resultDto);

        return resultDto;
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------

    @Transactional
    public PagoDTO updatePago(Integer id, PagoDTO dto) {
        TipoPago tipoPago = resolveTipoPago(dto);
        if (TIPOS_PRESUPUESTO.contains(tipoPago.getTipo())) {
            return updatePagoPresupuesto(id, dto, tipoPago);
        }
        return updatePagoVenta(id, dto, tipoPago);
    }

    private PagoDTO updatePagoPresupuesto(Integer id, PagoDTO dto, TipoPago tipoPago) {
        PagoPresupuesto pago = pagoPresupuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        mapCommonFields(dto, tipoPago, pago);
        pago.setIdPresupuesto(dto.getIdPresupuesto());
        if (dto.getEnabled() != null) {
            pago.setEnabled(dto.getEnabled());
        }

        // Rollback discount if disabling balance payment
        if (Boolean.FALSE.equals(pago.getEnabled()) && tipoPago.getTipo().equalsIgnoreCase("PRESUPUESTO")) {
            List<Descuento> discounts = descuentoRepository.findByIdPresupuesto(pago.getIdPresupuesto());
            List<Descuento> cashDiscounts = discounts.stream().filter(d -> d.getIdTipoDescuento() == 1).collect(Collectors.toList());
            if (!cashDiscounts.isEmpty()) {
                descuentoRepository.deleteAll(cashDiscounts);
            }
        }

        PagoPresupuesto saved = pagoPresupuestoRepository.save(pago);
        updatePresupuestoCobradoStatus(pago.getIdPresupuesto());

        PagoDTO resultDto = toDTO(saved);
        auditLogService.log("MODIFICAR", "PAGOS", saved.getId().toString(),
                "Pago #" + saved.getId() + " del Presupuesto #"
                        + saved.getIdPresupuesto() + " actualizado. Activo: " + saved.getEnabled(), resultDto);

        return resultDto;
    }

    private PagoDTO updatePagoVenta(Integer id, PagoDTO dto, TipoPago tipoPago) {
        PagoVenta pago = pagoVentaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        mapCommonFields(dto, tipoPago, pago);
        PagoVenta saved = pagoVentaRepository.save(pago);

        PagoDTO resultDto = toDTO(saved);
        auditLogService.log("MODIFICAR", "PAGOS", saved.getId().toString(),
                "Pago #" + saved.getId() + " de la Venta #" + saved.getIdVenta().getId() + " modificado", resultDto);

        return resultDto;
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    @Transactional
    public void deletePago(Integer id) {
        java.util.Optional<PagoPresupuesto> maybePago = pagoPresupuestoRepository.findByIdAndEnabledTrue(id);
        if (maybePago.isPresent()) {
            PagoPresupuesto pago = maybePago.get();
            pago.setEnabled(false);
            pagoPresupuestoRepository.save(pago);

            if (pago.getIdTipoPago().getTipo().equalsIgnoreCase("PRESUPUESTO")) {
                List<Descuento> discounts = descuentoRepository.findByIdPresupuesto(pago.getIdPresupuesto());
                List<Descuento> cashDiscounts = discounts.stream().filter(d -> d.getIdTipoDescuento() == 1).collect(Collectors.toList());
                if (!cashDiscounts.isEmpty()) {
                    descuentoRepository.deleteAll(cashDiscounts);
                }
            }
            updatePresupuestoCobradoStatus(pago.getIdPresupuesto());

            auditLogService.log("ELIMINAR", "PAGOS", id.toString(),
                    "Pago #" + id + " del Presupuesto #" + pago.getIdPresupuesto() + " deshabilitado", toDTO(pago));
        } else {
            PagoVenta pago = pagoVentaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
            pagoVentaRepository.delete(pago);

            auditLogService.log("ELIMINAR", "PAGOS", id.toString(),
                    "Pago #" + id + " de la Venta #" + pago.getIdVenta().getId() + " eliminado", toDTO(pago));
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public PagoDTO anularPago(Integer id, String motivo) {
        java.util.Optional<PagoPresupuesto> maybePresupuesto = pagoPresupuestoRepository.findByIdAndEnabledTrue(id);
        if (maybePresupuesto.isPresent()) {
            PagoPresupuesto pago = maybePresupuesto.get();

            Cierre ultimoCierreCerrado = cierreRepository.findFirstByCerradoTrueOrderByFechaCierreDesc().orElse(null);
            if (ultimoCierreCerrado != null && !pago.getFechaHora().isAfter(ultimoCierreCerrado.getFechaCierre())) {
                throw new IllegalArgumentException(
                        "No se puede anular un pago realizado antes o durante el último cierre de caja bloqueado. "
                                + "Por favor, registre un ajuste.");
            }

            pago.setEnabled(false);
            pago.setAnulado(true);
            pago.setMotivoAnulado(motivo);
            pago.setFechaAnulado(Instant.now());
            pago.setUsuarioAnulador(getCurrentUsername());
            PagoPresupuesto saved = pagoPresupuestoRepository.save(pago);

            // Fetch client name to log in AuditoriaAnulacionPago
            String clientName = "";
            Presupuesto pres = presupuestoRepository.findById(pago.getIdPresupuesto()).orElse(null);
            if (pres != null && pres.getIdCliente() != null) {
                clientName = clienteRepository.findById(pres.getIdCliente())
                        .map(Cliente::getNombreCliente).orElse("");
            }

            AuditoriaAnulacionPago audit = new AuditoriaAnulacionPago();
            audit.setIdPago(pago.getId());
            audit.setIdPresupuesto(pago.getIdPresupuesto());
            audit.setMonto(pago.getMonto());
            audit.setClienteNombre(clientName);
            audit.setUsuarioCreador(pago.getUsuarioCreador());
            audit.setUsuarioAnulador(pago.getUsuarioAnulador());
            audit.setFechaHoraAnulacion(Instant.now());
            audit.setMotivo(motivo);
            audit.setFechaHoraPago(pago.getFechaHora());
            audit.setTipoPago(pago.getIdTipoPago() != null ? pago.getIdTipoPago().getTipo() : "-");
            String medioDesc = "-";
            if (pago.getIdMedioPago() != null) {
                medioDesc = pago.getIdMedioPago().getDescripcion() != null
                        ? pago.getIdMedioPago().getDescripcion() : pago.getIdMedioPago().getTipo();
            }
            audit.setMedioPago(medioDesc);
            auditoriaAnulacionPagoRepository.save(audit);

            if (pago.getIdTipoPago().getTipo().equalsIgnoreCase("PRESUPUESTO")) {
                List<Descuento> discounts = descuentoRepository.findByIdPresupuesto(pago.getIdPresupuesto());
                List<Descuento> cashDiscounts = discounts.stream().filter(d -> d.getIdTipoDescuento() == 1).collect(Collectors.toList());
                if (!cashDiscounts.isEmpty()) {
                    descuentoRepository.deleteAll(cashDiscounts);
                }
            }
            updatePresupuestoCobradoStatus(pago.getIdPresupuesto());

            PagoDTO result = toDTO(saved);
            auditLogService.log("ANULAR", "PAGOS", id.toString(),
                    "Pago #" + id + " del Presupuesto #" + pago.getIdPresupuesto() + " anulado. Motivo: " + motivo, result);
            return result;
        }

        PagoVenta pagoVenta = pagoVentaRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado o ya anulado: " + id));

        Cierre ultimoCierreCerrado = cierreRepository.findFirstByCerradoTrueOrderByFechaCierreDesc().orElse(null);
        if (ultimoCierreCerrado != null && !pagoVenta.getFechaHora().isAfter(ultimoCierreCerrado.getFechaCierre())) {
            throw new IllegalArgumentException(
                    "No se puede anular un pago realizado antes o durante el último cierre de caja bloqueado. "
                            + "Por favor, registre un ajuste.");
        }

        pagoVenta.setEnabled(false);
        pagoVenta.setAnulado(true);
        pagoVenta.setMotivoAnulado(motivo);
        pagoVenta.setFechaAnulado(Instant.now());
        pagoVenta.setUsuarioAnulador(getCurrentUsername());
        PagoVenta savedVenta = pagoVentaRepository.save(pagoVenta);

        AuditoriaAnulacionPago audit = new AuditoriaAnulacionPago();
        audit.setIdPago(pagoVenta.getId());
        audit.setIdVenta(pagoVenta.getIdVenta() != null ? pagoVenta.getIdVenta().getId() : null);
        audit.setIdPresupuesto(null);
        audit.setMonto(pagoVenta.getMonto());
        audit.setClienteNombre("Venta Directa #" + (pagoVenta.getIdVenta() != null ? pagoVenta.getIdVenta().getId() : "-"));
        audit.setUsuarioCreador(pagoVenta.getUsuarioCreador());
        audit.setUsuarioAnulador(pagoVenta.getUsuarioAnulador());
        audit.setFechaHoraAnulacion(Instant.now());
        audit.setMotivo(motivo);
        audit.setFechaHoraPago(pagoVenta.getFechaHora());
        audit.setTipoPago(pagoVenta.getIdTipoPago() != null ? pagoVenta.getIdTipoPago().getTipo() : "VENTAS");
        String medioDesc = "-";
        if (pagoVenta.getIdMedioPago() != null) {
            medioDesc = pagoVenta.getIdMedioPago().getDescripcion() != null
                    ? pagoVenta.getIdMedioPago().getDescripcion() : pagoVenta.getIdMedioPago().getTipo();
        }
        audit.setMedioPago(medioDesc);
        auditoriaAnulacionPagoRepository.save(audit);

        PagoDTO result = toDTO(savedVenta);
        auditLogService.log("ANULAR", "PAGOS", id.toString(),
                "Pago #" + id + " de la Venta #" + (pagoVenta.getIdVenta() != null ? pagoVenta.getIdVenta().getId() : "-")
                        + " anulado. Motivo: " + motivo, result);
        return result;
    }

    public List<AuditoriaAnulacionPago> getAuditoriaAnulaciones() {
        return auditoriaAnulacionPagoRepository.findByOrderByFechaHoraAnulacionDesc();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    private void updatePresupuestoCobradoStatus(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto).orElse(null);
        if (presupuesto == null) {
            return;
        }

        List<PagoPresupuesto> allActivePagos = pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(idPresupuesto);

        BigDecimal totalPaid = allActivePagos.stream()
                .map(PagoPresupuesto::getMonto)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Descuento> discounts = descuentoRepository.findByIdPresupuesto(idPresupuesto);
        BigDecimal totalDiscount = discounts.stream()
                .filter(d -> d.getIdTipoDescuento() != null && d.getIdTipoDescuento() == 1) // only cash discount
                .map(Descuento::getMonto)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCovered = totalPaid.add(totalDiscount);
        BigDecimal totalPresupuesto = presupuesto.getPrecioSinDescuento() != null ? presupuesto.getPrecioSinDescuento() : BigDecimal.ZERO;

        BigDecimal difference = totalPresupuesto.subtract(totalCovered);

        // If remaining balance is <= 0.1 (accounting for rounding/float differences)
        if (difference.compareTo(BigDecimal.valueOf(0.1)) <= 0) {
            presupuesto.setCobrado(true);
            if (presupuesto.getFechaCobrado() == null) {
                presupuesto.setFechaCobrado(java.time.Instant.now());
            }
        } else {
            presupuesto.setCobrado(false);
            presupuesto.setFechaCobrado(null);
        }
        presupuestoRepository.save(presupuesto);
        presupuestoService.actualizarPdfFisico(idPresupuesto);
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

    /**
     * Mapea los campos comunes a ambos tipos de pago.
     */
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
        dto.setAnulado(p.getAnulado());
        dto.setMotivoAnulado(p.getMotivoAnulado());
        dto.setFechaAnulado(p.getFechaAnulado());
        dto.setUsuarioCreador(p.getUsuarioCreador());
        dto.setUsuarioAnulador(p.getUsuarioAnulador());
        dto.setAnulable(isPagoAnulable(p.getFechaHora(), p.getAnulado(), p.getEnabled()));
        dto.setTipoPago(buildTipoPagoDTO(p.getIdTipoPago()));
        dto.setMedioPago(buildMedioPagoDTO(p.getIdMedioPago()));
        if (p.getIdTarjeta() != null) {
            dto.setIdTarjeta(p.getIdTarjeta().getId());
            dto.setTarjetaNombre(p.getIdTarjeta().getNombre());
        }
        if (p.getIdCuentaBancaria() != null) {
            dto.setIdCuentaBancaria(p.getIdCuentaBancaria().getId());
            dto.setCuentaBancariaNombre(p.getIdCuentaBancaria().getBanco() + " (" + p.getIdCuentaBancaria().getNumeroCuenta() + ")");
        }
        if (p.getIdPresupuesto() != null) {
            presupuestoRepository.findById(p.getIdPresupuesto()).ifPresent(presupuesto -> {
                if (presupuesto.getIdCliente() != null) {
                    clienteRepository.findById(presupuesto.getIdCliente()).ifPresent(cliente -> {
                        dto.setClienteNombre(cliente.getNombreCliente());
                    });
                }
            });
        }
        return dto;
    }

    private boolean isPagoAnulable(Instant fechaHora, Boolean anulado, Boolean enabled) {
        if (Boolean.TRUE.equals(anulado) || !Boolean.TRUE.equals(enabled)) {
            return false;
        }
        Cierre ultimoCierreCerrado = cierreRepository.findFirstByCerradoTrueOrderByFechaCierreDesc().orElse(null);
        if (ultimoCierreCerrado == null) {
            return true;
        }
        return fechaHora.isAfter(ultimoCierreCerrado.getFechaCierre());
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
        dto.setEnabled(p.getEnabled() != null ? p.getEnabled() : true);
        dto.setAnulado(Boolean.TRUE.equals(p.getAnulado()));
        dto.setMotivoAnulado(p.getMotivoAnulado());
        dto.setFechaAnulado(p.getFechaAnulado());
        dto.setUsuarioCreador(p.getUsuarioCreador());
        dto.setUsuarioAnulador(p.getUsuarioAnulador());
        dto.setAnulable(isPagoAnulable(p.getFechaHora(), p.getAnulado(), p.getEnabled()));
        dto.setClienteNombre("Venta Directa #" + (p.getIdVenta() != null ? p.getIdVenta().getId() : "-"));
        dto.setTipoPago(buildTipoPagoDTO(p.getIdTipoPago()));
        dto.setMedioPago(buildMedioPagoDTO(p.getIdMedioPago()));
        if (p.getIdTarjeta() != null) {
            dto.setIdTarjeta(p.getIdTarjeta().getId());
            dto.setTarjetaNombre(p.getIdTarjeta().getNombre());
        }
        if (p.getIdCuentaBancaria() != null) {
            dto.setIdCuentaBancaria(p.getIdCuentaBancaria().getId());
            dto.setCuentaBancariaNombre(p.getIdCuentaBancaria().getBanco() + " (" + p.getIdCuentaBancaria().getNumeroCuenta() + ")");
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
