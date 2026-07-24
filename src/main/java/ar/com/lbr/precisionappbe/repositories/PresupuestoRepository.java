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

    long countByIdClienteAndEntregadoTrueAndCobradoFalseAndHabilitadoTrue(Integer idCliente);

    java.util.List<Presupuesto> findByIdClienteAndEntregadoTrueAndCobradoFalseAndHabilitadoTrue(Integer idCliente);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT p.id) FROM Presupuesto p " +
            "LEFT JOIN TrabajoPresupuestado t ON p.id = t.idPresupuesto " +
            "WHERE p.idCliente = :idCliente " +
            "AND p.habilitado = true " +
            "AND p.cobrado = false " +
            "AND (p.entregado = true OR (t.seleccionado = true AND t.estado = ar.com.lbr.precisionappbe.model.EstadoTrabajo.ENTREGADO))")
    long countPresupuestosImpagosConTrabajosEntregados(@org.springframework.data.repository.query.Param("idCliente") Integer idCliente);
}
