package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.EventsDTO;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;

    public EventsService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public List<EventsDTO> getAllEvents(Integer calendar, String startDateStr, String endDateStr) {
        LocalDateTime minStartDate = LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime startDate = startDateStr != null ? LocalDateTime.parse(startDateStr) : minStartDate;
        LocalDateTime endDate = endDateStr != null ? LocalDateTime.parse(endDateStr) : null;

        return eventsRepository.findByFilters(calendar, startDate, endDate).stream()
                .map(EventsDTO::toDTO)
                .collect(Collectors.toList());
    }
}
