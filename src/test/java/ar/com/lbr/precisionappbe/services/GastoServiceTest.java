package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.GastoDTO;
import ar.com.lbr.precisionappbe.model.Gasto;
import ar.com.lbr.precisionappbe.repositories.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private AuditLogService auditLogService;

    private GastoService service;

    @BeforeEach
    void setUp() {
        service = new GastoService(gastoRepository, auditLogService);
    }

    @Test
    void getAllGastos_returnsSortedList() {
        Gasto g1 = new Gasto();
        g1.setId(1);
        g1.setMontoGasto(BigDecimal.TEN);

        when(gastoRepository.findAll(any(Sort.class))).thenReturn(List.of(g1));

        List<GastoDTO> result = service.getAllGastos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMontoGasto()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void createGasto_savesAndLogs() {
        GastoDTO dto = new GastoDTO();
        dto.setMontoGasto(BigDecimal.valueOf(500));
        dto.setMotivoGasto("Alquiler");
        dto.setResponsableGasto("Admin");
        dto.setIdUsuario(1);

        Gasto saved = new Gasto();
        saved.setId(10);
        saved.setMontoGasto(BigDecimal.valueOf(500));
        saved.setMotivoGasto("Alquiler");
        saved.setResponsableGasto("Admin");
        saved.setIdUsuario(1);
        saved.setFechaGasto(Instant.now());

        when(gastoRepository.save(any(Gasto.class))).thenReturn(saved);

        GastoDTO result = service.createGasto(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        verify(auditLogService).log(eq("CREAR"), eq("GASTOS"), eq("10"), any(String.class), any(GastoDTO.class));
    }

    @Test
    void deleteGasto_existing_deletesAndLogs() {
        Gasto gasto = new Gasto();
        gasto.setId(2);
        gasto.setMontoGasto(BigDecimal.valueOf(100));
        gasto.setResponsableGasto("Juan");
        gasto.setMotivoGasto("Papelería");

        when(gastoRepository.findById(2)).thenReturn(Optional.of(gasto));

        service.deleteGasto(2);

        verify(gastoRepository).delete(gasto);
        verify(auditLogService).log(eq("ELIMINAR"), eq("GASTOS"), eq("2"), any(String.class), any(GastoDTO.class));
    }

    @Test
    void deleteGasto_nonExisting_throwsRuntimeException() {
        when(gastoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteGasto(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Gasto no encontrado con ID: 99");
    }
}
