package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventsRepository extends JpaRepository<Event, Integer> {
        @Query("SELECT e FROM Event e WHERE " +
                        "(:calendarId IS NULL OR e.idMaquina.id = :calendarId) AND " +
                        "(cast(:startDate as timestamp) IS NULL OR e.startDate >= :startDate) AND " +
                        "(cast(:endDate as timestamp) IS NULL OR e.endDate <= :endDate)")
        List<Event> findByFilters(@Param("calendarId") Integer calendarId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
