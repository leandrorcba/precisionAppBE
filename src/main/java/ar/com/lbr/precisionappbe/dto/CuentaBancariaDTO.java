package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.CuentaBancaria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CuentaBancariaDTO {

    private Integer id;
    private String banco;
    private String aliasCbu;
    private String cbu;
    private String numeroCuenta;
    private String moneda;
    private Boolean habilitada;

    public static CuentaBancariaDTO toDTO(CuentaBancaria c) {
        if (c == null) {
            return null;
        }
        return new CuentaBancariaDTO(
                c.getId(),
                c.getBanco(),
                c.getAliasCbu(),
                c.getCbu(),
                c.getNumeroCuenta(),
                c.getMoneda(),
                c.getHabilitada()
        );
    }
}
