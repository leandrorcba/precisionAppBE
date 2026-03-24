package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.CuentaBancaria;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.Tarjeta;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagosService {

    private final PagoPresupuestoRepository pagoPresupuestoRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final MedioPagoRepository medioPagoRepository;

    public PagosService(PagoPresupuestoRepository pagoPresupuestoRepository,
                        TipoPagoRepository tipoPagoRepository,
                        MedioPagoRepository medioPagoRepository) {
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
        this.tipoPagoRepository = tipoPagoRepository;
        this.medioPagoRepository = medioPagoRepository;
    }

    public List<PagoDTO> getPagosByPresupuesto(Integer idPresupuesto) {
        return pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(idPresupuesto)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PagoDTO getPagoById(Integer id) {
        PagoPresupuesto pago = pagoPresupuestoRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        return toDTO(pago);
    }

    public PagoDTO createPago(PagoDTO dto) {
        PagoPresupuesto pago = new PagoPresupuesto();
        mapFromDTO(dto, pago);
        pago.setFechaHora(Instant.now());
        pago.setEnabled(true);
        return toDTO(pagoPresupuestoRepository.save(pago));
    }

    public PagoDTO updatePago(Integer id, PagoDTO dto) {
        PagoPresupuesto pago = pagoPresupuestoRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        mapFromDTO(dto, pago);
        return toDTO(pagoPresupuestoRepository.save(pago));
    }

    public void deletePago(Integer id) {
        PagoPresupuesto pago = pagoPresupuestoRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));
        pago.setEnabled(false);
        pagoPresupuestoRepository.save(pago);
    }

    private void mapFromDTO(PagoDTO dto, PagoPresupuesto pago) {
        pago.setIdPresupuesto(dto.getIdPresupuesto());
        pago.setMonto(dto.getMonto());
        pago.setCuotas(dto.getCuotas());
        pago.setAutorizacion(dto.getAutorizacion());
        pago.setNotas(dto.getNotas());

        TipoPago tipoPago = tipoPagoRepository.findById(dto.getTipoPago().getId())
                .orElseThrow(() -> new EntityNotFoundException("TipoPago no encontrado: " + dto.getTipoPago().getId()));
        pago.setIdTipoPago(tipoPago);

        MedioPago medioPago = medioPagoRepository.findById(dto.getMedioPago().getId())
                .orElseThrow(() -> new EntityNotFoundException("MedioPago no encontrado: " + dto.getMedioPago().getId()));
        pago.setIdMedioPago(medioPago);

        if (dto.getIdTarjeta() != null) {
            Tarjeta tarjeta = new Tarjeta();
            tarjeta.setId(dto.getIdTarjeta());
            pago.setIdTarjeta(tarjeta);
        } else {
            pago.setIdTarjeta(null);
        }

        if (dto.getIdCuentaBancaria() != null) {
            CuentaBancaria cuenta = new CuentaBancaria();
            cuenta.setId(dto.getIdCuentaBancaria());
            pago.setIdCuentaBancaria(cuenta);
        } else {
            pago.setIdCuentaBancaria(null);
        }
    }

    private PagoDTO toDTO(PagoPresupuesto p) {
        PagoDTO dto = new PagoDTO();
        dto.setId(p.getId());
        dto.setIdPresupuesto(p.getIdPresupuesto());
        dto.setMonto(p.getMonto());
        dto.setFechaHora(p.getFechaHora());
        dto.setCuotas(p.getCuotas());
        dto.setAutorizacion(p.getAutorizacion());
        dto.setNotas(p.getNotas());
        dto.setEnabled(p.getEnabled());

        if (p.getIdTipoPago() != null) {
            TipoPagoDTO tipoDTO = new TipoPagoDTO();
            tipoDTO.setId(p.getIdTipoPago().getId());
            tipoDTO.setTipo(p.getIdTipoPago().getTipo());
            dto.setTipoPago(tipoDTO);
        }

        if (p.getIdMedioPago() != null) {
            MedioPagoDTO medioDTO = new MedioPagoDTO();
            medioDTO.setId(p.getIdMedioPago().getId());
            medioDTO.setTipo(p.getIdMedioPago().getTipo());
            medioDTO.setDescripcion(p.getIdMedioPago().getDescripcion());
            dto.setMedioPago(medioDTO);
        }

        if (p.getIdTarjeta() != null) {
            dto.setIdTarjeta(p.getIdTarjeta().getId());
        }

        if (p.getIdCuentaBancaria() != null) {
            dto.setIdCuentaBancaria(p.getIdCuentaBancaria().getId());
        }

        return dto;
    }
}
