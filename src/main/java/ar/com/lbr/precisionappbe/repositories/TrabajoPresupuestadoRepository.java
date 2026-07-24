package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrabajoPresupuestadoRepository extends JpaRepository<TrabajoPresupuestado, Integer> {
    List<TrabajoPresupuestado> findByIdPresupuesto(Integer idPresupuesto);
}
