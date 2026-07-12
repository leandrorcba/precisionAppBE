package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.PresupuestoMapper;
import ar.com.lbr.precisionappbe.dto.AprobarPresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.PresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.response.PresupuestoResponse;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.Presupuesto;

import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
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
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoServiceTest {

    @Mock private PresupuestoRepository presupuestoRepository;
    @Mock private TipoClienteRepository tipoClienteRepository;
    @Mock private PresupuestoMapper presupuestoMapper;
    @Mock private PagoPresupuestoRepository pagoPresupuestoRepository;
    @Mock private DescuentoRepository descuentoRepository;
    @Mock private TrabajoPresupuestadoRepository trabajoPresupuestadoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private MaterialRepository materialRepository;
    @Mock private VariosRepository variosRepository;
    @Mock private EventsService eventsService;
    @Mock private FolderService folderService;
    @Mock private RemitoPdfService remitoPdfService;
    @Mock private AuditLogService auditLogService;

    private PresupuestoService service;

    @BeforeEach
    void setUp() {
        service = new PresupuestoService(presupuestoRepository, tipoClienteRepository, presupuestoMapper, pagoPresupuestoRepository,
                descuentoRepository, trabajoPresupuestadoRepository, clienteRepository, materialRepository, variosRepository,
                eventsService, folderService, remitoPdfService, auditLogService);
    }

    @Test
    void buscarPresupuestoByIdCliente_returnsResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Cliente cliente = new Cliente();
        cliente.setNombreCliente("Juan");

        Presupuesto pr = new Presupuesto();
        pr.setId(1);
        pr.setIdCliente(5);
        Page<Presupuesto> page = new PageImpl<>(List.of(pr));

        PresupuestoDTO dto = new PresupuestoDTO();
        dto.setIdPresupuesto(1);

        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));
        when(presupuestoRepository.findByIdClienteAndHabilitadoOrderByIdDesc(5, true, pageable)).thenReturn(page);
        when(presupuestoMapper.map(any(List.class))).thenReturn(List.of(dto));

        PresupuestoResponse result = service.buscarPresupuestoByIdCliente(5, true, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getPresupuestos()).hasSize(1);
        verify(folderService).crearCarpetaCliente("Juan");
    }

    @Test
    void createPresupuesto_savesAndGeneratesPdf() {
        PresupuestoDTO dto = new PresupuestoDTO();
        dto.setIdCliente(5);

        Presupuesto entity = new Presupuesto();
        entity.setId(12);
        entity.setIdCliente(5);

        Cliente cliente = new Cliente();
        cliente.setNombreCliente("Maria");

        when(presupuestoMapper.toEntity(dto, true)).thenReturn(entity);
        when(presupuestoRepository.save(entity)).thenReturn(entity);
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));
        when(presupuestoRepository.findById(12)).thenReturn(Optional.of(entity));
        when(remitoPdfService.generateRemito(12)).thenReturn(new byte[]{1, 2, 3});

        PresupuestoDTO result = service.createPresupuesto(dto);

        assertThat(result).isNotNull();
        verify(folderService).crearCarpetaPresupuesto("Maria", 12);
        verify(folderService).guardarPdfEnCarpeta("Maria", 12, new byte[]{1, 2, 3});
        verify(auditLogService).log(eq("CREAR"), eq("PRESUPUESTOS"), eq("12"), any(String.class), eq(entity));
    }

    @Test
    void aprobarPresupuesto_updatesStateAndCreatesEvents() {
        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setIdCliente(5);

        Cliente cl = new Cliente();
        cl.setNombreCliente("Luis");

        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setId(20);
        tr.setSeleccionado(true);
        tr.setPrecioTrabajo(BigDecimal.valueOf(100));
        tr.setTiempoDeCorte(15);
        tr.setIdMateriales(1);

        Material mat = new Material();
        mat.setMateriales("MDF 3mm");

        Varios varios = new Varios();
        varios.setHoraInicio(LocalTime.of(9, 0));
        varios.setHoraCierre(LocalTime.of(17, 0));

        AprobarPresupuestoDTO item = new AprobarPresupuestoDTO();
        item.setIdTrabajo(20);
        item.setIdMaquina(1);

        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(trabajoPresupuestadoRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));
        when(trabajoPresupuestadoRepository.findAllById(Collections.singleton(20))).thenReturn(List.of(tr));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cl));
        when(variosRepository.findFirstByOrderByIdAsc()).thenReturn(varios);
        when(materialRepository.findById(1)).thenReturn(Optional.of(mat));

        PresupuestoDTO result = service.aprobarPresupuesto(10, List.of(item));

        assertThat(result).isNotNull();
        assertThat(pr.getAprobado()).isTrue();
        verify(eventsService).createEventForTrabajo(
                eq(1), eq(10), eq(20), any(String.class), eq(15), eq(LocalTime.of(9, 0)), eq(LocalTime.of(17, 0)));
    }

    @Test
    void aprobarPresupuesto_withTraeMaterial_createsEventWithTMInName() {
        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setIdCliente(5);

        Cliente cl = new Cliente();
        cl.setNombreCliente("Luis");

        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setId(20);
        tr.setSeleccionado(true);
        tr.setPrecioTrabajo(BigDecimal.valueOf(100));
        tr.setTiempoDeCorte(15);
        tr.setIdMateriales(1);
        tr.setTraeMaterial(true);

        Material mat = new Material();
        mat.setMateriales("MDF 3mm");

        Varios varios = new Varios();
        varios.setHoraInicio(LocalTime.of(9, 0));
        varios.setHoraCierre(LocalTime.of(17, 0));

        AprobarPresupuestoDTO item = new AprobarPresupuestoDTO();
        item.setIdTrabajo(20);
        item.setIdMaquina(1);

        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(trabajoPresupuestadoRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));
        when(trabajoPresupuestadoRepository.findAllById(Collections.singleton(20))).thenReturn(List.of(tr));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cl));
        when(variosRepository.findFirstByOrderByIdAsc()).thenReturn(varios);
        when(materialRepository.findById(1)).thenReturn(Optional.of(mat));

        service.aprobarPresupuesto(10, List.of(item));

        verify(eventsService).createEventForTrabajo(
                eq(1), eq(10), eq(20), contains("(Trae Material)"), eq(15), eq(LocalTime.of(9, 0)), eq(LocalTime.of(17, 0)));
    }

    @Test
    void cancelarPresupuesto_withRealizadoJobs_throwsIllegalArgument() {
        Presupuesto pr = new Presupuesto();
        pr.setId(10);

        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setEstado(EstadoTrabajo.REALIZADO);

        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(trabajoPresupuestadoRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));

        assertThatThrownBy(() -> service.cancelarPresupuesto(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede cancelar el presupuesto");
    }

    @Test
    void cancelarPresupuesto_successful_deletesEventsAndDisables() {
        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setIdCliente(5);

        Cliente cl = new Cliente();
        cl.setNombreCliente("Ana");

        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setEstado(EstadoTrabajo.PENDIENTE);
        tr.setSeleccionado(true);

        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(trabajoPresupuestadoRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cl));
        when(pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(10)).thenReturn(Collections.emptyList());

        service.cancelarPresupuesto(10);

        assertThat(tr.getSeleccionado()).isFalse();
        assertThat(pr.getHabilitado()).isFalse();
        assertThat(pr.getAprobado()).isFalse();
        verify(eventsService).deleteEventsByPresupuesto(10);
        verify(auditLogService).log(eq("DESHABILITAR"), eq("PRESUPUESTOS"), eq("10"), any(String.class));
    }
}
