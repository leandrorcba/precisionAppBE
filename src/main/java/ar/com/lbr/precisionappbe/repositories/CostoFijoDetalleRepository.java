package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.CostoFijo;
import ar.com.lbr.precisionappbe.model.CostoFijoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostoFijoDetalleRepository extends JpaRepository<CostoFijoDetalle, Integer> {

    List<CostoFijoDetalle> findByIdCostoFijo(CostoFijo costoFijo);

    void deleteByIdCostoFijo(CostoFijo costoFijo);
}