package ar.com.lbr.precisionappbe.dto;

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

}
