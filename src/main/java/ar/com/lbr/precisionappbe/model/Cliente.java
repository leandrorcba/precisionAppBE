package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente", nullable = false)
    private Integer id;

    @Column(name = "dni_cliente", length = 50)
    private String dniCliente;

    @Column(name = "email_cliente", length = 100)
    private String emailCliente;

    @Column(name = "nombre_cliente", length = 200)
    private String nombreCliente;

    @Column(name = "telefono_cliente", length = 100)
    private String telefonoCliente;

    @Column(name = "precio_minuto_empresa", precision = 10, scale = 2)
    private BigDecimal precioMinutoEmpresa;

    @Column(name = "mora")
    private Boolean mora;

    @Column(name = "id_tipo_cliente")
    private Integer idTipoCliente;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @ColumnDefault("0")
    @Column(name = "disabled")
    private Boolean disabled;

}