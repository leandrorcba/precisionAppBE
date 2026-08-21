package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.PagoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PagoVentaRepository extends JpaRepository<PagoVenta, Integer> {

    List<PagoVenta> findByIdVenta_Id(Integer idVenta);

    List<PagoVenta> findByIdVenta_IdAndEnabledTrue(Integer idVenta);

    java.util.Optional<PagoVenta> findByIdAndEnabledTrue(Integer id);

    List<PagoVenta> findByIdVenta_IdIn(List<Integer> idsVentas);

    List<PagoVenta> findByIdVenta_IdInAndEnabledTrue(List<Integer> idsVentas);

    @Query("SELECT p FROM PagoVenta p WHERE p.fechaHora >= :desde AND p.fechaHora < :hasta AND (p.enabled = true OR p.enabled IS NULL)")
    List<PagoVenta> findByFechaHoraBetween(@Param("desde") Instant desde, @Param("hasta") Instant hasta);
}
