package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;

    public EventsService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public List<EventsDTO> getAllEvents(Integer calendar, String startDateStr, String endDateStr) {
        Instant startDate = startDateStr != null ? Instant.parse(startDateStr) : null;
        Instant endDate = endDateStr != null ? Instant.parse(endDateStr) : null;

        return eventsRepository.findByFilters(calendar, startDate, endDate).stream()
                .map(EventsDTO::toDTO)
                .collect(Collectors.toList());
    }
}
