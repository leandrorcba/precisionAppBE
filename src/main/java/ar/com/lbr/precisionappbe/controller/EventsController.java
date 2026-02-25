package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.services.EventsService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
