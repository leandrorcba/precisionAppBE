package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.UserDTO;
import ar.com.lbr.precisionappbe.model.Role;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, auditLogService);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockCurrentUser(String username, Role role) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);

        User currentUser = User.builder().id(1).username(username).role(role).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(currentUser));
    }

    @Test
    void createUser_asSuperAdmin_canCreateAdminAndUser() {
        mockCurrentUser("super-admin", Role.SUPER_ADMIN);

        UserDTO adminDTO = UserDTO.builder().username("new-admin").password("pass12345").role(Role.ADMIN).build();
        when(passwordEncoder.encode("pass12345")).thenReturn("encoded-pass");
        
        User savedAdmin = User.builder().id(2).username("new-admin").role(Role.ADMIN).build();
        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        UserDTO result = userService.createUser(adminDTO);

        assertThat(result.getUsername()).isEqualTo("new-admin");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).save(any(User.class));
        verify(auditLogService).log(eq("CREAR"), eq("PARAMETROS"), anyString(), anyString());
    }

    @Test
    void createUser_asSuperAdmin_cannotCreateSuperAdmin() {
        mockCurrentUser("super-admin", Role.SUPER_ADMIN);

        UserDTO saDTO = UserDTO.builder().username("new-sa").password("pass12345").role(Role.SUPER_ADMIN).build();

        assertThatThrownBy(() -> userService.createUser(saDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(rse.getReason()).isEqualTo("No se puede crear otro Super Administrador");
                });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_asAdmin_canCreateUser() {
        mockCurrentUser("admin", Role.ADMIN);

        UserDTO userDTO = UserDTO.builder().username("new-user").password("pass12345").role(Role.USER).build();
        when(passwordEncoder.encode("pass12345")).thenReturn("encoded-pass");
        
        User savedUser = User.builder().id(3).username("new-user").role(Role.USER).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.createUser(userDTO);

        assertThat(result.getUsername()).isEqualTo("new-user");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_asAdmin_cannotCreateAdminOrSuperAdmin() {
        mockCurrentUser("admin", Role.ADMIN);

        UserDTO adminDTO = UserDTO.builder().username("new-admin").password("pass12345").role(Role.ADMIN).build();

        assertThatThrownBy(() -> userService.createUser(adminDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(rse.getReason()).isEqualTo("Un Administrador solo puede crear Usuarios");
                });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_asSuperAdmin_canChangeAnyPassword() {
        mockCurrentUser("super-admin", Role.SUPER_ADMIN);

        User targetUser = User.builder().id(10).username("some-user").role(Role.USER).build();
        when(userRepository.findById(10)).thenReturn(Optional.of(targetUser));
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");

        userService.changePassword(10, "new-pass");

        assertThat(targetUser.getPassword()).isEqualTo("encoded-new-pass");
        verify(userRepository).save(targetUser);
        verify(auditLogService).log(eq("MODIFICAR"), eq("PARAMETROS"), eq("10"), anyString());
    }

    @Test
    void changePassword_asAdmin_cannotChangeSuperAdminPassword() {
        mockCurrentUser("admin", Role.ADMIN);

        User targetUser = User.builder().id(10).username("leandror").role(Role.SUPER_ADMIN).build();
        when(userRepository.findById(10)).thenReturn(Optional.of(targetUser));

        assertThatThrownBy(() -> userService.changePassword(10, "new-pass"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(rse.getReason()).isEqualTo("Un Administrador no puede cambiar la contraseña del Super Administrador");
                });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_asAdmin_canChangeUserOrAdminPassword() {
        mockCurrentUser("admin", Role.ADMIN);

        User targetUser = User.builder().id(10).username("other-admin").role(Role.ADMIN).build();
        when(userRepository.findById(10)).thenReturn(Optional.of(targetUser));
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");

        userService.changePassword(10, "new-pass");

        assertThat(targetUser.getPassword()).isEqualTo("encoded-new-pass");
        verify(userRepository).save(targetUser);
    }
}
