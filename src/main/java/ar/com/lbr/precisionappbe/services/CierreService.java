package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CierreDTO;
import ar.com.lbr.precisionappbe.dto.MovimientoReporteDTO;
import ar.com.lbr.precisionappbe.dto.ReporteDiarioDTO;
import ar.com.lbr.precisionappbe.model.*;
import ar.com.lbr.precisionappbe.repositories.*;
import ar.com.lbr.precisionappbe.dto.response.CierreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CierreService {

    private final CierreRepository cierreRepository;
    private final PagoPresupuestoRepository pagoPresupuestoRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final ExtraccionRepository extraccionRepository;
    private final CompraMaterialeRepository compraMaterialeRepository;
    private final GastoRepository gastoRepository;
    private final UserRepository userRepository;
    private final DescuentoRepository descuentoRepository;

    private static final ZoneId ZONE_ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");

    public CierreService(CierreRepository cierreRepository,
                         PagoPresupuestoRepository pagoPresupuestoRepository,
                         PagoVentaRepository pagoVentaRepository,
                         ExtraccionRepository extraccionRepository,
                         CompraMaterialeRepository compraMaterialeRepository,
                         GastoRepository gastoRepository,
                         UserRepository userRepository,
                         DescuentoRepository descuentoRepository) {
        this.cierreRepository = cierreRepository;
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.extraccionRepository = extraccionRepository;
        this.compraMaterialeRepository = compraMaterialeRepository;
        this.gastoRepository = gastoRepository;
        this.userRepository = userRepository;
        this.descuentoRepository = descuentoRepository;
    }

    public CierreResponse getAllCierres(Pageable pageable) {
        Page<Cierre> cierresPage = cierreRepository.findAll(pageable);
        List<CierreDTO> cierreDTOs = cierresPage.getContent().stream()
                .map(c -> {
                    if (!Boolean.TRUE.equals(c.getCerrado())) {
                        calculateAndFillCierre(c);
                        cierreRepository.save(c);
                    }
                    return CierreDTO.toDTO(c);
                })
                .collect(Collectors.toList());
        return new CierreResponse(cierreDTOs, cierresPage.getTotalElements());
    }

    public CierreResponse getAllCierresByMesCierre(String mesCierre, Pageable pageable) {
        Page<Cierre> cierresPage = cierreRepository.findByMesCierre(mesCierre, pageable);
        List<CierreDTO> cierreDTOs = cierresPage.getContent().stream()
                .map(c -> {
                    if (!Boolean.TRUE.equals(c.getCerrado())) {
                        calculateAndFillCierre(c);
                        cierreRepository.save(c);
                    }
                    return CierreDTO.toDTO(c);
                })
                .collect(Collectors.toList());
        return new CierreResponse(cierreDTOs, cierresPage.getTotalElements());
    }

    public CierreResponse getCierresFiltered(String mesCierre, String fechaCierre, Pageable pageable) {
        Page<Cierre> cierresPage;
        if (fechaCierre != null && !fechaCierre.trim().isEmpty()) {
            LocalDate localDate = LocalDate.parse(fechaCierre);
            Instant desde = localDate.atStartOfDay(ZONE_ARGENTINA).toInstant();
            Instant hasta = localDate.plusDays(1).atStartOfDay(ZONE_ARGENTINA).toInstant();
            cierresPage = cierreRepository.findByFechaCierreBetween(desde, hasta, pageable);
        } else if (mesCierre != null && !mesCierre.trim().isEmpty()) {
            cierresPage = cierreRepository.findByMesCierre(mesCierre, pageable);
        } else {
            cierresPage = cierreRepository.findAll(pageable);
        }
        List<CierreDTO> cierreDTOs = cierresPage.getContent().stream()
                .map(c -> {
                    if (!Boolean.TRUE.equals(c.getCerrado())) {
                        calculateAndFillCierre(c);
                        cierreRepository.save(c);
                    }
                    return CierreDTO.toDTO(c);
                })
                .collect(Collectors.toList());
        return new CierreResponse(cierreDTOs, cierresPage.getTotalElements());
    }

    public CierreDTO createCierre(CierreDTO dto, String username) {
        // Validar si ya hay un cierre para el día de hoy
        LocalDate todayLocal = LocalDate.now(ZONE_ARGENTINA);
        Instant startOfDay = todayLocal.atStartOfDay(ZONE_ARGENTINA).toInstant();
        Instant endOfDay = todayLocal.plusDays(1).atStartOfDay(ZONE_ARGENTINA).toInstant();

        List<Cierre> existing = cierreRepository.findAll().stream()
                .filter(c -> c.getFechaCierre() != null && c.getFechaCierre().isAfter(startOfDay) && c.getFechaCierre().isBefore(endOfDay))
                .collect(Collectors.toList());

        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Ya existe un cierre registrado para el día de hoy.");
        }

        User user = userRepository.findByUsername(username).orElse(null);

        Cierre cierre = new Cierre();
        cierre.setMontoInicial(dto.getMontoInicial() != null ? dto.getMontoInicial() : BigDecimal.ZERO);
        cierre.setMontoFinal(BigDecimal.ZERO);
        cierre.setFechaCierre(Instant.now());
        cierre.setMesCierre(dto.getMesCierre());
        cierre.setResponsable(user != null ? user.getUsername() : username);
        cierre.setIdUsuario(user != null ? user.getId() : null);

        calculateAndFillCierre(cierre);

        Cierre saved = cierreRepository.save(cierre);
        return CierreDTO.toDTO(saved);
    }

    public CierreDTO updateCierre(Integer id, CierreDTO dto) {
        Cierre cierre = cierreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cierre no encontrado con ID: " + id));

        if (Boolean.TRUE.equals(cierre.getCerrado())) {
            throw new IllegalArgumentException("No se puede modificar un cierre de caja que ya está bloqueado.");
        }

        if (dto.getMontoInicial() != null) {
            cierre.setMontoInicial(dto.getMontoInicial());
        }
        if (dto.getMontoFinal() != null) {
            cierre.setMontoFinal(dto.getMontoFinal());
        }

        calculateAndFillCierre(cierre);

        Cierre saved = cierreRepository.save(cierre);
        return CierreDTO.toDTO(saved);
    }

    public CierreDTO cerrarCierre(Integer id) {
        Cierre cierre = cierreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cierre no encontrado con ID: " + id));

        calculateAndFillCierre(cierre);
        cierre.setCerrado(true);

        Cierre saved = cierreRepository.save(cierre);
        return CierreDTO.toDTO(saved);
    }

    private void calculateAndFillCierre(Cierre cierre) {
        Instant fechaCierre = cierre.getFechaCierre();
        if (fechaCierre == null) {
            fechaCierre = Instant.now();
            cierre.setFechaCierre(fechaCierre);
        }
        ZonedDateTime zdt = ZonedDateTime.ofInstant(fechaCierre, ZONE_ARGENTINA);
        Instant startOfDay = zdt.toLocalDate().atStartOfDay(ZONE_ARGENTINA).toInstant();
        Instant endOfDay = zdt.toLocalDate().plusDays(1).atStartOfDay(ZONE_ARGENTINA).toInstant();

        // 1. Ingresos (Presupuestos, Señas, Ventas)
        List<PagoPresupuesto> pagosPresupuesto = pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(startOfDay, endOfDay);
        List<PagoVenta> pagosVenta = pagoVentaRepository.findByFechaHoraBetween(startOfDay, endOfDay);

        System.out.println("DEBUG CIERRE: Calculando cierre ID: " + cierre.getId() + " para fecha: " + fechaCierre);
        System.out.println("DEBUG CIERRE: Rango UTC de busqueda: desde " + startOfDay + " hasta " + endOfDay);
        System.out.println("DEBUG CIERRE: Cantidad de pagos presupuesto encontrados en el rango: " + pagosPresupuesto.size());

        BigDecimal totalPresupuestosEfectivo = BigDecimal.ZERO;
        BigDecimal totalSeniaEfectivo = BigDecimal.ZERO;

        for (PagoPresupuesto p : pagosPresupuesto) {
            String medioTipo = p.getIdMedioPago() != null ? p.getIdMedioPago().getTipo() : "NULL";
            String tipoPagoTipo = p.getIdTipoPago() != null ? p.getIdTipoPago().getTipo() : "NULL";
            System.out.println("  -> Pago Presupuesto ID: " + p.getId() + ", Monto: " + p.getMonto() + ", Medio: " + medioTipo + ", Tipo: " + tipoPagoTipo + ", Enabled: " + p.getEnabled());
            
            boolean esEfectivo = false;
            if (p.getIdMedioPago() != null) {
                String mTipo = p.getIdMedioPago().getTipo();
                if ("EFECTIVO".equalsIgnoreCase(mTipo != null ? mTipo.trim() : "") || p.getIdMedioPago().getId() == 1) {
                    esEfectivo = true;
                }
            }

            if (esEfectivo) {
                Integer idTipo = p.getIdTipoPago() != null ? p.getIdTipoPago().getId() : 0;
                String tTipo = p.getIdTipoPago() != null ? p.getIdTipoPago().getTipo() : "";
                
                if (idTipo == 2 || "PRESUPUESTO".equalsIgnoreCase(tTipo != null ? tTipo.trim() : "")) {
                    totalPresupuestosEfectivo = totalPresupuestosEfectivo.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
                } else if (idTipo == 1 || "SENIA".equalsIgnoreCase(tTipo != null ? tTipo.trim() : "") || "SEÑA".equalsIgnoreCase(tTipo != null ? tTipo.trim() : "")) {
                    totalSeniaEfectivo = totalSeniaEfectivo.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
                }
            }
        }

        System.out.println("DEBUG CIERRE: Sumarizado -> totalPresupuestosEfectivo: " + totalPresupuestosEfectivo + ", totalSeniaEfectivo: " + totalSeniaEfectivo);
        System.out.println("DEBUG CIERRE: Cantidad de pagos venta encontrados en el rango: " + pagosVenta.size());

        BigDecimal totalVentasEfectivo = BigDecimal.ZERO;
        for (PagoVenta p : pagosVenta) {
            String medioTipo = p.getIdMedioPago() != null ? p.getIdMedioPago().getTipo() : "NULL";
            System.out.println("  -> Pago Venta ID: " + p.getId() + ", Monto: " + p.getMonto() + ", Medio: " + medioTipo);
            
            boolean esEfectivo = false;
            if (p.getIdMedioPago() != null) {
                String mTipo = p.getIdMedioPago().getTipo();
                if ("EFECTIVO".equalsIgnoreCase(mTipo != null ? mTipo.trim() : "") || p.getIdMedioPago().getId() == 1) {
                    esEfectivo = true;
                }
            }
            if (esEfectivo) {
                totalVentasEfectivo = totalVentasEfectivo.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
            }
        }
        System.out.println("DEBUG CIERRE: Sumarizado -> totalVentasEfectivo: " + totalVentasEfectivo);

        // 2. Egresos (Extracciones, Compra de Materiales, Gastos Generales)
        List<Extraccione> extracciones = extraccionRepository.findByFechaExtraccionBetween(startOfDay, endOfDay);
        List<CompraMateriale> compras = compraMaterialeRepository.findByFechaHoraCompraBetween(startOfDay, endOfDay);
        List<Gasto> gastosGenerales = gastoRepository.findByFechaGastoBetween(startOfDay, endOfDay);

        BigDecimal totalExtraccionesEfectivo = extracciones.stream()
                .map(Extraccione::getMontoExtraccion)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComprasEfectivo = compras.stream()
                .map(CompraMateriale::getMontoTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGastosGenerales = gastosGenerales.stream()
                .map(Gasto::getMontoGasto)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Calcular Arqueo Teórico (Monto Aplicación - sin incluir el monto inicial)
        BigDecimal arqueo = totalPresupuestosEfectivo
                .add(totalSeniaEfectivo)
                .add(totalVentasEfectivo)
                .subtract(totalExtraccionesEfectivo)
                .subtract(totalComprasEfectivo)
                .subtract(totalGastosGenerales);

        BigDecimal montoInicial = cierre.getMontoInicial() != null ? cierre.getMontoInicial() : BigDecimal.ZERO;
        BigDecimal montoTotal = montoInicial.add(arqueo);
        BigDecimal montoFinal = cierre.getMontoFinal() != null ? cierre.getMontoFinal() : BigDecimal.ZERO;
        
        // Diferencia es Monto total - Monto arqueo (montoFinal)
        BigDecimal diferencia = montoTotal.subtract(montoFinal);

        cierre.setMontoPresupuestos(totalPresupuestosEfectivo);
        cierre.setSenia(totalSeniaEfectivo);
        cierre.setVentas(totalVentasEfectivo);
        cierre.setMontoExtracciones(totalExtraccionesEfectivo);
        cierre.setMontoCompraMateriales(totalComprasEfectivo);
        cierre.setGastos(totalGastosGenerales);
        cierre.setDescuentoEfectivo(BigDecimal.ZERO);
        cierre.setArqueo(arqueo);
        cierre.setDiferencia(diferencia);
    }

    public ReporteDiarioDTO getReporteDiario(String fechaStr) {
        LocalDate localDate = LocalDate.parse(fechaStr);
        Instant startOfDay = localDate.atStartOfDay(ZONE_ARGENTINA).toInstant();
        Instant endOfDay = localDate.plusDays(1).atStartOfDay(ZONE_ARGENTINA).toInstant();

        List<MovimientoReporteDTO> ingresos = new ArrayList<>();
        List<MovimientoReporteDTO> egresos = new ArrayList<>();

        BigDecimal totalIngresosEfectivo = BigDecimal.ZERO;
        BigDecimal totalIngresosOtrosMedios = BigDecimal.ZERO;
        BigDecimal totalEgresosEfectivo = BigDecimal.ZERO;

        // 1. Ingresos por Pagos de Presupuesto
        List<PagoPresupuesto> pagosPresupuesto = pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(startOfDay, endOfDay);
        for (PagoPresupuesto p : pagosPresupuesto) {
            String medio = p.getIdMedioPago() != null ? (p.getIdMedioPago().getDescripcion() != null ? p.getIdMedioPago().getDescripcion() : p.getIdMedioPago().getTipo()) : "-";
            String cat = p.getIdTipoPago() != null ? p.getIdTipoPago().getTipo() : "PRESUPUESTO";
            BigDecimal monto = p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO;

            boolean esEfectivo = false;
            if (p.getIdMedioPago() != null) {
                String mTipo = p.getIdMedioPago().getTipo();
                if ("EFECTIVO".equalsIgnoreCase(mTipo != null ? mTipo.trim() : "") || p.getIdMedioPago().getId() == 1) {
                    esEfectivo = true;
                }
            }

            if (esEfectivo) {
                totalIngresosEfectivo = totalIngresosEfectivo.add(monto);
            } else {
                totalIngresosOtrosMedios = totalIngresosOtrosMedios.add(monto);
            }

            ingresos.add(MovimientoReporteDTO.builder()
                    .tipoMovimiento("INGRESO")
                    .categoria(cat)
                    .descripcion("Pago Presupuesto #" + p.getIdPresupuesto() + (p.getNotas() != null && !p.getNotas().isBlank() ? " - " + p.getNotas() : ""))
                    .monto(monto)
                    .medioPago(medio)
                    .fechaHora(p.getFechaHora())
                    .responsable("-")
                    .build());
        }

        // 2. Ingresos por Pagos de Ventas
        List<PagoVenta> pagosVenta = pagoVentaRepository.findByFechaHoraBetween(startOfDay, endOfDay);
        for (PagoVenta p : pagosVenta) {
            String medio = p.getIdMedioPago() != null ? (p.getIdMedioPago().getDescripcion() != null ? p.getIdMedioPago().getDescripcion() : p.getIdMedioPago().getTipo()) : "-";
            BigDecimal monto = p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO;

            boolean esEfectivo = false;
            if (p.getIdMedioPago() != null) {
                String mTipo = p.getIdMedioPago().getTipo();
                if ("EFECTIVO".equalsIgnoreCase(mTipo != null ? mTipo.trim() : "") || p.getIdMedioPago().getId() == 1) {
                    esEfectivo = true;
                }
            }

            if (esEfectivo) {
                totalIngresosEfectivo = totalIngresosEfectivo.add(monto);
            } else {
                totalIngresosOtrosMedios = totalIngresosOtrosMedios.add(monto);
            }

            ingresos.add(MovimientoReporteDTO.builder()
                    .tipoMovimiento("INGRESO")
                    .categoria("VENTA")
                    .descripcion("Pago Venta #" + (p.getIdVenta() != null ? p.getIdVenta().getId() : "") + (p.getNotas() != null && !p.getNotas().isBlank() ? " - " + p.getNotas() : ""))
                    .monto(monto)
                    .medioPago(medio)
                    .fechaHora(p.getFechaHora())
                    .responsable("-")
                    .build());
        }

        // 3. Egresos por Extracciones
        List<Extraccione> extracciones = extraccionRepository.findByFechaExtraccionBetween(startOfDay, endOfDay);
        for (Extraccione e : extracciones) {
            BigDecimal monto = e.getMontoExtraccion() != null ? e.getMontoExtraccion() : BigDecimal.ZERO;
            totalEgresosEfectivo = totalEgresosEfectivo.add(monto);

            String resp = e.getIdUsuario() != null ? userRepository.findById(e.getIdUsuario()).map(User::getUsername).orElse("-") : "-";

            egresos.add(MovimientoReporteDTO.builder()
                    .tipoMovimiento("EGRESO")
                    .categoria("EXTRACCION")
                    .descripcion(e.getMotivoExtraccion() != null ? e.getMotivoExtraccion() : "Retiro de caja")
                    .monto(monto)
                    .medioPago("EFECTIVO")
                    .fechaHora(e.getFechaExtraccion())
                    .responsable(resp)
                    .build());
        }

        // 4. Egresos por Compra de Materiales
        List<CompraMateriale> compras = compraMaterialeRepository.findByFechaHoraCompraBetween(startOfDay, endOfDay);
        for (CompraMateriale c : compras) {
            BigDecimal monto = c.getMontoTotal() != null ? c.getMontoTotal() : BigDecimal.ZERO;
            totalEgresosEfectivo = totalEgresosEfectivo.add(monto);

            String resp = c.getIdUser() != null ? userRepository.findById(c.getIdUser()).map(User::getUsername).orElse("-") : "-";

            egresos.add(MovimientoReporteDTO.builder()
                    .tipoMovimiento("EGRESO")
                    .categoria("COMPRA_MATERIAL")
                    .descripcion("Compra de " + c.getCantidad() + " " + (c.getMaterial() != null ? c.getMaterial() : "Materiales") + " (caja: " + c.getCaja() + ")")
                    .monto(monto)
                    .medioPago("EFECTIVO")
                    .fechaHora(c.getFechaHoraCompra())
                    .responsable(resp)
                    .build());
        }

        // 5. Egresos por Gastos Generales
        List<Gasto> gastos = gastoRepository.findByFechaGastoBetween(startOfDay, endOfDay);
        for (Gasto g : gastos) {
            BigDecimal monto = g.getMontoGasto() != null ? g.getMontoGasto() : BigDecimal.ZERO;
            totalEgresosEfectivo = totalEgresosEfectivo.add(monto);

            egresos.add(MovimientoReporteDTO.builder()
                    .tipoMovimiento("EGRESO")
                    .categoria("GASTO")
                    .descripcion(g.getMotivoGasto() != null ? g.getMotivoGasto() : "Gasto general")
                    .monto(monto)
                    .medioPago("EFECTIVO")
                    .fechaHora(g.getFechaGasto())
                    .responsable(g.getResponsableGasto())
                    .build());
        }

        BigDecimal balanceCajaEfectivo = totalIngresosEfectivo.subtract(totalEgresosEfectivo);

        ReporteDiarioDTO report = new ReporteDiarioDTO();
        report.setFecha(fechaStr);
        report.setIngresos(ingresos);
        report.setEgresos(egresos);
        report.setTotalIngresosEfectivo(totalIngresosEfectivo);
        report.setTotalEgresosEfectivo(totalEgresosEfectivo);
        report.setTotalIngresosOtrosMedios(totalIngresosOtrosMedios);
        report.setBalanceCajaEfectivo(balanceCajaEfectivo);

        return report;
    }
}
