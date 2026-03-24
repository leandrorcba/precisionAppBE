package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.Punto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PuntoRepository extends JpaRepository<Punto, Integer> {
    Optional<Punto> findByIdCliente(Integer idCliente);
    List<Punto> findByIdClienteIn(List<Integer> idClientes);
}
