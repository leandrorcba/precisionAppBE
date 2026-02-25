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

    private Integer id;
    private Integer calendarId;
    private Instant endDate;
    private Instant startDate;
    private String title;

    public EventsDTO(Event event) {
       this.id = event.getId();
       this.calendarId  = event.getIdMaquina().getId();
       this.endDate = event.getEndDate();
       this.startDate = event.getStartDate();
       this.title = event.getEventName();
    }

    public static EventsDTO toDTO(Event event) {
        if (event == null)
            return null;
        return new EventsDTO(event);
    }
}
