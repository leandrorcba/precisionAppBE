package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PagoPresupuestoRepository extends JpaRepository<PagoPresupuesto, Integer> {

    List<PagoPresupuesto> findByIdPresupuestoAndEnabledTrue(Integer idPresupuesto);

    // idTipoPago 1 corresponds to SENIA
    List<PagoPresupuesto> findByIdPresupuestoAndIdTipoPago_IdAndEnabledTrue(Integer idPresupuesto, Integer idTipoPago);

    Optional<PagoPresupuesto> findByIdAndEnabledTrue(Integer id);

    @Query("SELECT p FROM PagoPresupuesto p WHERE p.fechaHora >= :desde AND p.fechaHora < :hasta AND p.enabled = true")
    List<PagoPresupuesto> findByFechaHoraBetweenAndEnabledTrue(@Param("desde") Instant desde, @Param("hasta") Instant hasta);
}
