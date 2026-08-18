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
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventsServiceTest {

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private MaquinasRepository maquinasRepository;

    @Mock
    private TrabajoPresupuestadoRepository trabajoPresupuestadoRepository;

    @Mock
    private TrabajosService trabajosService;

    @Mock
    private VariosRepository variosRepository;

    @Mock
    private MaterialeRepository materialeRepository;

    private EventsService service;

    @BeforeEach
    void setUp() {
        service = new EventsService(eventsRepository, maquinasRepository,
                trabajoPresupuestadoRepository, trabajosService, variosRepository, materialeRepository);
    }

    @Test
    void getAllEvents_withValidFilters_returnsDtoList() {
        String startStr = "2026-06-01T08:00:00Z";
        String endStr = "2026-06-02T18:00:00Z";
        Instant start = OffsetDateTime.parse(startStr).toInstant();
        Instant end = OffsetDateTime.parse(endStr).toInstant();

        Maquina maquina = new Maquina();
        maquina.setId(1);

        Event event = new Event();
        event.setId(10);
        event.setIdMaquina(maquina);
        event.setIdTrabajo(5);
        event.setStartDate(start);
        event.setEndDate(end);

        TrabajoPresupuestado trabajo = new TrabajoPresupuestado();
        trabajo.setId(5);
        trabajo.setEstado(EstadoTrabajo.REALIZADO);

        when(eventsRepository.findByFilters(1, start, end)).thenReturn(List.of(event));
        when(trabajoPresupuestadoRepository.findAllById(List.of(5))).thenReturn(List.of(trabajo));

        List<EventsDTO> result = service.getAllEvents(1, startStr, endStr);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("realizado");
    }

    @Test
    void createEvent_validDto_savesEvent() {
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setCalendarId(2);
        dto.setStartDate(OffsetDateTime.parse("2026-06-14T09:00:00Z"));
        dto.setEndDate(OffsetDateTime.parse("2026-06-14T10:00:00Z"));
        dto.setEventName("Test Event");

        Maquina maquina = new Maquina();
        maquina.setId(2);

        Event savedEvent = new Event();
        savedEvent.setId(20);
        savedEvent.setIdMaquina(maquina);
        savedEvent.setStartDate(dto.getStartDate().toInstant());
        savedEvent.setEndDate(dto.getEndDate().toInstant());
        savedEvent.setEventName("Test Event");

        when(maquinasRepository.findById(2)).thenReturn(Optional.of(maquina));
        when(eventsRepository.save(any(Event.class))).thenReturn(savedEvent);

        EventsDTO result = service.createEvent(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20);
    }

    @Test
    void createEvent_machineNotFound_throwsEntityNotFound() {
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setCalendarId(99);

        when(maquinasRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createEvent(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Maquina not found");
    }

    @Test
    void updateEvent_existingEvent_savesUpdated() {
        Maquina maquina = new Maquina();
        maquina.setId(1);

        Event event = new Event();
        event.setId(10);
        event.setIdTrabajo(5);
        event.setIdMaquina(maquina);

        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setEventName("Updated Name");
        dto.setStatus("realizado");

        when(eventsRepository.findById(10)).thenReturn(Optional.of(event));
        when(eventsRepository.save(event)).thenReturn(event);

        EventsDTO result = service.updateEvent(10, dto);

        assertThat(result).isNotNull();
        verify(trabajosService).updateEstado(5, EstadoTrabajo.REALIZADO);
    }

    @Test
    void updateEvent_withMachineChange_computesAvailableSlot() {
        Maquina m1 = new Maquina();
        m1.setId(1);

        Maquina m2 = new Maquina();
        m2.setId(2);

        Event event = new Event();
        event.setId(10);
        event.setIdMaquina(m1);
        event.setDuracion(30);

        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setCalendarId(2);
        dto.setStartDate(OffsetDateTime.parse("2030-06-14T09:00:00-03:00"));

        Varios varios = new Varios();
        varios.setHoraInicio(LocalTime.of(8, 0));
        varios.setHoraCierre(LocalTime.of(18, 0));
        varios.setPermitirTrabajosFds(false);

        when(eventsRepository.findById(10)).thenReturn(Optional.of(event));
        when(maquinasRepository.findById(2)).thenReturn(Optional.of(m2));
        when(variosRepository.findFirstByOrderByIdAsc()).thenReturn(varios);
        when(eventsRepository.save(event)).thenReturn(event);

        EventsDTO result = service.updateEvent(10, dto);

        assertThat(result).isNotNull();
        assertThat(event.getIdMaquina().getId()).isEqualTo(2);
    }

    @Test
    void createEventForTrabajo_savesNewEvent() {
        Maquina maquina = new Maquina();
        maquina.setId(1);

        Varios varios = new Varios();
        varios.setHoraInicio(LocalTime.of(8, 0));
        varios.setHoraCierre(LocalTime.of(18, 0));

        when(maquinasRepository.findById(1)).thenReturn(Optional.of(maquina));
        when(variosRepository.findFirstByOrderByIdAsc()).thenReturn(varios);
        when(eventsRepository.findByIdMaquinaIdAndEndDateGreaterThanOrderByStartDate(eq(1), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        Event saved = new Event();
        saved.setId(15);
        saved.setIdMaquina(maquina);
        when(eventsRepository.save(any(Event.class))).thenReturn(saved);

        EventsDTO result = service.createEventForTrabajo(1, 100, 5, "Corte", 30, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(15);
    }

    @Test
    void createEventForTrabajo_withTraeMaterial_setsDetails() {
        Maquina maquina = new Maquina();
        maquina.setId(1);

        Varios varios = new Varios();
        varios.setHoraInicio(LocalTime.of(8, 0));
        varios.setHoraCierre(LocalTime.of(18, 0));

        TrabajoPresupuestado trabajo = new TrabajoPresupuestado();
        trabajo.setId(5);
        trabajo.setTraeMaterial(true);
        trabajo.setNotas("Alguna nota");

        when(maquinasRepository.findById(1)).thenReturn(Optional.of(maquina));
        when(variosRepository.findFirstByOrderByIdAsc()).thenReturn(varios);
        when(trabajoPresupuestadoRepository.findById(5)).thenReturn(Optional.of(trabajo));
        when(eventsRepository.findByIdMaquinaIdAndEndDateGreaterThanOrderByStartDate(eq(1), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        Event saved = new Event();
        saved.setId(15);
        saved.setIdMaquina(maquina);
        when(eventsRepository.save(eventCaptor.capture())).thenReturn(saved);

        service.createEventForTrabajo(1, 100, 5, "Corte", 30, null, null);

        Event captured = eventCaptor.getValue();
        assertThat(captured.getDetails()).isEqualTo("Cliente trae material");
        assertThat(captured.getNotas()).isEqualTo("Alguna nota");
    }

    @Test
    void deleteEvent_existing_deletes() {
        when(eventsRepository.existsById(1)).thenReturn(true);

        service.deleteEvent(1);

        verify(eventsRepository).deleteById(1);
    }

    @Test
    void deleteEvent_nonExisting_throwsEntityNotFound() {
        when(eventsRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteEvent(99))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteEventsByPresupuesto_deletesAll() {
        Event event = new Event();
        when(eventsRepository.findByIdPresupuesto(5)).thenReturn(List.of(event));

        service.deleteEventsByPresupuesto(5);

        verify(eventsRepository).deleteAll(List.of(event));
    }
}
