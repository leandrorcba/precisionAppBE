package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialeRepository extends JpaRepository<Material, Integer> {
    List<Material> findByIsMaterialTrue();

    List<Material> findByIsMaterialTrueOrderByMaterialesAsc();
}
