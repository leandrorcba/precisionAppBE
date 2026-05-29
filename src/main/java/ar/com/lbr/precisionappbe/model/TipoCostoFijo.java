package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "tipo_costo_fijo")
public class TipoCostoFijo {
    @Id
    @Column(name = "id_tipo_costo_fijo", nullable = false)
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

}