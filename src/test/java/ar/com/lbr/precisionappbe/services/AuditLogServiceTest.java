package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.AuditLog;
import ar.com.lbr.precisionappbe.repositories.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private AuditLogService service;

    @BeforeEach
    void setUp() {
        service = new AuditLogService(auditLogRepository, new com.fasterxml.jackson.databind.ObjectMapper());
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void log_withAuthenticatedUser_savesLogWithUsername() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test-user");

        service.log("CREAR", "CLIENTES", "12", "Detalle de prueba");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog captured = captor.getValue();

        assertThat(captured.getUsuario()).isEqualTo("test-user");
        assertThat(captured.getAccion()).isEqualTo("CREAR");
        assertThat(captured.getModulo()).isEqualTo("CLIENTES");
        assertThat(captured.getRegistroId()).isEqualTo("12");
        assertThat(captured.getDetalles()).isEqualTo("Detalle de prueba");
        assertThat(captured.getFechaHora()).isNotNull();
    }

    @Test
    void log_withoutAuthentication_savesLogWithSystem() {
        when(securityContext.getAuthentication()).thenReturn(null);

        service.log("MODIFICAR", "PRESUPUESTOS", "34", "Detalle presupuesto");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog captured = captor.getValue();

        assertThat(captured.getUsuario()).isEqualTo("SYSTEM");
        assertThat(captured.getAccion()).isEqualTo("MODIFICAR");
        assertThat(captured.getModulo()).isEqualTo("PRESUPUESTOS");
        assertThat(captured.getRegistroId()).isEqualTo("34");
        assertThat(captured.getDetalles()).isEqualTo("Detalle presupuesto");
    }

    @Test
    void getUniqueUsers_returnsListFromRepository() {
        List<String> mockUsers = Arrays.asList("admin", "operario1", "SYSTEM");
        when(auditLogRepository.findDistinctUsuarios()).thenReturn(mockUsers);

        List<String> result = service.getUniqueUsers();

        assertThat(result).containsExactlyInAnyOrder("admin", "operario1", "SYSTEM");
        verify(auditLogRepository).findDistinctUsuarios();
    }
}
