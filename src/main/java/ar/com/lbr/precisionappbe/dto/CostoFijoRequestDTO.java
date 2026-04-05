package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CostoFijoRequestDTO {

    private LocalDate periodo;
    private List<DetalleItem> detalles;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetalleItem {
        private Integer idTipoCostoFijo;
        private BigDecimal monto;
    }
}