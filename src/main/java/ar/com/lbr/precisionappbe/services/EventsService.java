package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.dto.UpdateEventDTO;
import ar.com.lbr.precisionappbe.model.Event;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;
    private final MaquinasRepository maquinasRepository;

    public EventsService(EventsRepository eventsRepository, MaquinasRepository maquinasRepository) {
        this.eventsRepository = eventsRepository;
        this.maquinasRepository = maquinasRepository;
    }

    public List<EventsDTO> getAllEvents(Integer calendar, String startDateStr, String endDateStr) {
        LocalDateTime minStartDate = LocalDate.now().minusMonths(3).atStartOfDay();
        LocalDateTime startDate = startDateStr != null
                ? OffsetDateTime.parse(startDateStr).toLocalDateTime()
                : minStartDate;
        LocalDateTime endDate = endDateStr != null
                ? OffsetDateTime.parse(endDateStr).toLocalDateTime()
                : LocalDateTime.now();

        return eventsRepository.findByFilters(calendar, startDate, endDate).stream()
                .map(EventsDTO::toDTO)
                .collect(Collectors.toList());
    }

    public EventsDTO updateEvent(Integer id, UpdateEventDTO dto) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));

        if (dto.getEventName() != null) {
            event.setEventName(dto.getEventName());
        }
        if (dto.getStartDate() != null) {
            event.setStartDate(dto.getStartDate().toLocalDateTime());
        }
        if (dto.getEndDate() != null) {
            event.setEndDate(dto.getEndDate().toLocalDateTime());
        }
        if (dto.getDetails() != null) {
            event.setDetails(dto.getDetails());
        }
        if (dto.getDuracion() != null) {
            event.setDuracion(dto.getDuracion());
        }
        if (dto.getStatus() != null) {
            event.setStatus(dto.getStatus());
        }
        if (dto.getNotas() != null) {
            event.setNotas(dto.getNotas());
        }
        if (dto.getCalendarId() != null) {
            Maquina maquina = maquinasRepository.findById(dto.getCalendarId())
                    .orElseThrow(() -> new EntityNotFoundException("Maquina not found: " + dto.getCalendarId()));
            event.setIdMaquina(maquina);
        }

        return EventsDTO.toDTO(eventsRepository.save(event));
    }
}
