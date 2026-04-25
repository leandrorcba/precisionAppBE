package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DescuentoDTO {
    private Integer tipoDescuento;

    private BigDecimal monto;

    private Integer idPresupuesto;

    private Integer idTipoDescuento;

    private Integer idTrabajoPresupuestado;

    private Integer minutosPorPunto;

    private BigDecimal precioMinuto;

    private Integer minutosDescontados;
}


