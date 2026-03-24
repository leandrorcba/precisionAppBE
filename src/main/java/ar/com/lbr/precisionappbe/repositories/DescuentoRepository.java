package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescuentoRepository extends JpaRepository<Descuento, Integer> {
  //List<Descuento> findByIdPresupuesto(Integer idPresupuesto);
  
  List<Descuento> findDescuentoByIdPresupuesto_Id(Integer idPresupuestoId);

  //List<Pago> findByIdOrigenPagoAndTipoPago_Tipo(Integer idOrigenPago, String tipoPago_Tipo);

  /*@Query("""
        select des
        from Descuento des
        join fetch des.idTipoDescuento
        where des.idPresupuesto = :presupuestoId
      """)
  List<Descuento> findDescuentosOfPresupuesto(@Param("presupuestoId") Integer presupuestoId);*/
}
