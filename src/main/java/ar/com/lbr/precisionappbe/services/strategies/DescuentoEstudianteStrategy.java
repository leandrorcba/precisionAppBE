package ar.com.lbr.precisionappbe.services.strategies;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DescuentoEstudianteStrategy implements DescuentoStrategy {
    @Override
    public boolean aplicaA(String tipoCliente) {
        return "ESTUDIANTE".equals(tipoCliente);
    }

    @Override
    public BigDecimal calcularDescuento(int tiempo, BigDecimal precioBase, VariosDTO config) {
        int puntos = tiempo / config.getMinutosPorPunto();
        return new BigDecimal(puntos).multiply(BigDecimal.valueOf(config.getDescuentoPorPunto()));
    }
}
