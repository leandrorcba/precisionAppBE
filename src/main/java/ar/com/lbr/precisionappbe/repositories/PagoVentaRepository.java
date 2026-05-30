package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.PagoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoVentaRepository extends JpaRepository<PagoVenta, Integer> {

    List<PagoVenta> findByIdVenta_Id(Integer idVenta);

    List<PagoVenta> findByIdVenta_IdIn(List<Integer> idsVentas);
}
