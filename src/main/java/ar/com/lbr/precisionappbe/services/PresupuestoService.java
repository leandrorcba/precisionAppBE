package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.PresupuestoMapper;
import ar.com.lbr.precisionappbe.dto.DescuentoDTO;
import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.PagoDTO;
import ar.com.lbr.precisionappbe.dto.PresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.TipoDescuentoDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.dto.response.PresupuestoResponse;
import ar.com.lbr.precisionappbe.model.Descuento;
import ar.com.lbr.precisionappbe.model.Pago;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PagoRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PresupuestoService {

    PresupuestoRepository presupuestoRepository;
    TipoClienteRepository tipoClienteRepository;
    PresupuestoMapper presupuestoMapper;
    PagoPresupuestoRepository pagoPresupuestoRepository;
    DescuentoRepository descuentoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
            TipoClienteRepository tipoClienteRepository,
            PresupuestoMapper presupuestoMapper,
            PagoPresupuestoRepository pagoPresupuestoRepository,
            DescuentoRepository descuentoRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.tipoClienteRepository = tipoClienteRepository;
        this.presupuestoMapper = presupuestoMapper;
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
        this.descuentoRepository = descuentoRepository;
    }

    public PresupuestoResponse buscarPresupuestoByIdCliente(Integer idCliente, Pageable pageable) {

        Page<Presupuesto> presupuesto = presupuestoRepository.findByIdClienteOrderByIdDesc(idCliente, pageable);
        List<PresupuestoDTO> presupuestoDTOS = getPresupuestoDTOS(presupuesto);

        return new PresupuestoResponse(presupuestoDTOS, presupuesto.getTotalElements());
    }

    public PresupuestoResponse buscarPresupuestoByIdPresupuesto(Integer idPresupuesto, Pageable pageable) {

        Page<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto, pageable);
        List<PresupuestoDTO> presupuestoDTOS = getPresupuestoDTOS(presupuesto);

        return new PresupuestoResponse(presupuestoDTOS, presupuesto.getTotalElements());
    }

    private List<PresupuestoDTO> getPresupuestoDTOS(Page<Presupuesto> presupuesto) {
        List<PresupuestoDTO> presupuestoDTOS = presupuestoMapper.map(presupuesto.getContent());

        presupuestoDTOS.forEach(dto -> {
            List<PagoPresupuesto> senias = pagoPresupuestoRepository
                    .findPagoPresupuestoByIdPresupuestoAndIdTipoPago_Id(dto.getIdPresupuesto(), 1);
            List<Descuento> descuentos = descuentoRepository.findDescuentoByIdPresupuesto_Id(dto.getIdPresupuesto());
            List<PagoPresupuesto> pagos = pagoPresupuestoRepository
                    .findPagoPresupuestoByIdPresupuestoAndIdTipoPago_Id(dto.getIdPresupuesto(), 2);


            BigDecimal totalSenia = senias.stream()
                    .map(PagoPresupuesto::getMonto)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDescuento = descuentos.stream()
                    .map(Descuento::getMonto)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPagos = pagos.stream()
                    .map(PagoPresupuesto::getMonto)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dto.setMontoSenia(totalSenia);
            dto.setDescuento(totalDescuento);
            dto.setPrecioCobrado(totalPagos);
            dto.setPrecioSinDescuento(dto.getPrecioSinDescuento());

            /*
             * dto.setPagos(pagos.stream().map(p -> {
             * PagoPresupuesto pagoDTO = new PagoPresupuesto();
             * pagoDTO.setId(p.getId());
             * pagoDTO.setMonto(p.getMonto());
             * pagoDTO.setFechaHora(p.getFechaHora());
             * 
             * TipoPagoDTO tipoPagoDTO = new TipoPagoDTO();
             * tipoPagoDTO.setId(p..getId());
             * tipoPagoDTO.setTipo(p.getTipoPago().getTipo());
             * pagoDTO.setTipoPago(tipoPagoDTO);
             * 
             * MedioPagoDTO medioPagoDTO = new MedioPagoDTO();
             * //medioPagoDTO.setId(p.getMedioPago().getId());
             * //medioPagoDTO.setTipo(p.getMedioPago().getTipo());
             * // medioPagoDTO.setDescripcion(p.getMedioPago().getDescripcion());
             * pagoDTO.setMedioPago(medioPagoDTO);
             * 
             * return pagoDTO;
             * }).collect(java.util.stream.Collectors.toList()));
             * 
             * dto.setSenias(senias.stream().map(p -> {
             * PagoDTO pagoDTO = new PagoDTO();
             * pagoDTO.setId(p.getId());
             * pagoDTO.setMonto(p.getMonto());
             * pagoDTO.setFechaHora(p.getFechaHora());
             * 
             * TipoPagoDTO tipoPagoDTO = new TipoPagoDTO();
             * tipoPagoDTO.setId(p.getTipoPago().getId());
             * tipoPagoDTO.setTipo(p.getTipoPago().getTipo());
             * pagoDTO.setTipoPago(tipoPagoDTO);
             * 
             * MedioPagoDTO medioPagoDTO = new MedioPagoDTO();
             * medioPagoDTO.setId(p.getMedioPago().getId());
             * medioPagoDTO.setTipo(p.getMedioPago().getTipo());
             * medioPagoDTO.setDescripcion(p.getMedioPago().getDescripcion());
             * pagoDTO.setMedioPago(medioPagoDTO);
             * 
             * return pagoDTO;
             * }).collect(java.util.stream.Collectors.toList()));
             * 
             * dto.setDescuentos(descuentos.stream().map(p -> {
             * DescuentoDTO descuentoDTO = new DescuentoDTO();
             * descuentoDTO.setMonto(p.getMonto());
             * 
             * TipoDescuentoDTO tipoDescuentoDTO = new TipoDescuentoDTO();
             * //tipoDescuentoDTO.setTipo(p.getIdTipoDescuento().getNombre());
             * descuentoDTO.setTipoDescuento(tipoDescuentoDTO);
             * 
             * return descuentoDTO;
             * }).collect(java.util.stream.Collectors.toList()));
             * 
             * BigDecimal descuento = descuentos.stream().map(p ->
             * p.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
             * BigDecimal senia = senias.stream().map(p ->
             * p.getMonto()).reduce(BigDecimal.ZERO, BigDecimal::add);
             * 
             * dto.setDescuento(descuento);
             * dto.setMontoSenia(senia);
             * 
             * dto.setPrecioCobrado(dto.getPrecioSinDescuento().subtract(dto.getDescuento())
             * .subtract(dto.getMontoSenia()));
             */

        });
        return presupuestoDTOS;
    }

    public List<TipoCliente> getTipoCliente() {
        return tipoClienteRepository.findAll();
    }

    public PresupuestoDTO createCliente(PresupuestoDTO dto) {

        /*
         * TipoCliente tipoCliente =
         * tipoClienteRepository.findById(dto.getIdTipoCliente())
         * .orElseThrow(() -> new RuntimeException("Tipo de cliente no encontrado"));
         */

        Presupuesto presupuestoEntity = presupuestoMapper.toEntity(dto, true);

        Presupuesto presupuesto = presupuestoRepository.save(presupuestoEntity);

        dto.setIdCliente(presupuesto.getId());

        return dto;
    }

    public PresupuestoDTO updateCliente(PresupuestoDTO dto) {

        /*
         * TipoCliente tipoCliente =
         * tipoClienteRepository.findById(dto.getIdTipoCliente())
         * .orElseThrow(() -> new RuntimeException("Tipo de cliente no encontrado"));
         */

        Presupuesto presupuestoEntity = presupuestoMapper.toEntity(dto, false);

        Presupuesto presupuesto = presupuestoRepository.save(presupuestoEntity);

        dto.setIdCliente(presupuesto.getId());

        return dto;
    }

    /*
     * mapPagos(List<Pago> pagos) {
     * pagos.stream().map(p -> {
     * PagoDTO pagoDTO = new PagoDTO();
     * pagoDTO.setId(p.getId());
     * pagoDTO.setMonto(p.getMonto());
     * pagoDTO.setFechaHora(p.getFechaHora());
     * 
     * TipoPagoDTO tipoPagoDTO = new TipoPagoDTO();
     * tipoPagoDTO.setId(p.getTipoPago().getId());
     * tipoPagoDTO.setTipo(p.getTipoPago().getTipo());
     * pagoDTO.setTipoPago(tipoPagoDTO);
     * 
     * MedioPagoDTO medioPagoDTO = new MedioPagoDTO();
     * medioPagoDTO.setId(p.getMedioPago().getId());
     * medioPagoDTO.setTipo(p.getMedioPago().getTipo());
     * medioPagoDTO.setDescripcion(p.getMedioPago().getDescripcion());
     * pagoDTO.setMedioPago(medioPagoDTO);
     * 
     * return pagoDTO;
     * }
     */
}
