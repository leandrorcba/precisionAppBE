package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {

    private Integer id;
    private Integer idPresupuesto;
    private Integer idVenta;
    private BigDecimal monto;
    private Instant fechaHora;
    private TipoPagoDTO tipoPago;
    private Integer idTipoPago;
    private MedioPagoDTO medioPago;
    private Integer idMedioPago;
    private Integer idTarjeta;
    private Integer idCuentaBancaria;
    private String tarjetaNombre;
    private String cuentaBancariaNombre;
    private Short cuotas;
    private String autorizacion;
    private String notas;
    private Boolean enabled;
    private String clienteNombre;

}
