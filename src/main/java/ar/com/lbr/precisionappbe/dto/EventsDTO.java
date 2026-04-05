package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsDTO {

    private String notas;
    private Integer id;
    private Integer calendarId;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private String descripcion;
    private String maquina;
    private String status;
    private String tipoDeTrabajo;

    public EventsDTO(Event event) {
        this.id = event.getId();
        this.calendarId = event.getIdMaquina().getId();
        this.endDate = event.getEndDate();
        this.startDate = event.getStartDate();
        this.descripcion = event.getEventName();
        this.maquina = event.getIdMaquina().getNombreMaquina();
        this.status = event.getStatus();
        this.tipoDeTrabajo = "Polyfan";
        this.notas = event.getNotas();
    }

    public static EventsDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        return new EventsDTO(event);
    }
}
