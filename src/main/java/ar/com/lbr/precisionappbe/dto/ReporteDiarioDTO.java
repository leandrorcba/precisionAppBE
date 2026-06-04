package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDiarioDTO {
    private String fecha;
    private List<MovimientoReporteDTO> ingresos;
    private List<MovimientoReporteDTO> egresos;
    private BigDecimal totalIngresosEfectivo;
    private BigDecimal totalEgresosEfectivo;
    private BigDecimal totalIngresosOtrosMedios;
    private BigDecimal balanceCajaEfectivo;
}
