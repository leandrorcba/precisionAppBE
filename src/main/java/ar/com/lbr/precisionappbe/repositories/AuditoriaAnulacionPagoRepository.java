package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.AuditoriaAnulacionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaAnulacionPagoRepository extends JpaRepository<AuditoriaAnulacionPago, Integer> {
    List<AuditoriaAnulacionPago> findByOrderByFechaHoraAnulacionDesc();
}
