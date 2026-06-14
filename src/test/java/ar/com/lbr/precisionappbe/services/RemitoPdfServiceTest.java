package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Descuento;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemitoPdfServiceTest {

    @Mock private PresupuestoRepository presupuestoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private TrabajoPresupuestadoRepository trabajoRepository;
    @Mock private MaterialRepository materialRepository;
    @Mock private PagoPresupuestoRepository pagoPresupuestoRepository;
    @Mock private DescuentoRepository descuentoRepository;

    private RemitoPdfService service;

    @BeforeEach
    void setUp() {
        service = new RemitoPdfService(presupuestoRepository, clienteRepository, trabajoRepository, materialRepository,
                pagoPresupuestoRepository, descuentoRepository);
    }

    @Test
    void generateRemito_validEntities_returnsPdfBytes() {
        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setIdCliente(5);
        pr.setFechaHoraPresupuesto(Instant.now());
        pr.setPrecioSinDescuento(BigDecimal.valueOf(1200));

        Cliente cl = new Cliente();
        cl.setId(5);
        cl.setNombreCliente("Empresa de Corte");
        cl.setDniCliente("20-30405060-7");
        cl.setTelefonoCliente("351663000");

        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setId(20);
        tr.setPrecioTrabajo(BigDecimal.valueOf(600));
        tr.setEstado(EstadoTrabajo.REALIZADO);
        tr.setNotas("Nota especial");
        tr.setIdMateriales(1);

        Material mat = new Material();
        mat.setId(1);
        mat.setMateriales("MDF 3mm");

        PagoPresupuesto payment = new PagoPresupuesto();
        payment.setId(100);
        payment.setMonto(BigDecimal.valueOf(200));

        Descuento discount = new Descuento();
        discount.setId(1);
        discount.setIdTipoDescuento(1);
        discount.setMonto(BigDecimal.valueOf(100));

        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cl));
        when(trabajoRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));
        when(materialRepository.findById(1)).thenReturn(Optional.of(mat));
        when(pagoPresupuestoRepository.findByIdPresupuestoAndIdTipoPago_IdAndEnabledTrue(10, 1))
                .thenReturn(Collections.singletonList(payment));
        when(pagoPresupuestoRepository.findByIdPresupuestoAndIdTipoPago_IdAndEnabledTrue(10, 2))
                .thenReturn(Collections.singletonList(payment));
        when(descuentoRepository.findByIdPresupuesto(10))
                .thenReturn(Collections.singletonList(discount));

        byte[] pdfBytes = service.generateRemito(10);

        assertThat(pdfBytes).isNotEmpty();
    }

    @Test
    void generateRemito_presupuestoNotFound_throwsNotFound() {
        when(presupuestoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateRemito(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Presupuesto no encontrado");
    }

    @Test
    void generateRemito_clienteNotFound_throwsNotFound() {
        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setIdCliente(5);

        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(clienteRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateRemito(10))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cliente no encontrado");
    }
}
