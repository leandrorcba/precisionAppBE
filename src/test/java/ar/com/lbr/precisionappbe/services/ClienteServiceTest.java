package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.ClientesMapper;
import ar.com.lbr.precisionappbe.dto.ClienteDTO;

import ar.com.lbr.precisionappbe.dto.response.ClienteResponse;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Punto;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.PuntoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClientesMapper clientesMapper;

    @Mock
    private UtilsService utilsService;

    @Mock
    private PuntoRepository puntoRepository;

    @Mock
    private FolderService folderService;

    @Mock
    private AuditLogService auditLogService;

    private ClienteService service;

    @BeforeEach
    void setUp() {
        service = new ClienteService(clienteRepository, clientesMapper, utilsService, puntoRepository, folderService, auditLogService);
    }

    @Test
    @SuppressWarnings("unchecked")
    void buscarClientes_returnsPageOfClientes() {
        Pageable pageable = PageRequest.of(0, 10);
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombreCliente("Cliente Prueba");
        Page<Cliente> page = new PageImpl<>(List.of(cliente));

        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(1);
        dto.setNombreCliente("Cliente Prueba");

        Punto punto = new Punto();
        punto.setIdCliente(1);
        punto.setPuntosAcumulados(10);

        when(clienteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(clientesMapper.map(any(List.class))).thenReturn(List.of(dto));
        when(puntoRepository.findByIdClienteIn(any(List.class))).thenReturn(List.of(punto));

        ClienteResponse response = service.buscarClientes("Cliente", false, 0, false, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getClientes()).hasSize(1);
        assertThat(response.getClientes().get(0).getPunto()).isNotNull();
        assertThat(response.getClientes().get(0).getPunto().getPuntosAcumulados()).isEqualTo(10);
    }

    @Test
    void createCliente_createsAndSavesCliente() {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdTipoCliente(2);
        dto.setNombreCliente("Nuevo Cliente");

        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setId(2);
        tipoCliente.setNombreTipo("Empresa");

        Cliente entity = new Cliente();
        entity.setId(10);
        entity.setNombreCliente("Nuevo Cliente");

        when(utilsService.getTipoClienteById(2)).thenReturn(tipoCliente);
        when(clientesMapper.toEntity(dto, tipoCliente, true)).thenReturn(entity);
        when(clienteRepository.save(entity)).thenReturn(entity);

        ClienteDTO result = service.createCliente(dto);

        assertThat(result).isNotNull();
        assertThat(result.getIdCliente()).isEqualTo(10);
        verify(folderService).crearCarpetaCliente("Nuevo Cliente");
        verify(puntoRepository).save(any(Punto.class));
        verify(auditLogService).log(eq("CREAR"), eq("CLIENTES"), eq("10"), any(String.class), eq(entity));
    }

    @Test
    void rehabilitarCliente_updatesStatusToEnabled() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombreCliente("Cliente Inactivo");
        cliente.setDisabled(true);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        service.rehabilitarCliente(1);

        assertThat(cliente.getDisabled()).isFalse();
        verify(clienteRepository).save(cliente);
        verify(auditLogService).log(eq("MODIFICAR"), eq("CLIENTES"), eq("1"), any(String.class), eq(cliente));
    }

    @Test
    void rehabilitarCliente_notFound_throwsException() {
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rehabilitarCliente(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente con ID 1 no encontrado");
    }

    @Test
    void deleteCliente_updatesStatusToDisabled() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombreCliente("Cliente Activo");
        cliente.setDisabled(false);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        service.deleteCliente(1);

        assertThat(cliente.getDisabled()).isTrue();
        verify(clienteRepository).save(cliente);
        verify(auditLogService).log(eq("DESHABILITAR"), eq("CLIENTES"), eq("1"), any(String.class), eq(cliente));
    }

    @Test
    void deleteCliente_notFound_throwsException() {
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCliente(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente con ID 1 no encontrado");
    }

    @Test
    void getClienteById_returnsDto() {
        Cliente cliente = new Cliente();
        cliente.setId(5);
        cliente.setNombreCliente("Cliente Cinco");

        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));

        ClienteDTO result = service.getClienteById(5);

        assertThat(result).isNotNull();
        assertThat(result.getIdCliente()).isEqualTo(5);
        assertThat(result.getNombreCliente()).isEqualTo("Cliente Cinco");
    }

    @Test
    void updateCliente_updatesAndSaves() {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(1);
        dto.setIdTipoCliente(3);
        dto.setNombreCliente("Cliente Modificado");

        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setId(3);
        tipoCliente.setNombreTipo("Estudiante");

        Cliente entity = new Cliente();
        entity.setId(1);
        entity.setNombreCliente("Cliente Original");

        when(utilsService.getTipoClienteById(3)).thenReturn(tipoCliente);
        when(clienteRepository.findById(1)).thenReturn(Optional.of(entity));
        when(clienteRepository.save(entity)).thenReturn(entity);

        ClienteDTO result = service.updateCliente(dto);

        assertThat(result).isNotNull();
        assertThat(result.getIdCliente()).isEqualTo(1);
        verify(clientesMapper).updateEntityFromDto(dto, entity, tipoCliente);
        verify(folderService).crearCarpetaCliente("Cliente Original");
        verify(auditLogService).log(eq("MODIFICAR"), eq("CLIENTES"), eq("1"), any(String.class), eq(entity));
    }
}
