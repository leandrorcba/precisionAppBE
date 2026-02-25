package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoClienteRepository extends JpaRepository<TipoCliente, Integer> {

    TipoCliente findTipoClienteById(Integer id);
}
