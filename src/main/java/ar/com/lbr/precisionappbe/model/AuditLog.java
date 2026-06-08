package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit_log", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @NotNull
    @Size(max = 100)
    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario;

    @NotNull
    @Size(max = 50)
    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @NotNull
    @Size(max = 50)
    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @Size(max = 50)
    @Column(name = "registro_id", length = 50)
    private String registroId;

    @Size(max = 255)
    @Column(name = "uri", length = 255)
    private String uri;

    @Column(name = "json", columnDefinition = "LONGTEXT")
    private String json;

    @NotNull
    @Column(name = "detalles", nullable = false, columnDefinition = "TEXT")
    private String detalles;
}
