package ar.com.lbr.precisionappbe.repositories;

import ar.com.lbr.precisionappbe.model.CuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Integer> {
    List<CuentaBancaria> findByHabilitadaTrue();
}
