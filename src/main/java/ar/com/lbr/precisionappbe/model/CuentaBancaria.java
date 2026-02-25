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

@Getter
@Setter
@Entity
@Table(name = "cuenta_bancaria", schema = "precision_schema_v2")
public class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta_bancaria", nullable = false)
    private Integer id;

    @Column(name = "banco", nullable = false, length = 100)
    private String banco;

    @Column(name = "alias_cbu", length = 30)
    private String aliasCbu;

    @Column(name = "cbu", length = 22)
    private String cbu;

    @Column(name = "numero_cuenta", length = 34)
    private String numeroCuenta;

    @ColumnDefault("'ARS'")
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @ColumnDefault("1")
    @Column(name = "habilitada", nullable = false)
    private Boolean habilitada = false;

}