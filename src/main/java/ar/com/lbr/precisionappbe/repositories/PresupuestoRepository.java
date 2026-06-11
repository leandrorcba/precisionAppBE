package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Presupuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PresupuestoRepository  extends JpaRepository<Presupuesto, Integer>, JpaSpecificationExecutor<Presupuesto> {

    Page<Presupuesto> findByIdClienteOrderByIdDesc(Integer idCliente, Pageable pageable);

    Page<Presupuesto> findByIdClienteAndHabilitadoOrderByIdDesc(Integer idCliente, Boolean habilitado, Pageable pageable);

    Page<Presupuesto> findById(Integer id, Pageable pageable);
}
