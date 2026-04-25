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
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "mercado_pago", schema = "precision_schema_v2")
public class MercadoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mercado_pago", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "titular", nullable = false, length = 100)
    private String titular;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "disabled", nullable = false)
    private Boolean disabled = false;

}