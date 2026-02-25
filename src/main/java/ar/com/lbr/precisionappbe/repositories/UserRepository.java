package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
