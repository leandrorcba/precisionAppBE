package ar.com.lbr.precisionappbe.services.strategies;

import ar.com.lbr.precisionappbe.dto.VariosDTO;

import java.math.BigDecimal;

public interface DescuentoStrategy {
    boolean aplicaA(String tipoCliente);

    BigDecimal calcularDescuento(int tiempo, BigDecimal precioBase, VariosDTO config);
}
