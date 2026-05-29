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
@Table(name = "tipo_pago")
public class TipoPago {
    @Id
    @Column(name = "id_tipo_pago", nullable = false)
    private Integer id;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

}