package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Varios;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VariosDTO {

    private BigDecimal precioMinuto;
    private LocalTime horaInicio;
    private LocalTime horaCierre;
    private Integer ajuste;
    private BigDecimal precioMinutoEmpresa;
    private Integer descuentoEfectivo;
    private Integer descuentoPorPunto;
    private Integer minutosPorPunto;

    private LocalTime horaInicioFds;
    private LocalTime horaCierreFds;

    public VariosDTO(Varios entity) {
        this.precioMinuto = entity.getPrecioMinuto();
        this.horaInicio = entity.getHoraInicio();
        this.horaCierre = entity.getHoraCierre();
        this.ajuste = entity.getAjuste();
        this.precioMinutoEmpresa = entity.getPrecioMinutoEmpresa();
        this.descuentoEfectivo = entity.getDescuentoEfectivo();
        this.horaInicioFds = entity.getHoraInicioFds();
        this.horaCierreFds = entity.getHoraCierreFds();
        this.descuentoPorPunto = entity.getDescuentoPorPunto();
        this.minutosPorPunto = entity.getMinutosPorPunto();
    }

    public static VariosDTO toDTO(Varios entity) {
        if (entity == null) {
            return null;
        }
        return new VariosDTO(entity);
    }
}