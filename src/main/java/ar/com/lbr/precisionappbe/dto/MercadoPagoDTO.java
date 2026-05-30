package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.MercadoPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MercadoPagoDTO {
    private Integer id;
    private String titular;
    private Boolean disabled;

    public static MercadoPagoDTO toDTO(MercadoPago mp) {
        if (mp == null) {
            return null;
        }
        return new MercadoPagoDTO(mp.getId(), mp.getTitular(), mp.getDisabled());
    }
}
