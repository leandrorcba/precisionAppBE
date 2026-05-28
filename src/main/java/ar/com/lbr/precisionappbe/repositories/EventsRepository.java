package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Integer> {
        @Query("SELECT e FROM Event e WHERE " +
                        "(:calendarId IS NULL OR e.idMaquina.id = :calendarId) AND " +
                        "(cast(:startDate as instant) IS NULL OR e.startDate >= :startDate) AND " +
                        "(cast(:endDate as instant) IS NULL OR e.endDate <= :endDate)")
        List<Event> findByFilters(@Param("calendarId") Integer calendarId,
                        @Param("startDate") Instant startDate,
                        @Param("endDate") Instant endDate);

        List<Event> findByIdMaquinaIdAndStartDateGreaterThanEqualOrderByStartDate(
                        Integer idMaquinaId, Instant from);
}
