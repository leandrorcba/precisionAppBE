package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Pago;
import ar.com.lbr.precisionappbe.model.Presupuesto;
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
    private BigDecimal monto;
    private Instant fechaHora;
    private TipoPagoDTO tipoPago;
    private MedioPagoDTO medioPago;
    private Integer idCuentaBancaria;
    private Integer idOrigenPago;
    private Short cuotas;
    private String autorizacion;
    private String notas;

    public PagoDTO(Pago pago) {

        this.id = pago.getId();
        this.idOrigenPago = pago.getIdOrigenPago();
        this.monto = pago.getMonto();
        this.fechaHora = pago.getFechaHora();
        this.cuotas = pago.getCuotas();
        this.idCuentaBancaria = pago.getIdCuentaBancaria();
        this.autorizacion = pago.getAutorizacion();
        this.notas = pago.getNotas();

    }

    public static PagoDTO toDTO(Pago pago) {
        if (pago == null)
            return null;

        PagoDTO dto = new PagoDTO(pago);
        /*dto.setIdCliente(presupuesto.getIdCliente());

        // dto.setMontoSenia(totalSenias(presupuesto));

        // dto.setDescuento(presupuesto.getDescuento() == null ? BigDecimal.ZERO :
        // presupuesto.getDescuento().getMonto());

        // dto.setPrecioCobrado(totalPagos(presupuesto));

        dto.setPrecioSinDescuento(presupuesto.getPrecioSinDescuento());
        dto.setPrecioMinuto(presupuesto.getPrecioMinuto());
        dto.setAprobado(presupuesto.getAprobado());
        dto.setRealizado(presupuesto.getRealizado());
        dto.setCobrado(presupuesto.getCobrado());
        dto.setEntregado(presupuesto.getEntregado());
        dto.setFechaHoraPresupuesto(presupuesto.getFechaHoraPresupuesto());
        dto.setFechaRealizado(presupuesto.getFechaRealizado());
        // dto.setFechaAprobado(presupuesto.getFechaAprobado());*/

        return dto;
    }
}
