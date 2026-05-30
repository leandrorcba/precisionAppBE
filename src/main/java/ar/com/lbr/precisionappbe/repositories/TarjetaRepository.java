package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {
}
