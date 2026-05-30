package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.MercadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MercadoPagoRepository extends JpaRepository<MercadoPago, Integer> {
    List<MercadoPago> findByDisabledFalse();
}
