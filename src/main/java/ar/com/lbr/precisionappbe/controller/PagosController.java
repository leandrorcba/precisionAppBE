package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {

    private final PagoPresupuestoRepository pagoPresupuestoRepository;

    public PagosController(PagoPresupuestoRepository pagoPresupuestoRepository) {
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
    }

    @GetMapping("/{idPresupuesto}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> getPagosByPresupuesto(@PathVariable Integer idPresupuesto) {
        List<PagoPresupuesto> pagos = pagoPresupuestoRepository.findPagoPresupuestoByIdPresupuesto(idPresupuesto);

        List<PagoDTO> pagoDTOs = pagos.stream().map(p -> {
            PagoDTO dto = new PagoDTO();
            dto.setId(p.getId());
            dto.setMonto(p.getMonto());
            dto.setFechaHora(
                    p.getFechaHora() != null ? p.getFechaHora().atZone(java.time.ZoneId.systemDefault()).toInstant()
                            : null);
            dto.setCuotas(p.getCuotas());
            dto.setAutorizacion(p.getAutorizacion());
            dto.setNotas(p.getNotas());

            if (p.getIdCuentaBancaria() != null) {
                dto.setIdCuentaBancaria(p.getIdCuentaBancaria().getId());
            }

            if (p.getIdTipoPago() != null) {
                ar.com.lbr.precisionappbe.dto.TipoPagoDTO tipoDto = new ar.com.lbr.precisionappbe.dto.TipoPagoDTO();
                tipoDto.setId(p.getIdTipoPago().getId());
                tipoDto.setTipo(p.getIdTipoPago().getTipo());
                dto.setTipoPago(tipoDto);
            }

            if (p.getIdMedioPago() != null) {
                ar.com.lbr.precisionappbe.dto.MedioPagoDTO medioDto = new ar.com.lbr.precisionappbe.dto.MedioPagoDTO();
                medioDto.setId(p.getIdMedioPago().getId());
                medioDto.setTipo(p.getIdMedioPago().getTipo());
                medioDto.setDescripcion(p.getIdMedioPago().getDescripcion());
                dto.setMedioPago(medioDto);
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseBuilder.ok("Pagos obtenidos con éxito", pagoDTOs, (long) pagoDTOs.size());
    }


}
