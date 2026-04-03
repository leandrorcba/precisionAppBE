package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialeRepository extends JpaRepository<Material, Integer> {
    List<Material> findByIsMaterialTrue();

    Page<Material> findByDisabledFalse(Pageable pageable);

    Page<Material> findByDisabledTrue(Pageable pageable);

    Page<Material> findByMaterialesContainingIgnoreCaseAndDisabledFalse(String materiales, Pageable pageable);

    Page<Material> findByMaterialesContainingIgnoreCaseAndDisabledTrue(String materiales, Pageable pageable);

    List<Material> findByIsMaterialTrueAndDisabledFalseOrderByMaterialesAsc();

    List<Material> findByIsGrabadoTrueAndDisabledFalseOrderByMaterialesAsc();
}
