package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private Role role;
    private String password;
    private boolean enabled;
}
