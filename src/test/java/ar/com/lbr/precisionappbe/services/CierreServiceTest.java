package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CierreDTO;
import ar.com.lbr.precisionappbe.dto.ReporteDiarioDTO;
import ar.com.lbr.precisionappbe.dto.response.CierreResponse;
import ar.com.lbr.precisionappbe.model.Cierre;
import ar.com.lbr.precisionappbe.model.Extraccione;
import ar.com.lbr.precisionappbe.model.Gasto;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.CierreRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.ExtraccionRepository;
import ar.com.lbr.precisionappbe.repositories.GastoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CierreServiceTest {

    @Mock
    private CierreRepository cierreRepository;
    @Mock
    private PagoPresupuestoRepository pagoPresupuestoRepository;
    @Mock
    private PagoVentaRepository pagoVentaRepository;
    @Mock
    private ExtraccionRepository extraccionRepository;
    @Mock
    private GastoRepository gastoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DescuentoRepository descuentoRepository;
    @Mock
    private AuditLogService auditLogService;

    private CierreService service;

    @BeforeEach
    void setUp() {
        service = new CierreService(
                cierreRepository, pagoPresupuestoRepository, pagoVentaRepository,
                extraccionRepository, gastoRepository,
                userRepository, descuentoRepository, auditLogService);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Cierre cierreAbierto(Integer id, Instant fechaCierre) {
        Cierre c = new Cierre();
        c.setId(id);
        c.setFechaCierre(fechaCierre);
        c.setMontoInicial(new BigDecimal("1000.00"));
        c.setMontoFinal(BigDecimal.ZERO);
        c.setCerrado(false);
        return c;
    }

    private Cierre cierreCerrado(Integer id) {
        Cierre c = cierreAbierto(id, Instant.now());
        c.setCerrado(true);
        c.setArqueo(BigDecimal.ZERO);
        c.setDiferencia(BigDecimal.ZERO);
        c.setMontoPresupuestos(BigDecimal.ZERO);
        c.setSenia(BigDecimal.ZERO);
        c.setVentas(BigDecimal.ZERO);
        c.setMontoExtracciones(BigDecimal.ZERO);
        c.setMontoCompraMateriales(BigDecimal.ZERO);
        c.setGastos(BigDecimal.ZERO);
        c.setDescuentoEfectivo(BigDecimal.ZERO);
        return c;
    }

    private MedioPago medioEfectivo() {
        MedioPago mp = new MedioPago();
        mp.setId(1);
        mp.setTipo("EFECTIVO");
        return mp;
    }

    private MedioPago medioTransferencia() {
        MedioPago mp = new MedioPago();
        mp.setId(2);
        mp.setTipo("TRANSFERENCIA");
        return mp;
    }

    private TipoPago tipoPresupuesto() {
        TipoPago tp = new TipoPago();
        tp.setId(2);
        tp.setTipo("PRESUPUESTO");
        return tp;
    }

    private TipoPago tipoSenia() {
        TipoPago tp = new TipoPago();
        tp.setId(1);
        tp.setTipo("SENIA");
        return tp;
    }

    private PagoPresupuesto buildPagoPresupuesto(BigDecimal monto, MedioPago medio, TipoPago tipo) {
        PagoPresupuesto p = new PagoPresupuesto();
        p.setId(1);
        p.setMonto(monto);
        p.setIdMedioPago(medio);
        p.setIdTipoPago(tipo);
        p.setEnabled(true);
        p.setFechaHora(Instant.now());
        p.setIdPresupuesto(100);
        return p;
    }

    /** Stub repos de cálculo para que devuelvan vacío. */
    private void stubCalculoVacio() {
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(Collections.emptyList());
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(gastoRepository.findByFechaGastoBetween(any(), any()))
                .thenReturn(Collections.emptyList());
    }

    // =========================================================================
    // createCierre
    // =========================================================================

    @Test
    void createCierre_sinCierreHoy_guardaYDevuelveDTO() {
        when(cierreRepository.existsByFechaCierreBetween(any(Instant.class), any(Instant.class))).thenReturn(false);

        User user = new User();
        user.setId(5);
        user.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        stubCalculoVacio();

        Cierre saved = cierreAbierto(1, Instant.now());
        saved.setMontoInicial(new BigDecimal("500.00"));
        saved.setArqueo(BigDecimal.ZERO);
        saved.setDiferencia(BigDecimal.ZERO);
        saved.setMontoPresupuestos(BigDecimal.ZERO);
        saved.setSenia(BigDecimal.ZERO);
        saved.setVentas(BigDecimal.ZERO);
        saved.setMontoExtracciones(BigDecimal.ZERO);
        saved.setMontoCompraMateriales(BigDecimal.ZERO);
        saved.setGastos(BigDecimal.ZERO);
        saved.setDescuentoEfectivo(BigDecimal.ZERO);
        when(cierreRepository.save(any(Cierre.class))).thenReturn(saved);

        CierreDTO dto = new CierreDTO();
        dto.setMontoInicial(new BigDecimal("500.00"));
        dto.setMesCierre("2026-06");

        CierreDTO result = service.createCierre(dto, "admin");

        assertThat(result).isNotNull();
        assertThat(result.getMontoInicial()).isEqualByComparingTo("500.00");
        verify(cierreRepository).save(any(Cierre.class));
        verify(auditLogService).log(eq("CREAR"), eq("CIERRES"), anyString(), anyString());
    }

    @Test
    void createCierre_yaExisteCierreHoy_lanzaIllegalArgument() {
        when(cierreRepository.existsByFechaCierreBetween(any(Instant.class), any(Instant.class))).thenReturn(true);

        CierreDTO dto = new CierreDTO();
        assertThatThrownBy(() -> service.createCierre(dto, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un cierre registrado para el día de hoy");

        verify(cierreRepository, never()).save(any());
    }

    // =========================================================================
    // updateCierre
    // =========================================================================

    @Test
    void updateCierre_existenteAbierto_actualizaMontoInicialYDevuelve() {
        Cierre cierre = cierreAbierto(10, Instant.now());
        when(cierreRepository.findById(10)).thenReturn(Optional.of(cierre));
        stubCalculoVacio();
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CierreDTO update = new CierreDTO();
        update.setMontoInicial(new BigDecimal("2000.00"));

        CierreDTO result = service.updateCierre(10, update);

        assertThat(result.getMontoInicial()).isEqualByComparingTo("2000.00");
        verify(auditLogService).log(eq("MODIFICAR"), eq("CIERRES"), eq("10"), anyString());
    }

    @Test
    void updateCierre_noEncontrado_lanzaIllegalArgument() {
        when(cierreRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCierre(999, new CierreDTO()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cierre no encontrado con ID: 999");
    }

    @Test
    void updateCierre_cierreBloqueado_lanzaIllegalArgument() {
        Cierre cerrado = cierreAbierto(7, Instant.now());
        cerrado.setCerrado(true);
        when(cierreRepository.findById(7)).thenReturn(Optional.of(cerrado));

        assertThatThrownBy(() -> service.updateCierre(7, new CierreDTO()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bloqueado");

        verify(cierreRepository, never()).save(any());
    }

    // =========================================================================
    // cerrarCierre
    // =========================================================================

    @Test
    void cerrarCierre_existente_poneCerradoTrueYGuarda() {
        Cierre cierre = cierreAbierto(5, Instant.now());
        when(cierreRepository.findById(5)).thenReturn(Optional.of(cierre));
        stubCalculoVacio();
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CierreDTO result = service.cerrarCierre(5);

        assertThat(result.getCerrado()).isTrue();
        verify(cierreRepository).save(argThat(c -> Boolean.TRUE.equals(c.getCerrado())));
        verify(auditLogService).log(eq("CERRAR"), eq("CIERRES"), eq("5"), anyString());
    }

    @Test
    void cerrarCierre_noEncontrado_lanzaIllegalArgument() {
        when(cierreRepository.findById(42)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cerrarCierre(42))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cierre no encontrado con ID: 42");
    }

    // =========================================================================
    // calculateAndFillCierre — lógica de cálculo vía cerrarCierre
    // =========================================================================

    @Test
    void calcular_pagoPresupuestoEfectivo_sumaAMontoPresupuestosYArqueo() {
        Cierre cierre = cierreAbierto(1, Instant.now());
        cierre.setMontoInicial(BigDecimal.ZERO);
        when(cierreRepository.findById(1)).thenReturn(Optional.of(cierre));
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PagoPresupuesto pago = buildPagoPresupuesto(
                new BigDecimal("300.00"), medioEfectivo(), tipoPresupuesto());
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(List.of(pago));
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(Collections.emptyList());
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(Collections.emptyList());

        CierreDTO result = service.cerrarCierre(1);

        assertThat(result.getMontoPresupuestos()).isEqualByComparingTo("300.00");
        assertThat(result.getArqueo()).isEqualByComparingTo("300.00");
        assertThat(result.getSenia()).isEqualByComparingTo("0.00");
    }

    @Test
    void calcular_pagoSeniaEfectivo_sumaASeniaNoAPresupuestos() {
        Cierre cierre = cierreAbierto(2, Instant.now());
        cierre.setMontoInicial(BigDecimal.ZERO);
        when(cierreRepository.findById(2)).thenReturn(Optional.of(cierre));
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PagoPresupuesto senia = buildPagoPresupuesto(
                new BigDecimal("250.00"), medioEfectivo(), tipoSenia());
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(List.of(senia));
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(Collections.emptyList());
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(Collections.emptyList());

        CierreDTO result = service.cerrarCierre(2);

        assertThat(result.getSenia()).isEqualByComparingTo("250.00");
        assertThat(result.getMontoPresupuestos()).isEqualByComparingTo("0.00");
        assertThat(result.getArqueo()).isEqualByComparingTo("250.00");
    }

    @Test
    void calcular_pagoTransferencia_noContaEnArqueo() {
        Cierre cierre = cierreAbierto(3, Instant.now());
        cierre.setMontoInicial(BigDecimal.ZERO);
        when(cierreRepository.findById(3)).thenReturn(Optional.of(cierre));
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PagoPresupuesto transferencia = buildPagoPresupuesto(
                new BigDecimal("500.00"), medioTransferencia(), tipoPresupuesto());
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(List.of(transferencia));
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(Collections.emptyList());
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(Collections.emptyList());

        CierreDTO result = service.cerrarCierre(3);

        // Transferencia no cuenta en efectivo → presupuestos = 0, arqueo = 0
        assertThat(result.getMontoPresupuestos()).isEqualByComparingTo("0.00");
        assertThat(result.getArqueo()).isEqualByComparingTo("0.00");
    }

    @Test
    void calcular_egresosRestanDelArqueo() {
        Cierre cierre = cierreAbierto(4, Instant.now());
        cierre.setMontoInicial(BigDecimal.ZERO);
        when(cierreRepository.findById(4)).thenReturn(Optional.of(cierre));
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Ingreso: 1000 efectivo presupuesto
        PagoPresupuesto pago = buildPagoPresupuesto(
                new BigDecimal("1000.00"), medioEfectivo(), tipoPresupuesto());
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(List.of(pago));
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());

        // Egresos: extraccion 200 + gasto 50 = 250
        Extraccione ext = new Extraccione();
        ext.setMontoExtraccion(new BigDecimal("200.00"));
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(List.of(ext));

        Gasto gasto = new Gasto();
        gasto.setMontoGasto(new BigDecimal("50.00"));
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(List.of(gasto));

        CierreDTO result = service.cerrarCierre(4);

        // arqueo = 1000 - 200 (extracciones) - 50 (gastos) = 750
        assertThat(result.getArqueo()).isEqualByComparingTo("750.00");
        assertThat(result.getMontoExtracciones()).isEqualByComparingTo("200.00");
        assertThat(result.getMontoCompraMateriales()).isEqualByComparingTo("0.00");
        assertThat(result.getGastos()).isEqualByComparingTo("50.00");
    }

    @Test
    void calcular_montoInicialSumaAlMontoTotal() {
        Cierre cierre = cierreAbierto(5, Instant.now());
        cierre.setMontoInicial(new BigDecimal("500.00"));
        when(cierreRepository.findById(5)).thenReturn(Optional.of(cierre));
        stubCalculoVacio();
        when(cierreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CierreDTO result = service.cerrarCierre(5);

        // arqueo = 0 (sin pagos), montoTotal = 500 + 0 = 500
        assertThat(result.getArqueo()).isEqualByComparingTo("0.00");
        assertThat(result.getMontoTotal()).isEqualByComparingTo("500.00");
        assertThat(result.getDiferencia()).isEqualByComparingTo("500.00"); // montoTotal - montoFinal(0)
    }

    // =========================================================================
    // getCierresFiltered — routing hacia los repos correctos
    // =========================================================================

    @Test
    void getCierresFiltered_conFechaCierre_usaFindByFechaCierreBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cierre> pagina = new PageImpl<>(List.of(cierreCerrado(1)));
        when(cierreRepository.findByFechaCierreBetween(any(Instant.class), any(Instant.class), eq(pageable)))
                .thenReturn(pagina);

        CierreResponse result = service.getCierresFiltered("2026-06", "2026-06-08", pageable);

        assertThat(result.getCierres()).hasSize(1);
        verify(cierreRepository).findByFechaCierreBetween(any(), any(), eq(pageable));
        verify(cierreRepository, never()).findByMesCierre(any(), any());
        verify(cierreRepository, never()).findAll(pageable);
    }

    @Test
    void getCierresFiltered_conMesCierre_usaFindByMesCierre() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cierre> pagina = new PageImpl<>(List.of(cierreCerrado(2)));
        when(cierreRepository.findByMesCierre("2026-06", pageable)).thenReturn(pagina);

        CierreResponse result = service.getCierresFiltered("2026-06", null, pageable);

        assertThat(result.getCierres()).hasSize(1);
        verify(cierreRepository).findByMesCierre("2026-06", pageable);
        verify(cierreRepository, never()).findAll(pageable);
    }

    @Test
    void getCierresFiltered_sinFiltros_usaFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cierre> pagina = new PageImpl<>(List.of(cierreCerrado(3)));
        when(cierreRepository.findAll(pageable)).thenReturn(pagina);

        CierreResponse result = service.getCierresFiltered(null, null, pageable);

        assertThat(result.getCierres()).hasSize(1);
        verify(cierreRepository).findAll(pageable);
    }

    // =========================================================================
    // getReporteDiario
    // =========================================================================

    @Test
    void getReporteDiario_diaVacio_totalesEnCero() {
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(Collections.emptyList());
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(Collections.emptyList());
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(Collections.emptyList());

        ReporteDiarioDTO report = service.getReporteDiario("2026-06-08");

        assertThat(report.getFecha()).isEqualTo("2026-06-08");
        assertThat(report.getIngresos()).isEmpty();
        assertThat(report.getEgresos()).isEmpty();
        assertThat(report.getTotalIngresosEfectivo()).isEqualByComparingTo("0.00");
        assertThat(report.getTotalEgresosEfectivo()).isEqualByComparingTo("0.00");
        assertThat(report.getBalanceCajaEfectivo()).isEqualByComparingTo("0.00");
    }

    @Test
    void getReporteDiario_conIngresosYEgresos_calculaBalanceCajaCorrectamente() {
        PagoPresupuesto pp = buildPagoPresupuesto(
                new BigDecimal("800.00"), medioEfectivo(), tipoPresupuesto());
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(List.of(pp));
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());

        Extraccione ext = new Extraccione();
        ext.setMontoExtraccion(new BigDecimal("200.00"));
        ext.setMotivoExtraccion("Retiro para gastos");
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(List.of(ext));
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(Collections.emptyList());

        ReporteDiarioDTO report = service.getReporteDiario("2026-06-08");

        assertThat(report.getTotalIngresosEfectivo()).isEqualByComparingTo("800.00");
        assertThat(report.getTotalEgresosEfectivo()).isEqualByComparingTo("200.00");
        assertThat(report.getBalanceCajaEfectivo()).isEqualByComparingTo("600.00");
        assertThat(report.getIngresos()).hasSize(1);
        assertThat(report.getEgresos()).hasSize(1);
        assertThat(report.getEgresos().get(0).getCategoria()).isEqualTo("EXTRACCION");
    }

    @Test
    void getReporteDiario_pagoTransferenciaNoEfectivo_sumaAOtrosMedios() {
        PagoPresupuesto transferencia = buildPagoPresupuesto(
                new BigDecimal("600.00"), medioTransferencia(), tipoPresupuesto());
        when(pagoPresupuestoRepository.findByFechaHoraBetweenAndEnabledTrue(any(), any()))
                .thenReturn(List.of(transferencia));
        when(pagoVentaRepository.findByFechaHoraBetween(any(), any())).thenReturn(Collections.emptyList());
        when(extraccionRepository.findByFechaExtraccionBetween(any(), any())).thenReturn(Collections.emptyList());
        when(gastoRepository.findByFechaGastoBetween(any(), any())).thenReturn(Collections.emptyList());

        ReporteDiarioDTO report = service.getReporteDiario("2026-06-08");

        assertThat(report.getTotalIngresosEfectivo()).isEqualByComparingTo("0.00");
        assertThat(report.getTotalIngresosOtrosMedios()).isEqualByComparingTo("600.00");
        assertThat(report.getBalanceCajaEfectivo()).isEqualByComparingTo("0.00");
    }
}