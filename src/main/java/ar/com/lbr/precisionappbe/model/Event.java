package ar.com.lbr.precisionappbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Integer id;

    @Column(name = "event_name", nullable = false, length = 127)
    private String eventName;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(name = "details")
    private String details;

    @Column(name = "duracion")
    private Integer duracion;

    @Column(name = "idPresupuesto")
    private Integer idPresupuesto;

    @Column(name = "idTrabajo")
    private Integer idTrabajo;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_maquina", nullable = false)
    private Maquina idMaquina;

    @Column(name = "notas", length = 255)
    private String notas;

}