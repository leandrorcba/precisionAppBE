package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Presupuesto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PresupuestoDTO {

    private String id;
    private Integer idPresupuesto;
    private Integer idCliente;
    private BigDecimal precioCobrado;
    private BigDecimal precioSinDescuento;
    private BigDecimal precioMinuto;
    private Boolean aprobado;
    private Boolean realizado;
    private Boolean cobrado;
    private Boolean entregado;
    private LocalDateTime fechaHoraPresupuesto;
    private LocalDateTime fechaRealizado;
    private LocalDateTime fechaAprobado;
    private LocalDateTime fechaCobrado;
    private LocalDateTime fechaEntregado;
    private BigDecimal montoSenia;
    private BigDecimal descuento;
    private List<PagoDTO> pagos;
    private List<PagoDTO> senias;
    private List<DescuentoDTO> descuentos;

    public PresupuestoDTO(Presupuesto presupuesto) {

        this.idPresupuesto = presupuesto.getId();
         this.idCliente = presupuesto.getIdCliente();
         this.precioSinDescuento = presupuesto.getPrecioSinDescuento();
        this.precioMinuto = presupuesto.getPrecioMinuto();
         this.aprobado = presupuesto.getAprobado();
         this.realizado = presupuesto.getRealizado();
         this.cobrado = presupuesto.getCobrado();
         this.entregado = presupuesto.getEntregado();
         this.fechaHoraPresupuesto = presupuesto.getFechaHoraPresupuesto();
         this.fechaRealizado = presupuesto.getFechaRealizado();
    }

    public static PresupuestoDTO toDTO(Presupuesto presupuesto) {
        if (presupuesto == null) {
            return null;
        }

        PresupuestoDTO dto = new PresupuestoDTO(presupuesto);
         dto.setIdCliente(presupuesto.getIdCliente());

        ///dto.setMontoSenia(totalSenias(presupuesto));

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
        // dto.setFechaAprobado(presupuesto.getFechaAprobado());

        return dto;
    }

    /*
     * public static BigDecimal totalSenias(Presupuesto p) {
     * if (p == null) return BigDecimal.ZERO;
     * 
     * return Optional.ofNullable(p.getSenias()).orElse(List.of()).stream()
     * .filter(Objects::nonNull)
     * .flatMap(s -> Optional.ofNullable(s.getPagos()).orElse(List.of()).stream())
     * .map(Pago::getMonto)
     * .filter(Objects::nonNull)
     * .reduce(BigDecimal.ZERO, BigDecimal::add);
     * }
     */

    /*
     * public static BigDecimal totalPagos(Presupuesto p) {
     * if (p == null) return BigDecimal.ZERO;
     * 
     * return Optional.ofNullable(p.getPagos()).orElse(List.of()).stream()
     * .filter(Objects::nonNull)
     * .map(Pago::getMonto)
     * .filter(Objects::nonNull)
     * .reduce(BigDecimal.ZERO, BigDecimal::add);
     * }
     */
}
