package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.PrecioMateriale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrecioMaterialRepository  extends JpaRepository<PrecioMateriale, Integer> {
    PrecioMateriale findByIdMaterialesAndIdSuperficie(Integer idMateriales, Integer idSuperficie);
}
