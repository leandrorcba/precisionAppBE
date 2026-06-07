package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.UserDTO;
import ar.com.lbr.precisionappbe.model.Role;
import ar.com.lbr.precisionappbe.model.User;
import ar.com.lbr.precisionappbe.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    public List<UserDTO> getAllUsers() {
        // Solo Super Administradores y Administradores pueden ver la lista
        User currentUser = getCurrentUser();
        if (currentUser == null || (currentUser.getRole() != Role.SUPER_ADMIN && currentUser.getRole() != Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para ver los usuarios");
        }
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Integer id) {
        User currentUser = getCurrentUser();
        if (currentUser == null || (currentUser.getRole() != Role.SUPER_ADMIN && currentUser.getRole() != Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para ver este usuario");
        }
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public UserDTO getUserByUsername(String username) {
        User currentUser = getCurrentUser();
        if (currentUser == null || (currentUser.getRole() != Role.SUPER_ADMIN && currentUser.getRole() != Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para ver este usuario");
        }
        return userRepository.findByUsername(username)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        // Validaciones de creación según rol
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getRole() == Role.SUPER_ADMIN) {
                // SUPER_ADMIN puede crear ADMIN y USER. No puede crear otro SUPER_ADMIN
                if (userDTO.getRole() == Role.SUPER_ADMIN) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede crear otro Super Administrador");
                }
            } else if (currentUser.getRole() == Role.ADMIN) {
                // ADMIN puede crear únicamente USER
                if (userDTO.getRole() != Role.USER) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un Administrador solo puede crear Usuarios");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para crear usuarios");
            }
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(userDTO.getRole())
                .allowedMenus(userDTO.getAllowedMenus())
                .enabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        auditLogService.log("CREAR", "PARAMETROS", savedUser.getId().toString(), 
                "Se creó el usuario '" + savedUser.getUsername() + "' con rol: " + savedUser.getRole());
        return mapToDTO(savedUser);
    }

    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        User currentUser = getCurrentUser();
        if (currentUser != null) {
            // No se puede modificar al SUPER_ADMIN (leandror) si no es él mismo
            if (targetUser.getRole() == Role.SUPER_ADMIN && !currentUser.getId().equals(targetUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede modificar al Super Administrador");
            }

            if (currentUser.getRole() == Role.SUPER_ADMIN) {
                // Super Admin puede cambiar roles excepto degradar su propio rol
                if (targetUser.getId().equals(currentUser.getId()) && userDTO.getRole() != Role.SUPER_ADMIN) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede cambiar el rol del Super Administrador");
                }
            } else if (currentUser.getRole() == Role.ADMIN) {
                // Admin no puede cambiar roles
                if (userDTO.getRole() != null && userDTO.getRole() != targetUser.getRole()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un Administrador no puede cambiar los roles de los usuarios");
                }
                // Admin solo puede modificar usuarios de rol USER o ADMIN
                if (targetUser.getRole() == Role.SUPER_ADMIN) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para modificar al Super Administrador");
                }
                // Admin no puede modificar menús de otros administradores
                if (targetUser.getRole() == Role.ADMIN && userDTO.getAllowedMenus() != null && !userDTO.getAllowedMenus().equals(targetUser.getAllowedMenus())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un Administrador no puede cambiar los menús de otro Administrador");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para modificar usuarios");
            }
        }

        targetUser.setUsername(userDTO.getUsername());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            targetUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getRole() != null) {
            targetUser.setRole(userDTO.getRole());
        }
        targetUser.setAllowedMenus(userDTO.getAllowedMenus());
        if (userDTO.getEnabled() != null) {
            targetUser.setEnabled(userDTO.getEnabled());
        }

        User updatedUser = userRepository.save(targetUser);
        auditLogService.log("MODIFICAR", "PARAMETROS", updatedUser.getId().toString(), 
                "Se modificó el usuario '" + updatedUser.getUsername() + "' (Rol: " + updatedUser.getRole() + ")");
        return mapToDTO(updatedUser);
    }

    public void changePassword(Integer targetUserId, String newPassword) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // SA puede cambiar todos los pass
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            targetUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(targetUser);
            auditLogService.log("MODIFICAR", "PARAMETROS", targetUser.getId().toString(), 
                "Super Administrador cambió la contraseña del usuario: " + targetUser.getUsername());
            return;
        }

        // Administradores los de los usuarios o de los otros administradores siempre y cuando este logueado
        if (currentUser.getRole() == Role.ADMIN) {
            if (targetUser.getRole() == Role.SUPER_ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un Administrador no puede cambiar la contraseña del Super Administrador");
            }
            targetUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(targetUser);
            auditLogService.log("MODIFICAR", "PARAMETROS", targetUser.getId().toString(), 
                "Administrador cambió la contraseña del usuario: " + targetUser.getUsername());
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para cambiar contraseñas");
    }

    public void deleteUser(Integer id) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        User currentUser = getCurrentUser();
        if (currentUser != null) {
            if (targetUser.getRole() == Role.SUPER_ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede eliminar al Super Administrador");
            }

            if (currentUser.getRole() != Role.SUPER_ADMIN && currentUser.getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para eliminar usuarios");
            }
        }

        userRepository.deleteById(id);
        auditLogService.log("ELIMINAR", "PARAMETROS", id.toString(), 
                "Se eliminó el usuario: " + targetUser.getUsername());
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .allowedMenus(user.getAllowedMenus())
                .build();
    }
}
