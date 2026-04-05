package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Extraccione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ExtraccionRepository extends JpaRepository<Extraccione, Integer> {

    @Query("SELECT e FROM Extraccione e WHERE e.fechaExtraccion >= :desde AND e.fechaExtraccion < :hasta")
    List<Extraccione> findByFechaExtraccionBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta);
}
