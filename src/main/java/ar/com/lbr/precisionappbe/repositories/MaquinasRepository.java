package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaquinasRepository extends JpaRepository<Maquina, Integer> {
}