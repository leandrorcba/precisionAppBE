package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoPresupuestoRepository extends JpaRepository<PagoPresupuesto, Integer> {
    List<PagoPresupuesto> findPagoPresupuestoByIdPresupuesto(Integer idPresupuesto);

    // idTipoPago 1 corresponds to SENIA
    List<PagoPresupuesto> findPagoPresupuestoByIdPresupuestoAndIdTipoPago_Id(Integer idPresupuesto, Integer idTipoPago);
}
