package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.AuthRequest;
import ar.com.lbr.precisionappbe.dto.response.AuthResponse;
import ar.com.lbr.precisionappbe.model.RefreshToken;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import ar.com.lbr.precisionappbe.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationService(repository, passwordEncoder, jwtService, authenticationManager, refreshTokenService);
    }

    @Test
    void register_withNewUsername_registersUserSuccessfully() {
        AuthRequest request = new AuthRequest("newUser", "password123");
        User savedUser = User.builder().id(1).username("newUser").build();
        RefreshToken refreshToken = RefreshToken.builder().token("refresh-token-uuid").build();

        when(repository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token-jwt");
        when(refreshTokenService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

        AuthResponse response = service.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token-jwt");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-uuid");
        verify(repository).save(any(User.class));
    }

    @Test
    void register_withExistingUsername_throwsRuntimeException() {
        AuthRequest request = new AuthRequest("existingUser", "password123");
        User existingUser = User.builder().id(1).username("existingUser").build();

        when(repository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El nombre de usuario ya existe.");
    }

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        AuthRequest request = new AuthRequest("loginUser", "password123");
        User user = User.builder().id(2).username("loginUser").build();
        RefreshToken refreshToken = RefreshToken.builder().token("refresh-token-uuid").build();

        when(repository.findByUsername("loginUser")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token-jwt");
        when(refreshTokenService.generateRefreshToken(user)).thenReturn(refreshToken);

        AuthResponse response = service.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token-jwt");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-uuid");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refresh_withValidToken_returnsNewAccessToken() {
        User user = User.builder().id(3).username("refreshUser").build();
        RefreshToken refreshToken = RefreshToken.builder().token("refresh-token-uuid").user(user).build();

        when(refreshTokenService.validateRefreshToken("refresh-token-uuid")).thenReturn(refreshToken);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token-jwt");

        AuthResponse response = service.refresh("refresh-token-uuid");

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-access-token-jwt");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-uuid");
    }

    @Test
    void logout_withValidToken_deletesRefreshToken() {
        User user = User.builder().id(4).username("logoutUser").build();
        RefreshToken refreshToken = RefreshToken.builder().token("refresh-token-uuid").user(user).build();

        when(refreshTokenService.validateRefreshToken("refresh-token-uuid")).thenReturn(refreshToken);

        service.logout("refresh-token-uuid");

        verify(refreshTokenService).deleteByUser(user);
    }
}
