package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsDTO {

    private String notas;
    private Integer id;
    private Integer calendarId;
    private Instant endDate;
    private Instant startDate;
    private String descripcion;
    private String maquina;
    private String status;
    private String tipoDeTrabajo;
    private Integer idTrabajo;
    private Integer idPresupuesto;
    private String archivo;

    public EventsDTO(Event event) {
        this.id = event.getId();
        this.calendarId = event.getIdMaquina().getId();
        this.endDate = event.getEndDate();
        this.startDate = event.getStartDate();
        this.descripcion = event.getEventName();
        this.maquina = event.getIdMaquina().getNombreMaquina();
        this.status = event.getStatus();
        this.tipoDeTrabajo = null;
        this.notas = event.getNotas();
        this.idTrabajo = event.getIdTrabajo();
        this.idPresupuesto = event.getIdPresupuesto();
        this.archivo = null;
    }

    public static EventsDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        return new EventsDTO(event);
    }
}
