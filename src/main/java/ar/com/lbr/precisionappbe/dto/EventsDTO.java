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

    private Integer id;
    private Integer calendarId;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private String title;
    private String maquina;

    public EventsDTO(Event event) {
       this.id = event.getId();
       this.calendarId  = event.getIdMaquina().getId();
       this.endDate = event.getEndDate();
       this.startDate = event.getStartDate();
       this.title = event.getEventName();
       this.maquina = event.getIdMaquina().getNombreMaquina();
    }

    public static EventsDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        return new EventsDTO(event);
    }
}
