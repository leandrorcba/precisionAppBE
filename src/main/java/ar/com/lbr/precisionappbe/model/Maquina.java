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

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "maquinas")
public class Maquina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maquina", nullable = false)
    private Integer id;

    @Column(name = "nombre_maquina", nullable = false, length = 100)
    private String nombreMaquina;

    @ColumnDefault("1")
    @Column(name = "habilitada", nullable = false)
    private Boolean habilitada = false;

    @ColumnDefault("(UTC_TIMESTAMP())")
    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

}