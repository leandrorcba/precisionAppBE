package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoReporteDTO {
    private String tipoMovimiento; // "INGRESO" o "EGRESO"
    private String categoria;      // "PRESUPUESTO", "SENIA", "VENTA", "EXTRACCION", "COMPRA_MATERIAL", "GASTO"
    private String descripcion;    // Detalle de notas, cliente, material o motivo
    private BigDecimal monto;
    private String medioPago;      // "EFECTIVO", "TRANSFERENCIA", "TARJETA", etc.
    private Instant fechaHora;
    private String responsable;
}
