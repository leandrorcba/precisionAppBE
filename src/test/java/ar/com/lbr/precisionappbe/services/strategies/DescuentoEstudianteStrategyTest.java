package ar.com.lbr.precisionappbe.services.strategies;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DescuentoEstudianteStrategyTest {

    private final DescuentoEstudianteStrategy strategy = new DescuentoEstudianteStrategy();

    @Test
    void calcularDescuento_DebeAplicarPuntosCorrectamente() {
        // GIVEN: 12 minutos y $300 por punto
        int tiempoCorte = 12; // 12 / 5 = 2 puntos (floor)
        BigDecimal precioBase = new BigDecimal("4000");
        VariosDTO config = new VariosDTO();
        config.setDescuentoPorPunto(300);
        config.setMinutosPorPunto(5);

        // WHEN
        BigDecimal descuento = strategy.calcularDescuento(tiempoCorte, precioBase, config);

        // THEN: El descuento por puntos debe ser cero ya que se eliminó esa lógica
        assertEquals(0, BigDecimal.ZERO.compareTo(descuento));
    }

    @Test
    void aplicaA_DebeResponderTrueSoloParaEstudiante() {
        assertTrue(strategy.aplicaA("ESTUDIANTE"));
        assertFalse(strategy.aplicaA("NORMAL"));
    }
}
