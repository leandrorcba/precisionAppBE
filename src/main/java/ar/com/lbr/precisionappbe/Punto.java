package ar.com.lbr.precisionappbe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "puntos", schema = "precision_schema_v2")
public class Punto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puntos", nullable = false)
    private Integer id;

    @Column(name = "fecha_primer_punto")
    private LocalDate fechaPrimerPunto;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @NotNull
    @Column(name = "puntos_acumulados", nullable = false)
    private Integer puntosAcumulados;

    @NotNull
    @Column(name = "puntos_acumulados_historico", nullable = false)
    private Integer puntosAcumuladosHistorico;

}