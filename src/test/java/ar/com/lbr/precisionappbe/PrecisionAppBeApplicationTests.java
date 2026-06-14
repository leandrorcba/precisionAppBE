package ar.com.lbr.precisionappbe;

import ar.com.lbr.precisionappbe.model.Cierre;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.Role;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.CierreRepository;
import ar.com.lbr.precisionappbe.repositories.CompraMaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import ar.com.lbr.precisionappbe.repositories.ExtraccionRepository;
import ar.com.lbr.precisionappbe.repositories.GastoRepository;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import ar.com.lbr.precisionappbe.services.CierreService;
import ar.com.lbr.precisionappbe.dto.CierreDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("local")
class PrecisionAppBeApplicationTests {

    @Autowired
    private CierreService cierreService;

    @Autowired
    private PagoPresupuestoRepository pagoPresupuestoRepository;

    @Autowired
    private MedioPagoRepository medioPagoRepository;

    @Autowired
    private TipoPagoRepository tipoPagoRepository;

    @Autowired
    private CierreRepository cierreRepository;

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private TrabajoPresupuestadoRepository trabajoPresupuestadoRepository;

    @Autowired
    private PagoVentaRepository pagoVentaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ExtraccionRepository extraccionRepository;

    @Autowired
    private CompraMaterialeRepository compraMaterialeRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private DescuentoRepository descuentoRepository;

    private static final ZoneId ZONE_ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");

