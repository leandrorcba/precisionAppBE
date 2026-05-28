package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.dto.UpdateEventDTO;
import ar.com.lbr.precisionappbe.model.Event;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventsService {

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final EventsRepository eventsRepository;
    private final MaquinasRepository maquinasRepository;

    public EventsService(EventsRepository eventsRepository, MaquinasRepository maquinasRepository) {
        this.eventsRepository = eventsRepository;
        this.maquinasRepository = maquinasRepository;
    }

    public List<EventsDTO> getAllEvents(Integer calendar, String startDateStr, String endDateStr) {
        Instant minStartDate = LocalDate.now().minusMonths(3).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant startDate = startDateStr != null
                ? OffsetDateTime.parse(startDateStr).toInstant()
                : minStartDate;
        Instant endDate = endDateStr != null
                ? OffsetDateTime.parse(endDateStr).toInstant()
                : Instant.now();

        return eventsRepository.findByFilters(calendar, startDate, endDate).stream()
                .map(EventsDTO::toDTO)
                .collect(Collectors.toList());
    }

    public EventsDTO createEvent(UpdateEventDTO dto) {
        Maquina maquina = maquinasRepository.findById(dto.getCalendarId())
                .orElseThrow(() -> new EntityNotFoundException("Maquina not found: " + dto.getCalendarId()));

        Event event = new Event();
        event.setIdMaquina(maquina);
        event.setStartDate(dto.getStartDate().toInstant());
        event.setEndDate(dto.getEndDate().toInstant());
        event.setEventName(dto.getEventName() != null ? dto.getEventName() : "");
        event.setStatus(dto.getStatus());
        event.setNotas(dto.getNotas());
        event.setDetails(dto.getDetails());
        event.setDuracion(dto.getDuracion());

        return EventsDTO.toDTO(eventsRepository.save(event));
    }

    public EventsDTO updateEvent(Integer id, UpdateEventDTO dto) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));

        if (dto.getEventName() != null) {
            event.setEventName(dto.getEventName());
        }
        if (dto.getStartDate() != null) {
            event.setStartDate(dto.getStartDate().toInstant());
        }
        if (dto.getEndDate() != null) {
            event.setEndDate(dto.getEndDate().toInstant());
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

    public EventsDTO createEventForTrabajo(Integer idMaquina, Integer idPresupuesto, Integer idTrabajo,
                                           String eventName, int durationMinutes,
                                           LocalTime horaInicio, LocalTime horaCierre) {
        Maquina maquina = maquinasRepository.findById(idMaquina)
                .orElseThrow(() -> new EntityNotFoundException("Maquina not found: " + idMaquina));

        Instant slotStart = findFirstAvailableSlot(idMaquina, durationMinutes, horaInicio, horaCierre);
        Instant slotEnd = slotStart.plus(durationMinutes, ChronoUnit.MINUTES);

        String name = eventName != null && eventName.length() > 127 ? eventName.substring(0, 127) : eventName;

        Event event = new Event();
        event.setIdMaquina(maquina);
        event.setStartDate(slotStart);
        event.setEndDate(slotEnd);
        event.setEventName(name != null ? name : "");
        event.setIdPresupuesto(idPresupuesto);
        event.setIdTrabajo(idTrabajo);
        event.setDuracion(durationMinutes);
        event.setStatus("PENDIENTE");

        return EventsDTO.toDTO(eventsRepository.save(event));
    }

    public Instant findFirstAvailableSlot(Integer idMaquina, int durationMinutes,
                                          LocalTime horaInicio, LocalTime horaCierre) {
        Instant now = Instant.now();
        List<Event> events = eventsRepository
                .findByIdMaquinaIdAndStartDateGreaterThanEqualOrderByStartDate(idMaquina, now);

        ZonedDateTime candidate = nextWorkingSlotStart(now.atZone(ZONE), horaInicio, horaCierre);

        for (Event event : events) {
            ZonedDateTime eventStart = event.getStartDate().atZone(ZONE);
            ZonedDateTime slotEnd = candidate.plusMinutes(durationMinutes);

            if (!slotEnd.isAfter(eventStart)) {
                return candidate.toInstant();
            }

            ZonedDateTime afterEvent = event.getEndDate().atZone(ZONE);
            candidate = nextWorkingSlotStart(afterEvent, horaInicio, horaCierre);
        }

        return candidate.toInstant();
    }

    private ZonedDateTime nextWorkingSlotStart(ZonedDateTime from, LocalTime horaInicio, LocalTime horaCierre) {
        ZonedDateTime result = from;

        for (int i = 0; i < 14; i++) {
            if (result.toLocalTime().compareTo(horaCierre) >= 0) {
                result = result.toLocalDate().plusDays(1).atTime(horaInicio).atZone(ZONE);
                continue;
            }

            DayOfWeek dow = result.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY) {
                result = result.toLocalDate().plusDays(2).atTime(horaInicio).atZone(ZONE);
                continue;
            }
            if (dow == DayOfWeek.SUNDAY) {
                result = result.toLocalDate().plusDays(1).atTime(horaInicio).atZone(ZONE);
                continue;
            }

            if (result.toLocalTime().isBefore(horaInicio)) {
                result = result.toLocalDate().atTime(horaInicio).atZone(ZONE);
            }

            return result;
        }

        return result;
    }
}
