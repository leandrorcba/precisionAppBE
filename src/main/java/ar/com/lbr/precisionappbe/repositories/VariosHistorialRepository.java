package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.VariosHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariosHistorialRepository extends JpaRepository<VariosHistorial, Integer> {
}
