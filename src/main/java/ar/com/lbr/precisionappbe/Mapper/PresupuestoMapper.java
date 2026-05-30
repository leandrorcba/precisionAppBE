package ar.com.lbr.precisionappbe.Mapper;

import ar.com.lbr.precisionappbe.dto.PresupuestoDTO;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PresupuestoMapper {

    public List<PresupuestoDTO> map(List<Presupuesto> presupuestos) {

        return presupuestos.stream()
                .map(PresupuestoDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Presupuesto toEntity(PresupuestoDTO dto, boolean nuevo) {
        Presupuesto presupuesto = new Presupuesto();
        if (!nuevo) {
            presupuesto.setId(dto.getIdPresupuesto()); // solo si estás editando
        }
        presupuesto.setIdCliente(dto.getIdCliente());
        presupuesto.setPrecioSinDescuento(dto.getPrecioSinDescuento());
        presupuesto.setPrecioMinuto(dto.getPrecioMinuto());
        presupuesto.setPrecioCobrado(dto.getPrecioCobrado());
        presupuesto.setAprobado(dto.getAprobado());
        presupuesto.setRealizado(dto.getRealizado());
        presupuesto.setCobrado(dto.getCobrado());
        presupuesto.setEntregado(dto.getEntregado());
        presupuesto.setFechaHoraPresupuesto(dto.getFechaHoraPresupuesto());
        presupuesto.setFechaRealizado(dto.getFechaRealizado());
        presupuesto.setFechaCobrado(dto.getFechaCobrado());
        presupuesto.setPuntosCanjeados(0);

        return presupuesto;
    }
}
