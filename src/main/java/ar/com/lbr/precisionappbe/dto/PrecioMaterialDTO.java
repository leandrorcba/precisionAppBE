package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Material;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrecioMaterialDTO {
    private BigDecimal precio;

}
