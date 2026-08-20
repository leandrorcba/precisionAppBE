package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.PagoVenta;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.Venta;
import ar.com.lbr.precisionappbe.repositories.AuditoriaAnulacionPagoRepository;
import ar.com.lbr.precisionappbe.repositories.CierreRepository;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagosServiceTest {

    @Mock private PagoPresupuestoRepository pagoPresupuestoRepository;
    @Mock private PagoVentaRepository pagoVentaRepository;
    @Mock private TipoPagoRepository tipoPagoRepository;
    @Mock private MedioPagoRepository medioPagoRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private PresupuestoRepository presupuestoRepository;
    @Mock private VariosRepository variosRepository;
    @Mock private DescuentoRepository descuentoRepository;
    @Mock private PresupuestoService presupuestoService;
    @Mock private AuditLogService auditLogService;
    @Mock private ClienteRepository clienteRepository;
    @Mock private AuditoriaAnulacionPagoRepository auditoriaAnulacionPagoRepository;
    @Mock private CierreRepository cierreRepository;

    private PagosService service;

    @BeforeEach
    void setUp() {
        service = new PagosService(pagoPresupuestoRepository, pagoVentaRepository, tipoPagoRepository, medioPagoRepository,
                ventaRepository, presupuestoRepository, variosRepository, descuentoRepository, presupuestoService, auditLogService,
                clienteRepository, auditoriaAnulacionPagoRepository, cierreRepository);
    }

    private TipoPago createTipoPago(Integer id, String tipo) {
        TipoPago tp = new TipoPago();
        tp.setId(id);
        tp.setTipo(tipo);
        return tp;
    }

    private MedioPago createMedioPago(Integer id, String tipo, String descripcion) {
        MedioPago mp = new MedioPago();
        mp.setId(id);
        mp.setTipo(tipo);
        mp.setDescripcion(descripcion);
        return mp;
    }

    @Test
    void getPagosByPresupuesto_returnsList() {
        PagoPresupuesto p = new PagoPresupuesto();
        p.setId(1);
        p.setMonto(BigDecimal.TEN);
        p.setIdTipoPago(createTipoPago(1, "SENIA"));
        p.setIdMedioPago(createMedioPago(1, "EFECTIVO", "Efectivo"));

        when(pagoPresupuestoRepository.findByIdPresupuesto(10)).thenReturn(List.of(p));

        List<PagoDTO> result = service.getPagosByPresupuesto(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMonto()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void getPagosByVenta_returnsList() {
        PagoVenta pv = new PagoVenta();
        pv.setId(2);
        pv.setMonto(BigDecimal.valueOf(100));
        pv.setIdTipoPago(createTipoPago(3, "MATERIAL"));
        pv.setIdMedioPago(createMedioPago(1, "EFECTIVO", "Efectivo"));

        when(pagoVentaRepository.findByIdVenta_Id(5)).thenReturn(List.of(pv));

        List<PagoDTO> result = service.getPagosByVenta(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMonto()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void getPagoById_existingInPresupuesto_returnsDto() {
        PagoPresupuesto p = new PagoPresupuesto();
        p.setId(1);
        p.setMonto(BigDecimal.TEN);
        p.setIdTipoPago(createTipoPago(1, "SENIA"));
        p.setIdMedioPago(createMedioPago(1, "EFECTIVO", "Efectivo"));

        when(pagoPresupuestoRepository.findByIdAndEnabledTrue(1)).thenReturn(Optional.of(p));

        PagoDTO result = service.getPagoById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getPagoById_existingInVenta_returnsDto() {
        PagoVenta pv = new PagoVenta();
        pv.setId(2);
        pv.setMonto(BigDecimal.valueOf(100));
        pv.setIdTipoPago(createTipoPago(3, "MATERIAL"));
        pv.setIdMedioPago(createMedioPago(1, "EFECTIVO", "Efectivo"));

        when(pagoPresupuestoRepository.findByIdAndEnabledTrue(2)).thenReturn(Optional.empty());
        when(pagoVentaRepository.findById(2)).thenReturn(Optional.of(pv));

        PagoDTO result = service.getPagoById(2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
    }

    @Test
    void getPagoById_notFound_throwsException() {
        when(pagoPresupuestoRepository.findByIdAndEnabledTrue(99)).thenReturn(Optional.empty());
        when(pagoVentaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPagoById(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Pago no encontrado: 99");
    }

    @Test
    void createPago_senia_firstPayment_saves() {
        PagoDTO dto = new PagoDTO();
        dto.setIdPresupuesto(10);
        dto.setIdTipoPago(1); // SENIA
        dto.setIdMedioPago(1); // EFECTIVO
        dto.setMonto(BigDecimal.valueOf(500));

        TipoPago tp = createTipoPago(1, "SENIA");
        MedioPago mp = createMedioPago(1, "EFECTIVO", "Efectivo");

        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setPrecioSinDescuento(BigDecimal.valueOf(1000));

        PagoPresupuesto saved = new PagoPresupuesto();
        saved.setId(100);
        saved.setIdPresupuesto(10);
        saved.setMonto(BigDecimal.valueOf(500));
        saved.setIdTipoPago(tp);
        saved.setIdMedioPago(mp);

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tp));
        when(medioPagoRepository.findById(1)).thenReturn(Optional.of(mp));
        when(pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(10)).thenReturn(Collections.emptyList());
        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(pagoPresupuestoRepository.save(any(PagoPresupuesto.class))).thenReturn(saved);

        PagoDTO result = service.createPago(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100);
    }


    @Test
    void createPago_venta_saves() {
        PagoDTO dto = new PagoDTO();
        dto.setIdVenta(5);
        dto.setIdTipoPago(3); // VENTA
        dto.setIdMedioPago(2); // TARJETA
        dto.setMonto(BigDecimal.valueOf(300));

        TipoPago tp = createTipoPago(3, "VENTA");
        MedioPago mp = createMedioPago(2, "TARJETA", "Tarjeta");
        Venta v = new Venta();
        v.setId(5);

        PagoVenta saved = new PagoVenta();
        saved.setId(200);
        saved.setIdVenta(v);
        saved.setMonto(BigDecimal.valueOf(300));
        saved.setIdTipoPago(tp);
        saved.setIdMedioPago(mp);

        when(tipoPagoRepository.findById(3)).thenReturn(Optional.of(tp));
        when(medioPagoRepository.findById(2)).thenReturn(Optional.of(mp));
        when(ventaRepository.findById(5)).thenReturn(Optional.of(v));
        when(pagoVentaRepository.save(any(PagoVenta.class))).thenReturn(saved);

        PagoDTO result = service.createPago(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(200);
    }

    @Test
    void updatePago_presupuesto_updates() {
        PagoPresupuesto existing = new PagoPresupuesto();
        existing.setId(50);
        existing.setIdPresupuesto(10);
        existing.setIdTipoPago(createTipoPago(2, "PRESUPUESTO"));
        existing.setIdMedioPago(createMedioPago(1, "EFECTIVO", "Efectivo"));

        PagoDTO dto = new PagoDTO();
        dto.setIdTipoPago(2);
        dto.setIdMedioPago(1);
        dto.setIdPresupuesto(10);
        dto.setEnabled(false);

        when(tipoPagoRepository.findById(2)).thenReturn(Optional.of(createTipoPago(2, "PRESUPUESTO")));
        when(medioPagoRepository.findById(1)).thenReturn(Optional.of(createMedioPago(1, "EFECTIVO", "Efectivo")));
        when(pagoPresupuestoRepository.findById(50)).thenReturn(Optional.of(existing));
        when(pagoPresupuestoRepository.save(existing)).thenReturn(existing);

        PagoDTO result = service.updatePago(50, dto);

        assertThat(result).isNotNull();
        assertThat(existing.getEnabled()).isFalse();
    }

    @Test
    void deletePago_presupuesto_marksDisabled() {
        PagoPresupuesto p = new PagoPresupuesto();
        p.setId(5);
        p.setIdPresupuesto(10);
        p.setIdTipoPago(createTipoPago(2, "PRESUPUESTO"));
        p.setIdMedioPago(createMedioPago(1, "EFECTIVO", "Efectivo"));
        p.setMonto(BigDecimal.TEN);

        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setPrecioSinDescuento(BigDecimal.valueOf(100));

        when(pagoPresupuestoRepository.findByIdAndEnabledTrue(5)).thenReturn(Optional.of(p));
        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));

        service.deletePago(5);

        assertThat(p.getEnabled()).isFalse();
        verify(pagoPresupuestoRepository).save(p);
    }
}
