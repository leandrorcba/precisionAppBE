package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Cierre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface CierreRepository extends JpaRepository<Cierre, Integer> {
    Page<Cierre> findByMesCierre(String mesCierre, Pageable pageable);

    @Query("SELECT c FROM Cierre c WHERE c.fechaCierre >= :desde AND c.fechaCierre < :hasta")
    Page<Cierre> findByFechaCierreBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta, Pageable pageable);
}
