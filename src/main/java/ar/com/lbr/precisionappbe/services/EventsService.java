package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.dto.UpdateEventDTO;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.Event;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class EventsService {

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final EventsRepository eventsRepository;
    private final MaquinasRepository maquinasRepository;
    private final TrabajoPresupuestadoRepository trabajoPresupuestadoRepository;
    private final TrabajosService trabajosService;
    private final VariosRepository variosRepository;

    public EventsService(EventsRepository eventsRepository,
                         MaquinasRepository maquinasRepository,
                         TrabajoPresupuestadoRepository trabajoPresupuestadoRepository,
                         @org.springframework.context.annotation.Lazy TrabajosService trabajosService,
                         VariosRepository variosRepository) {
        this.eventsRepository = eventsRepository;
        this.maquinasRepository = maquinasRepository;
        this.trabajoPresupuestadoRepository = trabajoPresupuestadoRepository;
        this.trabajosService = trabajosService;
        this.variosRepository = variosRepository;
    }

    private EstadoTrabajo mapStatusToEstado(String status) {
        if (status == null) {
            return EstadoTrabajo.PENDIENTE;
        }
        String st = status.trim().toLowerCase();
        if (st.equals("realizado")) {
            return EstadoTrabajo.REALIZADO;
        } else if (st.equals("entregado")) {
            return EstadoTrabajo.ENTREGADO;
        }
        return EstadoTrabajo.PENDIENTE;
    }

    private String mapEstadoToStatus(EstadoTrabajo estado) {
        if (estado == EstadoTrabajo.PENDIENTE) {
            return "aprobado";
        } else if (estado == EstadoTrabajo.REALIZADO) {
            return "realizado";
        } else if (estado == EstadoTrabajo.ENTREGADO) {
            return "entregado";
        }
        return "aprobado";
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "aprobado";
        }
        String st = status.trim().toLowerCase();
        if (st.equals("pendiente")) {
            return "aprobado";
        }
        return st;
    }

    private EventsDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        EventsDTO dto = EventsDTO.toDTO(event);
        if (event.getIdTrabajo() != null) {
            trabajoPresupuestadoRepository.findById(event.getIdTrabajo()).ifPresent(trabajo -> {
                dto.setStatus(mapEstadoToStatus(trabajo.getEstado()));
            });
        } else {
            dto.setStatus(normalizeStatus(event.getStatus()));
        }
        return dto;
    }

    public List<EventsDTO> getAllEvents(Integer calendar, String startDateStr, String endDateStr) {
        Instant minStartDate = LocalDate.now().minusMonths(3).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant startDate = startDateStr != null
                ? OffsetDateTime.parse(startDateStr).toInstant()
                : minStartDate;
        Instant endDate = endDateStr != null
                ? OffsetDateTime.parse(endDateStr).toInstant()
                : Instant.now();

        List<Event> events = eventsRepository.findByFilters(calendar, startDate, endDate);

        // Gather all job IDs
        List<Integer> jobIds = events.stream()
                .map(Event::getIdTrabajo)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        // Query them in bulk
        java.util.Map<Integer, EstadoTrabajo> jobStatusMap = new java.util.HashMap<>();
        if (!jobIds.isEmpty()) {
            List<TrabajoPresupuestado> trabajos = trabajoPresupuestadoRepository.findAllById(jobIds);
            for (TrabajoPresupuestado t : trabajos) {
                if (t.getEstado() != null) {
                    jobStatusMap.put(t.getId(), t.getEstado());
                }
            }
        }

        return events.stream()
                .map(event -> {
                    EventsDTO dto = EventsDTO.toDTO(event);
                    if (dto != null) {
                        if (event.getIdTrabajo() != null) {
                            EstadoTrabajo estado = jobStatusMap.get(event.getIdTrabajo());
                            if (estado != null) {
                                dto.setStatus(mapEstadoToStatus(estado));
                            } else {
                                dto.setStatus(normalizeStatus(event.getStatus()));
                            }
                        } else {
                            dto.setStatus(normalizeStatus(event.getStatus()));
                        }
                    }
                    return dto;
                })
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

        return toDTO(eventsRepository.save(event));
    }

    public EventsDTO updateEvent(Integer id, UpdateEventDTO dto) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));

        if (dto.getEventName() != null) {
            event.setEventName(dto.getEventName());
        }
        if (dto.getDetails() != null) {
            event.setDetails(dto.getDetails());
        }
        if (dto.getDuracion() != null) {
            event.setDuracion(dto.getDuracion());
        }
        if (dto.getStatus() != null) {
            event.setStatus(dto.getStatus());
            if (event.getIdTrabajo() != null) {
                EstadoTrabajo nuevoEstado = mapStatusToEstado(dto.getStatus());
                trabajosService.updateEstado(event.getIdTrabajo(), nuevoEstado);
            }
        }
        if (dto.getNotas() != null) {
            event.setNotas(dto.getNotas());
            if (event.getIdTrabajo() != null) {
                trabajoPresupuestadoRepository.findById(event.getIdTrabajo()).ifPresent(trabajo -> {
                    trabajo.setNotas(dto.getNotas());
                    trabajoPresupuestadoRepository.save(trabajo);
                });
            }
        }

        boolean machineChanged = dto.getCalendarId() != null &&
                (event.getIdMaquina() == null || !dto.getCalendarId().equals(event.getIdMaquina().getId()));

        if (machineChanged) {
            Maquina maquina = maquinasRepository.findById(dto.getCalendarId())
                    .orElseThrow(() -> new EntityNotFoundException("Maquina not found: " + dto.getCalendarId()));
            event.setIdMaquina(maquina);

            int durationMinutes = event.getDuracion() != null ? event.getDuracion() : 15;

            Varios varios = variosRepository.findFirstByOrderByIdAsc();
            LocalTime horaInicio = (varios != null && varios.getHoraInicio() != null) ? varios.getHoraInicio() : LocalTime.of(8, 0);
            LocalTime horaCierre = (varios != null && varios.getHoraCierre() != null) ? varios.getHoraCierre() : LocalTime.of(18, 0);
            Boolean permitirFds = (varios != null && varios.getPermitirTrabajosFds() != null) ? varios.getPermitirTrabajosFds() : false;
            LocalTime horaInicioFds = (varios != null && varios.getHoraInicioFds() != null) ?
                    varios.getHoraInicioFds() : LocalTime.of(9, 0);
            LocalTime horaCierreFds = (varios != null && varios.getHoraCierreFds() != null) ?
                    varios.getHoraCierreFds() : LocalTime.of(13, 0);

            Instant now = Instant.now();
            Instant targetStart = dto.getStartDate() != null ? dto.getStartDate().toInstant() : null;
            Instant targetEnd = dto.getEndDate() != null ? dto.getEndDate().toInstant() : null;

            boolean useTarget = false;
            if (targetStart != null && targetStart.isAfter(now)) {
                if (targetEnd == null) {
                    targetEnd = targetStart.plus(durationMinutes, ChronoUnit.MINUTES);
                }
                boolean overlaps = eventsRepository.existsOverlappingEvent(dto.getCalendarId(), targetStart, targetEnd, event.getId());
                if (!overlaps) {
                    event.setStartDate(targetStart);
                    event.setEndDate(targetEnd);
                    useTarget = true;
                }
            }

            if (!useTarget) {
                Instant slotStart = findFirstAvailableSlot(dto.getCalendarId(), durationMinutes, horaInicio,
                        horaCierre, permitirFds, horaInicioFds, horaCierreFds);
                Instant slotEnd = slotStart.plus(durationMinutes, ChronoUnit.MINUTES);

                event.setStartDate(slotStart);
                event.setEndDate(slotEnd);
            }

            if (event.getIdTrabajo() != null) {
                trabajoPresupuestadoRepository.findById(event.getIdTrabajo()).ifPresent(trabajo -> {
                    trabajo.setIdMaquina(dto.getCalendarId());
                    trabajoPresupuestadoRepository.save(trabajo);
                });
            }
        } else {
            if (dto.getStartDate() != null) {
                event.setStartDate(dto.getStartDate().toInstant());
            }
            if (dto.getEndDate() != null) {
                event.setEndDate(dto.getEndDate().toInstant());
            }
            if (dto.getCalendarId() != null) {
                Maquina maquina = maquinasRepository.findById(dto.getCalendarId())
                        .orElseThrow(() -> new EntityNotFoundException("Maquina not found: " + dto.getCalendarId()));
                event.setIdMaquina(maquina);
            }
        }

        return toDTO(eventsRepository.save(event));
    }

    public EventsDTO createEventForTrabajo(Integer idMaquina, Integer idPresupuesto, Integer idTrabajo,
                                           String eventName, int durationMinutes,
                                           LocalTime horaInicio, LocalTime horaCierre) {
        Maquina maquina = maquinasRepository.findById(idMaquina)
                .orElseThrow(() -> new EntityNotFoundException("Maquina not found: " + idMaquina));

        Varios varios = variosRepository.findFirstByOrderByIdAsc();
        Boolean permitirFds = (varios != null && varios.getPermitirTrabajosFds() != null) ? varios.getPermitirTrabajosFds() : false;
        LocalTime horaInicioFds = (varios != null && varios.getHoraInicioFds() != null) ? varios.getHoraInicioFds() : LocalTime.of(9, 0);
        LocalTime horaCierreFds = (varios != null && varios.getHoraCierreFds() != null) ? varios.getHoraCierreFds() : LocalTime.of(13, 0);

        LocalTime hInicio = (horaInicio != null) ? horaInicio : ((varios != null && varios.getHoraInicio() != null) ?
                varios.getHoraInicio() : LocalTime.of(8, 0));
        LocalTime hCierre = (horaCierre != null) ? horaCierre : ((varios != null && varios.getHoraCierre() != null) ?
                varios.getHoraCierre() : LocalTime.of(18, 0));

        Instant slotStart = findFirstAvailableSlot(idMaquina, durationMinutes, hInicio, hCierre, permitirFds, horaInicioFds, horaCierreFds);
        Instant slotEnd = slotStart.plus(durationMinutes, ChronoUnit.MINUTES);

        String name = eventName != null && eventName.length() > 127 ? eventName.substring(0, 127) : eventName;

        String notes = null;
        String details = null;
        if (idTrabajo != null) {
            TrabajoPresupuestado trabajo = trabajoPresupuestadoRepository.findById(idTrabajo).orElse(null);
            if (trabajo != null) {
                notes = trabajo.getNotas();
                if (Boolean.TRUE.equals(trabajo.getTraeMaterial())) {
                    details = "Cliente trae material";
                }
            }
        }

        Event event = new Event();
        event.setIdMaquina(maquina);
        event.setStartDate(slotStart);
        event.setEndDate(slotEnd);
        event.setEventName(name != null ? name : "");
        event.setIdPresupuesto(idPresupuesto);
        event.setIdTrabajo(idTrabajo);
        event.setDuracion(durationMinutes);
        event.setStatus("PENDIENTE");
        event.setNotas(notes);
        event.setDetails(details);

        return toDTO(eventsRepository.save(event));
    }

    public Instant findFirstAvailableSlot(Integer idMaquina, int durationMinutes,
                                          LocalTime horaInicio, LocalTime horaCierre,
                                          Boolean permitirTrabajosFds, LocalTime horaInicioFds, LocalTime horaCierreFds) {
        Instant now = Instant.now();
        List<Event> events = eventsRepository
                .findByIdMaquinaIdAndEndDateGreaterThanOrderByStartDate(idMaquina, now);

        ZonedDateTime candidate = nextWorkingSlotStart(now.atZone(ZONE), horaInicio, horaCierre, permitirTrabajosFds,
                horaInicioFds, horaCierreFds);

        for (Event event : events) {
            ZonedDateTime eventStart = event.getStartDate().atZone(ZONE);
            ZonedDateTime slotEnd = candidate.plusMinutes(durationMinutes);

            if (!slotEnd.isAfter(eventStart)) {
                return candidate.toInstant();
            }

            ZonedDateTime afterEvent = event.getEndDate().atZone(ZONE);
            candidate = nextWorkingSlotStart(afterEvent, horaInicio, horaCierre, permitirTrabajosFds, horaInicioFds, horaCierreFds);
        }

        return candidate.toInstant();
    }

    private ZonedDateTime nextWorkingSlotStart(ZonedDateTime from, LocalTime horaInicio, LocalTime horaCierre,
                                               Boolean permitirTrabajosFds, LocalTime horaInicioFds, LocalTime horaCierreFds) {
        ZonedDateTime result = from;

        for (int i = 0; i < 14; i++) {
            DayOfWeek dow = result.getDayOfWeek();
            boolean isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);

            LocalTime currentInicio = (isWeekend && Boolean.TRUE.equals(permitirTrabajosFds)) ? horaInicioFds : horaInicio;
            LocalTime currentCierre = (isWeekend && Boolean.TRUE.equals(permitirTrabajosFds)) ? horaCierreFds : horaCierre;

            if (currentInicio == null) {
                currentInicio = LocalTime.of(8, 0);
            }
            if (currentCierre == null) {
                currentCierre = LocalTime.of(18, 0);
            }

            if (result.toLocalTime().compareTo(currentCierre) >= 0) {
                result = result.toLocalDate().plusDays(1).atTime(currentInicio).atZone(ZONE);
                continue;
            }

            if (isWeekend && !Boolean.TRUE.equals(permitirTrabajosFds)) {
                if (dow == DayOfWeek.SATURDAY) {
                    result = result.toLocalDate().plusDays(2).atTime(horaInicio).atZone(ZONE);
                } else {
                    result = result.toLocalDate().plusDays(1).atTime(horaInicio).atZone(ZONE);
                }
                continue;
            }

            if (result.toLocalTime().isBefore(currentInicio)) {
                result = result.toLocalDate().atTime(currentInicio).atZone(ZONE);
            }

            return result;
        }

        return result;
    }

    public void deleteEvent(Integer id) {
        if (!eventsRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found: " + id);
        }
        eventsRepository.deleteById(id);
    }

    public void deleteEventsByPresupuesto(Integer idPresupuesto) {
        List<Event> events = eventsRepository.findByIdPresupuesto(idPresupuesto);
        if (events != null && !events.isEmpty()) {
            eventsRepository.deleteAll(events);
        }
    }
}
