package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.RefreshToken;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(refreshTokenRepository);
        ReflectionTestUtils.setField(service, "refreshTokenExpiration", 3600000L); // 1 hora
    }

    @Test
    void generateRefreshToken_deletesOldAndSavesNew() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        RefreshToken token = RefreshToken.builder()
                .token("generated-uuid")
                .user(user)
                .expiresAt(Instant.now().plusMillis(3600000L))
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(token);

        RefreshToken result = service.generateRefreshToken(user);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("generated-uuid");
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void validateRefreshToken_validToken_returnsToken() {
        RefreshToken token = RefreshToken.builder()
                .token("valid-uuid")
                .expiresAt(Instant.now().plusSeconds(600))
                .build();

        when(refreshTokenRepository.findByToken("valid-uuid")).thenReturn(Optional.of(token));

        RefreshToken result = service.validateRefreshToken("valid-uuid");

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("valid-uuid");
    }

    @Test
    void validateRefreshToken_expiredToken_deletesAndThrowsUnauthorized() {
        RefreshToken token = RefreshToken.builder()
                .token("expired-uuid")
                .expiresAt(Instant.now().minusSeconds(10))
                .build();

        when(refreshTokenRepository.findByToken("expired-uuid")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> service.validateRefreshToken("expired-uuid"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token expirado");

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void validateRefreshToken_notFound_throwsUnauthorized() {
        when(refreshTokenRepository.findByToken("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateRefreshToken("non-existent"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token inválido");
    }

    @Test
    void deleteByUser_callsRepository() {
        User user = new User();
        service.deleteByUser(user);
        verify(refreshTokenRepository).deleteByUser(user);
    }
}
