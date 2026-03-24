package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByFechaVentaBetween(LocalDate from, LocalDate to);
    List<Venta> findByFechaVenta(LocalDate fecha);
}
