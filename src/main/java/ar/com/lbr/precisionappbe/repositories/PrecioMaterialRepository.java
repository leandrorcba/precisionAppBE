package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.PrecioMateriale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrecioMaterialRepository extends JpaRepository<PrecioMateriale, Integer> {
    PrecioMateriale findByIdMaterialesAndIdSuperficie(Integer idMateriales, Integer idSuperficie);
    PrecioMateriale findFirstByIdMateriales(Integer idMateriales);

    @Query("SELECT pm, m.materiales FROM PrecioMateriale pm JOIN Material m ON pm.idMateriales = m.id ORDER BY m.materiales ASC")
    List<Object[]> findAllWithNombreMaterial();
}
