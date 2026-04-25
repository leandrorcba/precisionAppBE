package ar.com.lbr.precisionappbe.services.strategies;

import ar.com.lbr.precisionappbe.dto.VariosDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescuentoDefaultStrategyTest {

    private final DescuentoDefaultStrategy strategy = new DescuentoDefaultStrategy();

    @Test
    void calcularDescuento_SiempreDebeRetornarCero() {
        // GIVEN: Cualquier configuración
        BigDecimal resultado = strategy.calcularDescuento(100, new BigDecimal("5000"), new VariosDTO());

        // THEN
        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void aplicaA_DebeResponderTrueParaClientesNoEstudiantes() {
        assertTrue(strategy.aplicaA("NORMAL"));
        assertTrue(strategy.aplicaA("EMPRESA"));
        assertFalse(strategy.aplicaA("ESTUDIANTE"));
    }
}
