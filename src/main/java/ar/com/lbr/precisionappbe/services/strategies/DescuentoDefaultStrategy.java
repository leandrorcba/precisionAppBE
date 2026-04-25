package ar.com.lbr.precisionappbe.services.strategies;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DescuentoDefaultStrategy implements DescuentoStrategy {
    @Override
    public boolean aplicaA(String tipoCliente) {
        // Esta actúa como fallback, se puede manejar con prioridad o excluyendo tipos específicos
        return !"ESTUDIANTE".equalsIgnoreCase(tipoCliente);
    }

    @Override
    public BigDecimal calcularDescuento(int tiempo, BigDecimal precioBase, VariosDTO config) {
        return BigDecimal.ZERO;
    }
}
