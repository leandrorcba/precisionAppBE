package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDTO {

    private String eventName;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String details;
    private Integer duracion;
    private String status;
    private Integer calendarId;
    private String notas;
}