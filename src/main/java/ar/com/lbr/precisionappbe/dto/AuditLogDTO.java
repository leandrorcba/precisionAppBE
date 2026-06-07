package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {

    private Integer id;
    private Instant fechaHora;
    private String usuario;
    private String accion;
    private String modulo;
    private String registroId;
    private String detalles;

    public AuditLogDTO(AuditLog entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.fechaHora = entity.getFechaHora();
            this.usuario = entity.getUsuario();
            this.accion = entity.getAccion();
            this.modulo = entity.getModulo();
            this.registroId = entity.getRegistroId();
            this.detalles = entity.getDetalles();
        }
    }

    public static AuditLogDTO toDTO(AuditLog entity) {
        if (entity == null) {
            return null;
        }
        return new AuditLogDTO(entity);
    }
}
