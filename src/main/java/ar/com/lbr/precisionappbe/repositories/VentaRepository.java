package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByFechaHoraVentaBetween(Instant from, Instant to);
}
