package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Varios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariosRepository extends JpaRepository<Varios, Integer> {
    Varios findFirstByOrderByIdAsc();
}

