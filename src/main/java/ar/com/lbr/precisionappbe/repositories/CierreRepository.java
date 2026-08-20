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

    @Query("SELECT c FROM Cierre c WHERE c.fechaCreacion >= :desde AND c.fechaCreacion < :hasta")
    Page<Cierre> findByFechaCreacionBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta, Pageable pageable);

    @Query("SELECT COUNT(c) > 0 FROM Cierre c WHERE c.fechaCreacion >= :desde AND c.fechaCreacion < :hasta")
    boolean existsByFechaCreacionBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta);

    @Query("SELECT COUNT(c) > 0 FROM Cierre c WHERE c.fechaCreacion >= :desde AND c.fechaCreacion < :hasta AND c.cerrado = true")
    boolean existsClosedCierreBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta);

    java.util.Optional<Cierre> findFirstByCerradoTrueOrderByFechaCierreDesc();

    java.util.Optional<Cierre> findFirstByCerradoTrueOrderByFechaCreacionDesc();

    boolean existsByCerrado(Boolean cerrado);
}
