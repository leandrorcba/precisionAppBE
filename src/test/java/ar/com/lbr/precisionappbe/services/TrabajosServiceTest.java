package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.Superficie;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrabajosServiceTest {

    @Mock private TrabajoPresupuestadoRepository trabajosRepository;
    @Mock private PresupuestoRepository presupuestoRepository;
    @Mock private MaterialeRepository materialeRepository;
    @Mock private SuperficieRepository superficieRepository;
    @Mock private MaquinasRepository maquinasRepository;
    @Mock private PresupuestoService presupuestoService;
    @Mock private VariosService variosService;
    @Mock private ClienteService clienteService;
    @Mock private PresupuestoCalculadorService presupuestoCalculadorService;
    @Mock private DescuentoRepository descuentoRepository;
    @Mock private FolderService folderService;
    @Mock private AuditLogService auditLogService;
    @Mock private EventsRepository eventsRepository;

    private TrabajosService service;

    @BeforeEach
    void setUp() {
        service = new TrabajosService(trabajosRepository, presupuestoRepository, materialeRepository, superficieRepository,
                maquinasRepository, presupuestoService, variosService, clienteService, presupuestoCalculadorService,
                descuentoRepository, folderService, auditLogService, eventsRepository);
    }

    @Test
    void createTrabajo_validDto_savesAndReturnsDto() {
        TrabajoPresupuestadoDTO dto = new TrabajoPresupuestadoDTO();
        dto.setIdPresupuesto(10);
        dto.setPrecioTrabajo(BigDecimal.valueOf(150));
        dto.setArchivoCad("cut.dxf");

        Cliente client = new Cliente();
        client.setNombreCliente("Luis");

        TrabajoPresupuestado saved = new TrabajoPresupuestado();
        saved.setId(100);
        saved.setIdPresupuesto(10);
        saved.setPrecioTrabajo(BigDecimal.valueOf(150));
        saved.setArchivoCad("cut.dxf");

        when(presupuestoService.getClienteByPresupuestoId(10)).thenReturn(client);
        when(trabajosRepository.save(any(TrabajoPresupuestado.class))).thenReturn(saved);

        TrabajoPresupuestadoDTO result = service.createTrabajo(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100);
        verify(presupuestoCalculadorService).calcularYValidarTrabajo(eq(dto), any(ClienteDTO.class));
        verify(presupuestoService).actualizarPdfFisico(10);
        verify(auditLogService).log(eq("CREAR"), eq("TRABAJOS"), eq("100"), any(String.class));
    }

    @Test
    void updateSeleccionado_existingRealizadoJobs_throwsException() {
        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setId(1);
        tr.setIdPresupuesto(10);

        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setAprobado(true);

        TrabajoPresupuestado trRealizado = new TrabajoPresupuestado();
        trRealizado.setEstado(EstadoTrabajo.REALIZADO);

        when(trabajosRepository.findById(1)).thenReturn(Optional.of(tr));
        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(trabajosRepository.findByIdPresupuesto(10)).thenReturn(List.of(trRealizado));

        assertThatThrownBy(() -> service.updateSeleccionado(1, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede deseleccionar el trabajo");
    }

    @Test
    void updateSeleccionado_deselectsSuccessfully() {
        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setId(1);
        tr.setIdPresupuesto(10);

        Presupuesto pr = new Presupuesto();
        pr.setId(10);
        pr.setAprobado(true);

        when(trabajosRepository.findById(1)).thenReturn(Optional.of(tr));
        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));
        when(trabajosRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));
        when(presupuestoService.tienePagos(10)).thenReturn(false);
        when(trabajosRepository.save(tr)).thenReturn(tr);

        service.updateSeleccionado(1, false);

        assertThat(tr.getSeleccionado()).isFalse();
        assertThat(pr.getAprobado()).isFalse();
        verify(eventsRepository).deleteAll(any());
    }

    @Test
    void updateEstado_propagatesToPresupuesto() {
        TrabajoPresupuestado tr = new TrabajoPresupuestado();
        tr.setId(1);
        tr.setIdPresupuesto(10);
        tr.setSeleccionado(true);

        Presupuesto pr = new Presupuesto();
        pr.setId(10);

        when(trabajosRepository.findById(1)).thenReturn(Optional.of(tr));
        when(trabajosRepository.save(tr)).thenReturn(tr);
        when(trabajosRepository.findByIdPresupuesto(10)).thenReturn(List.of(tr));
        when(presupuestoRepository.findById(10)).thenReturn(Optional.of(pr));

        service.updateEstado(1, EstadoTrabajo.REALIZADO);

        assertThat(pr.getRealizado()).isTrue();
        verify(presupuestoRepository).save(pr);
    }

    @Test
    void getTrabajosByPresupuesto_returnsDtoList() {
        TrabajoPresupuestado t = new TrabajoPresupuestado();
        t.setId(1);
        t.setIdMateriales(5);
        t.setIdSuperficie(6);
        t.setIdMaquina(7);

        Material mat = new Material();
        mat.setId(5);
        mat.setMateriales("MDF 3mm");

        Superficie sup = new Superficie();
        sup.setId(6);
        sup.setValor("1/2");

        Maquina maq = new Maquina();
        maq.setId(7);
        maq.setNombreMaquina("Laser 1");

        when(trabajosRepository.findByIdPresupuesto(10)).thenReturn(List.of(t));
        when(materialeRepository.findAllById(Set.of(5))).thenReturn(List.of(mat));
        when(superficieRepository.findAllById(Set.of(6))).thenReturn(List.of(sup));
        when(maquinasRepository.findAllById(Set.of(7))).thenReturn(List.of(maq));

        List<TrabajoPresupuestadoDTO> result = service.getTrabajosByPresupuesto(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMaterial()).isEqualTo("MDF 3mm");
        assertThat(result.get(0).getSuperficie()).isEqualTo("1/2");
        assertThat(result.get(0).getMaquina()).isEqualTo("Laser 1");
    }
}
