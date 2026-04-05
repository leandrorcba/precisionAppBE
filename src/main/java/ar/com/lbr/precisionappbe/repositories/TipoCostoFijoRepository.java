package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.TipoCostoFijo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoCostoFijoRepository extends JpaRepository<TipoCostoFijo, Integer> {

    List<TipoCostoFijo> findByActivoTrue();
}