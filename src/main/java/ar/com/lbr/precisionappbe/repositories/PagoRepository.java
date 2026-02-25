package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
  List<Pago> findByIdOrigenPago(Integer idOrigenPago);

  List<Pago> findByIdOrigenPagoAndTipoPago_Tipo(Integer idOrigenPago, String tipoPago_Tipo);

  @Query("""
        select pa
        from Pago pa
        join fetch pa.tipoPago
        join fetch pa.medioPago
        where pa.idOrigenPago = :presupuestoId
      """)
  List<Pago> findPagosOfPresupuesto(@Param("presupuestoId") Long presupuestoId);
}
