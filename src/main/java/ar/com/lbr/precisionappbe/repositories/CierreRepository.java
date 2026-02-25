package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Cierre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CierreRepository extends JpaRepository<Cierre, Integer> {
    Page<Cierre> findByMesCierre(String mesCierre, Pageable pageable);
}
