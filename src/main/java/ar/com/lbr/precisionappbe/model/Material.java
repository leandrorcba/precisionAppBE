package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "materiales", schema = "precision_schema_v2")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materiales", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "materiales", length = 45)
    private String materiales;

    @ColumnDefault("0")
    @Column(name = "is_material")
    private Boolean isMaterial;

    @ColumnDefault("0")
    @Column(name = "is_grabado")
    private Boolean isGrabado;

    @ColumnDefault("false")
    @Column(name = "disabled", nullable = false)
    private Boolean disabled = false;

}