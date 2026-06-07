package ar.com.lbr.precisionappbe.config;

import ar.com.lbr.precisionappbe.model.Role;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("leandror").isEmpty()) {
            log.info("Creando usuario Super Administrador por primera vez...");
            User superAdmin = User.builder()
                    .username("leandror")
                    .password(passwordEncoder.encode("Escaramujo;01"))
                    .role(Role.SUPER_ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(superAdmin);
            log.info("Usuario Super Administrador 'leandror' creado con éxito.");
        } else {
            // Si el usuario existe pero no tiene rol de SUPER_ADMIN por algún motivo heredado, lo aseguramos
            userRepository.findByUsername("leandror").ifPresent(user -> {
                if (user.getRole() != Role.SUPER_ADMIN) {
                    log.info("Actualizando rol de 'leandror' a SUPER_ADMIN...");
                    user.setRole(Role.SUPER_ADMIN);
                    userRepository.save(user);
                }
            });
        }
    }
}
