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
@Table(name = "tipo_descuento")
public class TipoDescuento {
    @Id
    @Column(name = "id_tipo_descuento", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

}