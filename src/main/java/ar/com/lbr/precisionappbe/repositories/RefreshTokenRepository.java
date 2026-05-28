package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.RefreshToken;
import ar.com.lbr.precisionappbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
