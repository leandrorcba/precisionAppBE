package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.model.VariosHistorial;
import ar.com.lbr.precisionappbe.repositories.VariosHistorialRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariosServiceTest {

    @Mock
    private VariosRepository variosRepository;
    @Mock
    private VariosHistorialRepository variosHistorialRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private VariosService service;

    @BeforeEach
    void setUp() {
        service = new VariosService(variosRepository, variosHistorialRepository, auditLogService);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getVarios_existing_returnsDto() {
        Varios v = new Varios();
        v.setId(1);
        v.setPrecioMinuto(BigDecimal.valueOf(10));

        when(variosRepository.findAll()).thenReturn(List.of(v));

        VariosDTO result = service.getVarios();

        assertThat(result).isNotNull();
        assertThat(result.getPrecioMinuto()).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void getVarios_nonExisting_returnsNull() {
        when(variosRepository.findAll()).thenReturn(Collections.emptyList());

        VariosDTO result = service.getVarios();

        assertThat(result).isNull();
    }

    @Test
    void updateVarios_withAuthenticatedUser_savesVariosAndHistorial() {
        Varios existing = new Varios();
        existing.setId(1);

        VariosDTO dto = new VariosDTO();
        dto.setPrecioMinuto(BigDecimal.valueOf(15));
        dto.setAjuste(10);
        dto.setDescuentoEfectivo(5);
        dto.setHoraInicio(LocalTime.of(8, 0));
        dto.setHoraCierre(LocalTime.of(18, 0));

        User user = new User();
        user.setId(5);
        user.setUsername("admin");

        when(variosRepository.findAll()).thenReturn(List.of(existing));
        when(variosRepository.save(existing)).thenReturn(existing);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        VariosDTO result = service.updateVarios(dto);

        assertThat(result).isNotNull();
        assertThat(existing.getPrecioMinuto()).isEqualTo(BigDecimal.valueOf(15));

        verify(auditLogService).log(eq("CONFIGURAR"), eq("PARAMETROS"), eq("1"), any(String.class));

        ArgumentCaptor<VariosHistorial> captor = ArgumentCaptor.forClass(VariosHistorial.class);
        verify(variosHistorialRepository).save(captor.capture());
        VariosHistorial savedHistorial = captor.getValue();

        assertThat(savedHistorial.getUser()).isEqualTo(user);
        assertThat(savedHistorial.getPrecioMinuto()).isEqualTo(BigDecimal.valueOf(15));
    }

    @Test
    void updateVarios_noAuthentication_savesVariosAndHistorialWithoutUser() {
        Varios existing = new Varios();
        existing.setId(1);

        VariosDTO dto = new VariosDTO();
        dto.setPrecioMinuto(BigDecimal.valueOf(15));

        when(variosRepository.findAll()).thenReturn(List.of(existing));
        when(variosRepository.save(existing)).thenReturn(existing);
        when(securityContext.getAuthentication()).thenReturn(null);

        VariosDTO result = service.updateVarios(dto);

        assertThat(result).isNotNull();

        ArgumentCaptor<VariosHistorial> captor = ArgumentCaptor.forClass(VariosHistorial.class);
        verify(variosHistorialRepository).save(captor.capture());
        VariosHistorial savedHistorial = captor.getValue();

        assertThat(savedHistorial.getUser()).isNull();
    }
}
