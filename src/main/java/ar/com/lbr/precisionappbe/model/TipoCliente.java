package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_cliente")
public class TipoCliente {
    @Id
    @Column(name = "id_tipo_cliente", nullable = false)
    private Integer id;

    @Column(name = "nombre_tipo", nullable = false, length = 100)
    private String nombreTipo;

}