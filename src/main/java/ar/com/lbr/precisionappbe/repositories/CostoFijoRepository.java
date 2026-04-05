package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.CostoFijo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CostoFijoRepository extends JpaRepository<CostoFijo, Integer> {

    @Query("SELECT c FROM CostoFijo c WHERE c.periodo BETWEEN :start AND :end ORDER BY c.periodo ASC")
    List<CostoFijo> findByPeriodoBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
