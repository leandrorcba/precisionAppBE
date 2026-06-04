package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.CompraMateriale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CompraMaterialeRepository extends JpaRepository<CompraMateriale, Integer> {

    @Query("SELECT c FROM CompraMateriale c WHERE c.fechaHoraCompra >= :desde AND c.fechaHoraCompra < :hasta")
    List<CompraMateriale> findByFechaHoraCompraBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta);
}
