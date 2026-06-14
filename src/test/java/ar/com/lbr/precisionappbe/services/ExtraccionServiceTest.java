package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ExtraccionDTO;
import ar.com.lbr.precisionappbe.model.Extraccione;
import ar.com.lbr.precisionappbe.repositories.ExtraccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtraccionServiceTest {

    @Mock
    private ExtraccionRepository extraccionRepository;

    @Mock
    private AuditLogService auditLogService;

    private ExtraccionService service;

    @BeforeEach
    void setUp() {
        service = new ExtraccionService(extraccionRepository, auditLogService);
    }

    @Test
    void getAllExtracciones_withDates_queriesRepository() {
        LocalDate desde = LocalDate.of(2026, 6, 1);
        LocalDate hasta = LocalDate.of(2026, 6, 5);

        Extraccione ex = new Extraccione();
        ex.setId(1);
        ex.setMontoExtraccion(BigDecimal.valueOf(100));

        when(extraccionRepository.findByFechaExtraccionBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(ex));

        List<ExtraccionDTO> result = service.getAllExtracciones(desde, hasta);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMontoExtraccion()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void createExtraccion_savesAndLogs() {
        ExtraccionDTO dto = new ExtraccionDTO();
        dto.setIdUsuario(2);
        dto.setMontoExtraccion(BigDecimal.valueOf(150));
        dto.setMotivoExtraccion("Artículos de limpieza");

        Extraccione saved = new Extraccione();
        saved.setId(10);
        saved.setIdUsuario(2);
        saved.setMontoExtraccion(BigDecimal.valueOf(150));
        saved.setMotivoExtraccion("Artículos de limpieza");
        saved.setFechaExtraccion(Instant.now());

        when(extraccionRepository.save(any(Extraccione.class))).thenReturn(saved);

        ExtraccionDTO result = service.createExtraccion(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        verify(auditLogService).log(eq("CREAR"), eq("EXTRACCIONES"), eq("10"), any(String.class), any(ExtraccionDTO.class));
    }

    @Test
    void updateExtraccion_existing_updatesAndLogs() {
        Extraccione ex = new Extraccione();
        ex.setId(5);
        ex.setMontoExtraccion(BigDecimal.valueOf(100));

        ExtraccionDTO dto = new ExtraccionDTO();
        dto.setMontoExtraccion(BigDecimal.valueOf(120));
        dto.setIdUsuario(1);

        when(extraccionRepository.findById(5)).thenReturn(Optional.of(ex));
        when(extraccionRepository.save(ex)).thenReturn(ex);

        ExtraccionDTO result = service.updateExtraccion(5, dto);

        assertThat(result).isNotNull();
        assertThat(ex.getMontoExtraccion()).isEqualTo(BigDecimal.valueOf(120));
        verify(auditLogService).log(eq("MODIFICAR"), eq("EXTRACCIONES"), eq("5"), any(String.class), any(ExtraccionDTO.class));
    }

    @Test
    void updateExtraccion_nonExisting_throwsRuntimeException() {
        when(extraccionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateExtraccion(99, new ExtraccionDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Extracción no encontrada");
    }

    @Test
    void deleteExtraccion_existing_deletesAndLogs() {
        Extraccione ex = new Extraccione();
        ex.setId(7);
        ex.setMontoExtraccion(BigDecimal.valueOf(80));

        when(extraccionRepository.findById(7)).thenReturn(Optional.of(ex));

        service.deleteExtraccion(7);

        verify(extraccionRepository).delete(ex);
        verify(auditLogService).log(eq("ELIMINAR"), eq("EXTRACCIONES"), eq("7"), any(String.class), any(ExtraccionDTO.class));
    }

    @Test
    void deleteExtraccion_nonExisting_throwsRuntimeException() {
        when(extraccionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteExtraccion(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Extracción no encontrada");
    }
}
