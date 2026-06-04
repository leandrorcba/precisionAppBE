package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Integer> {

    @Query("SELECT g FROM Gasto g WHERE g.fechaGasto >= :desde AND g.fechaGasto < :hasta")
    List<Gasto> findByFechaGastoBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta);
}
