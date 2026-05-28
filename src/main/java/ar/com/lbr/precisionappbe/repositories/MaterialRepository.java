package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
}
