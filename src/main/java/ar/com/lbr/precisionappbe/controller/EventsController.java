package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.dto.UpdateEventDTO;
import ar.com.lbr.precisionappbe.services.EventsService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    EventsService eventService;

    public EventsController(EventsService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventsDTO>>> getAllEvents(
            @RequestParam(required = false) Integer calendar,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<EventsDTO> events = eventService.getAllEvents(calendar, startDate, endDate);
        return ResponseBuilder.ok("Materiales obtenidos con éxito", events, (long) events.size());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventsDTO>> createEvent(@RequestBody UpdateEventDTO dto) {
        EventsDTO created = eventService.createEvent(dto);
        return ResponseBuilder.ok("Event creado con éxito", created, 1L);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventsDTO>> updateEvent(
            @PathVariable Integer id,
            @RequestBody UpdateEventDTO dto) {
        EventsDTO updated = eventService.updateEvent(id, dto);
        return ResponseBuilder.ok("Event actualizado con éxito", updated, 1L);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseBuilder.ok("Event eliminado con éxito", null, 0L);
    }
}
