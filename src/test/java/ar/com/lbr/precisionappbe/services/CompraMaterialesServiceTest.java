package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CompraMaterialesDTO;
import ar.com.lbr.precisionappbe.model.CompraMateriale;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.CompraMaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraMaterialesServiceTest {

    @Mock
    private CompraMaterialeRepository compraMaterialeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    private CompraMaterialesService service;

    @BeforeEach
    void setUp() {
        service = new CompraMaterialesService(compraMaterialeRepository, userRepository, auditLogService);
    }

    @Test
    void getAllCompras_noFilters_returnsAllOrderedDesc() {
        CompraMateriale c1 = new CompraMateriale();
        c1.setId(1);
        c1.setIdUser(10);
        c1.setFechaHoraCompra(Instant.parse("2026-06-06T10:00:00Z"));
        c1.setMaterial("Polyfan");

        CompraMateriale c2 = new CompraMateriale();
        c2.setId(2);
        c2.setIdUser(10);
        c2.setFechaHoraCompra(Instant.parse("2026-06-06T12:00:00Z"));
        c2.setMaterial("Mdf comun");

        User user = new User();
        user.setId(10);
        user.setUsername("operario1");

        when(compraMaterialeRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(c1, c2));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<CompraMaterialesDTO> result = service.getAllCompras(null, null, null);

        assertThat(result).hasSize(2);
        // Verify sorting is Descending by date (c2 first)
        assertThat(result.get(0).getId()).isEqualTo(2);
        assertThat(result.get(0).getUsername()).isEqualTo("operario1");
        assertThat(result.get(1).getId()).isEqualTo(1);
        assertThat(result.get(1).getUsername()).isEqualTo("operario1");
    }

    @Test
    void createCompra_validInput_savesAndCalculatesTotal() {
        User user = new User();
        user.setId(5);
        user.setUsername("admin");

        CompraMaterialesDTO inputDto = new CompraMaterialesDTO();
        inputDto.setMaterial("Acrilico");
        inputDto.setMontoUnitario(new BigDecimal("150.00"));
        inputDto.setCantidad(4);

        CompraMateriale savedEntity = new CompraMateriale();
        savedEntity.setId(100);
        savedEntity.setMaterial("Acrilico");
        savedEntity.setMontoUnitario(new BigDecimal("150.00"));
        savedEntity.setCantidad(4);
        savedEntity.setMontoTotal(new BigDecimal("600.00"));
        savedEntity.setFechaHoraCompra(Instant.now());
        savedEntity.setIdUser(5);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(compraMaterialeRepository.save(any(CompraMateriale.class))).thenReturn(savedEntity);

        CompraMaterialesDTO result = service.createCompra(inputDto, "admin");

        assertThat(result.getId()).isEqualTo(100);
        assertThat(result.getMontoTotal()).isEqualByComparingTo("600.00");
        assertThat(result.getUsername()).isEqualTo("admin");

        ArgumentCaptor<CompraMateriale> captor = ArgumentCaptor.forClass(CompraMateriale.class);
        verify(compraMaterialeRepository).save(captor.capture());
        CompraMateriale captured = captor.getValue();
        assertThat(captured.getMontoTotal()).isEqualByComparingTo("600.00");
        assertThat(captured.getIdUser()).isEqualTo(5);
        assertThat(captured.getIdMateriales()).isNull();
        assertThat(captured.getTipo()).isNull();
        assertThat(captured.getCaja()).isNull();
    }

    @Test
    void deleteCompra_existingId_deletesSuccessfully() {
        CompraMateriale entity = new CompraMateriale();
        entity.setId(15);
        when(compraMaterialeRepository.findById(15)).thenReturn(Optional.of(entity));

        service.deleteCompra(15);

        verify(compraMaterialeRepository).delete(entity);
    }

    @Test
    void deleteCompra_nonExistingId_throwsException() {
        when(compraMaterialeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCompra(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Compra de material no encontrada");
    }
}