    @Test
    @Transactional // Evita LazyInitializationException al navegar relaciones lazy en el test
    void testCierreCalculaPagosPresupuestoCorrectamente() {
        System.out.println("========== INICIANDO TEST DE INTEGRACION DE CIERRE DE CAJA ==========");

        // 1. Limpiar cierres, pagos, trabajos, eventos, presupuestos y otros egresos/ingresos previos
        eventsRepository.deleteAllInBatch();
        trabajoPresupuestadoRepository.deleteAllInBatch();
        pagoPresupuestoRepository.deleteAllInBatch();
        pagoVentaRepository.deleteAllInBatch();
        ventaRepository.deleteAllInBatch();
        extraccionRepository.deleteAllInBatch();
        compraMaterialeRepository.deleteAllInBatch();
        gastoRepository.deleteAllInBatch();
        cierreRepository.deleteAllInBatch();
        descuentoRepository.deleteAllInBatch();
        presupuestoRepository.deleteAllInBatch();

        // 2. Obtener o crear TipoPago y MedioPago
        TipoPago tipoPresupuesto = tipoPagoRepository.findAll().stream()
                .filter(t -> "PRESUPUESTO".equalsIgnoreCase(t.getTipo()))
                .findFirst()
                .orElseGet(() -> {
                    TipoPago tp = new TipoPago();
                    tp.setId(2);
                    tp.setTipo("PRESUPUESTO");
                    return tipoPagoRepository.save(tp);
                });

        TipoPago tipoSenia = tipoPagoRepository.findAll().stream()
                .filter(t -> "SENIA".equalsIgnoreCase(t.getTipo()))
                .findFirst()
                .orElseGet(() -> {
                    TipoPago tp = new TipoPago();
                    tp.setId(1);
                    tp.setTipo("SENIA");
                    return tipoPagoRepository.save(tp);
                });

        MedioPago medioEfectivo = medioPagoRepository.findAll().stream()
                .filter(m -> "EFECTIVO".equalsIgnoreCase(m.getTipo()))
                .findFirst()
                .orElseGet(() -> {
                    MedioPago mp = new MedioPago();
                    mp.setTipo("EFECTIVO");
                    mp.setDescripcion("Efectivo");
                    return medioPagoRepository.save(mp);
                });

        MedioPago medioTransferencia = medioPagoRepository.findAll().stream()
                .filter(m -> "TRANSFERENCIA".equalsIgnoreCase(m.getTipo()))
                .findFirst()
                .orElseGet(() -> {
                    MedioPago mp = new MedioPago();
                    mp.setTipo("TRANSFERENCIA");
                    mp.setDescripcion("Transferencia");
                    return medioPagoRepository.save(mp);
                });

        // 3. Crear Presupuesto de prueba
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setFechaHoraPresupuesto(Instant.now());
        presupuesto.setIdCliente(1); // ID de cliente genérico
        presupuesto.setPrecioCobrado(new BigDecimal("3000.00"));
        presupuesto.setPrecioSinDescuento(new BigDecimal("3000.00"));
        presupuesto.setAprobado(true);
        presupuesto.setRealizado(false);
        presupuesto.setCobrado(false);
        presupuesto.setEntregado(false);
        presupuesto.setPuntosCanjeados(0);
        presupuesto = presupuestoRepository.save(presupuesto);
        System.out.println("Presupuesto de prueba creado con ID: " + presupuesto.getId());

        // 4. Obtener o crear Usuario para Cierre
        User user = userRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("admin");
                    u.setPassword("password");
                    u.setRole(Role.ADMIN);
                    return userRepository.save(u);
                });

        // 5. Crear pagos en distintas horas de hoy
        ZonedDateTime hoyArgentina = ZonedDateTime.now(ZONE_ARGENTINA);
        System.out.println("Fecha y hora actual en Argentina: " + hoyArgentina);

        // Pago 1: Presupuesto en efectivo a las 10:00 AM de hoy
        Instant horaPago1 = hoyArgentina.withHour(10).withMinute(0).withSecond(0).toInstant();
        PagoPresupuesto pago1 = new PagoPresupuesto();
        pago1.setIdPresupuesto(presupuesto.getId());
        pago1.setIdTipoPago(tipoPresupuesto);
        pago1.setIdMedioPago(medioEfectivo);
        pago1.setMonto(new BigDecimal("1500.50"));
        pago1.setFechaHora(horaPago1);
        pago1.setEnabled(true);
        pagoPresupuestoRepository.save(pago1);

        // Pago 2: Seña en efectivo a las 2:00 PM de hoy
        Instant horaPago2 = hoyArgentina.withHour(14).withMinute(0).withSecond(0).toInstant();
        PagoPresupuesto pago2 = new PagoPresupuesto();
        pago2.setIdPresupuesto(presupuesto.getId());
        pago2.setIdTipoPago(tipoSenia);
        pago2.setIdMedioPago(medioEfectivo);
        pago2.setMonto(new BigDecimal("500.00"));
        pago2.setFechaHora(horaPago2);
        pago2.setEnabled(true);
        pagoPresupuestoRepository.save(pago2);

        // Pago 3: Pago en Transferencia (no debe sumarse) a las 4:00 PM de hoy
        Instant horaPago3 = hoyArgentina.withHour(16).withMinute(0).withSecond(0).toInstant();
        PagoPresupuesto pago3 = new PagoPresupuesto();
        pago3.setIdPresupuesto(presupuesto.getId());
        pago3.setIdTipoPago(tipoPresupuesto);
        pago3.setIdMedioPago(medioTransferencia);
        pago3.setMonto(new BigDecimal("800.00"));
        pago3.setFechaHora(horaPago3);
        pago3.setEnabled(true);
        pagoPresupuestoRepository.save(pago3);

        // Pago 4: Pago deshabilitado (no debe sumarse)
        Instant horaPago4 = hoyArgentina.withHour(11).withMinute(0).withSecond(0).toInstant();
        PagoPresupuesto pago4 = new PagoPresupuesto();
        pago4.setIdPresupuesto(presupuesto.getId());
        pago4.setIdTipoPago(tipoPresupuesto);
        pago4.setIdMedioPago(medioEfectivo);
        pago4.setMonto(new BigDecimal("100.00"));
        pago4.setFechaHora(horaPago4);
        pago4.setEnabled(false);
        pagoPresupuestoRepository.save(pago4);

        // 6. Crear el cierre de caja de hoy inicializando todos los campos NOT NULL
        Cierre cierre = new Cierre();
        cierre.setMontoInicial(new BigDecimal("1000.00"));
        cierre.setMontoFinal(BigDecimal.ZERO);
        cierre.setFechaCierre(Instant.now());
        cierre.setMesCierre("2026-05-12");
        cierre.setCerrado(false);
        cierre.setResponsable(user.getUsername());
        cierre.setIdUsuario(user.getId());
        
        cierre.setArqueo(BigDecimal.ZERO);
        cierre.setDiferencia(BigDecimal.ZERO);
        cierre.setMontoPresupuestos(BigDecimal.ZERO);
        cierre.setSenia(BigDecimal.ZERO);
        cierre.setVentas(BigDecimal.ZERO);
        cierre.setMontoExtracciones(BigDecimal.ZERO);
        cierre.setMontoCompraMateriales(BigDecimal.ZERO);
        cierre.setGastos(BigDecimal.ZERO);
        cierre.setDescuentoEfectivo(BigDecimal.ZERO);

        Cierre savedCierre = cierreRepository.save(cierre);

        // 7. Ejecutar la llamada que recupera los cierres
        List<CierreDTO> result = cierreService
                .getCierresFiltered(null, null, org.springframework.data.domain.PageRequest.of(0, 10))
                .getCierres();
        
        System.out.println("Cierres devueltos: " + result.size());
        CierreDTO dto = result.stream().filter(r -> r.getId().equals(savedCierre.getId())).findFirst().orElse(null);
        
        assertNotNull(dto);
        System.out.println("Cierre calculado -> montoPresupuestos: " + dto.getMontoPresupuestos()
                + ", senia: " + dto.getSenia() + ", arqueo (teorico): " + dto.getArqueo());

        assertEquals(new BigDecimal("1500.50"), dto.getMontoPresupuestos());
        assertEquals(new BigDecimal("500.00"), dto.getSenia());
        assertEquals(new BigDecimal("2000.50"), dto.getArqueo());
        
        System.out.println("========== TEST FINALIZADO CON EXITO ==========");
    }
}
