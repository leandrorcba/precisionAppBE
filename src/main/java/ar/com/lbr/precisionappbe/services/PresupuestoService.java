package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.PresupuestoMapper;
import ar.com.lbr.precisionappbe.dto.AprobarPresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.PresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.response.PresupuestoResponse;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Descuento;
import ar.com.lbr.precisionappbe.model.PagoPresupuesto;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialRepository;
import ar.com.lbr.precisionappbe.repositories.PagoPresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PresupuestoService {

    PresupuestoRepository presupuestoRepository;
    TipoClienteRepository tipoClienteRepository;
    PresupuestoMapper presupuestoMapper;
    PagoPresupuestoRepository pagoPresupuestoRepository;
    DescuentoRepository descuentoRepository;
    TrabajoPresupuestadoRepository trabajoPresupuestadoRepository;
    ClienteRepository clienteRepository;
    MaterialRepository materialRepository;
    VariosRepository variosRepository;
    EventsService eventsService;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
                              TipoClienteRepository tipoClienteRepository,
                              PresupuestoMapper presupuestoMapper,
                              PagoPresupuestoRepository pagoPresupuestoRepository,
                              DescuentoRepository descuentoRepository,
                              TrabajoPresupuestadoRepository trabajoPresupuestadoRepository,
                              ClienteRepository clienteRepository,
                              MaterialRepository materialRepository,
                              VariosRepository variosRepository,
                              EventsService eventsService
    ) {
        this.presupuestoRepository = presupuestoRepository;
        this.tipoClienteRepository = tipoClienteRepository;
        this.presupuestoMapper = presupuestoMapper;
        this.pagoPresupuestoRepository = pagoPresupuestoRepository;
        this.descuentoRepository = descuentoRepository;
        this.trabajoPresupuestadoRepository = trabajoPresupuestadoRepository;
        this.clienteRepository = clienteRepository;
        this.materialRepository = materialRepository;
        this.variosRepository = variosRepository;
        this.eventsService = eventsService;
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
                    .findByIdPresupuestoAndIdTipoPago_IdAndEnabledTrue(dto.getIdPresupuesto(), 1);
            List<Descuento> descuentos = descuentoRepository.findByIdPresupuesto(dto.getIdPresupuesto());
            List<PagoPresupuesto> pagos = pagoPresupuestoRepository
                    .findByIdPresupuestoAndIdTipoPago_IdAndEnabledTrue(dto.getIdPresupuesto(), 2);


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


    public PresupuestoDTO createPresupuesto(PresupuestoDTO dto) {

        dto.setFechaHoraPresupuesto(LocalDateTime.now());
        Presupuesto presupuestoEntity = presupuestoMapper.toEntity(dto, true);

        Presupuesto presupuesto = presupuestoRepository.save(presupuestoEntity);

        dto.setIdCliente(presupuesto.getId());

        return dto;
    }

    public PresupuestoDTO updatePresupuesto(PresupuestoDTO dto) {

        Presupuesto presupuestoEntity = presupuestoMapper.toEntity(dto, false);

        Presupuesto presupuesto = presupuestoRepository.save(presupuestoEntity);

        dto.setIdCliente(presupuesto.getId());

        return dto;
    }

    public PresupuestoDTO aprobarPresupuesto(Integer idPresupuesto, List<AprobarPresupuestoDTO> items) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));

        BigDecimal precioSinDescuento = trabajoPresupuestadoRepository.findByIdPresupuesto(idPresupuesto).stream()
                .filter(t -> Boolean.TRUE.equals(t.getSeleccionado()))
                .map(t -> t.getPrecioTrabajo() != null ? t.getPrecioTrabajo() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        presupuesto.setAprobado(true);
        presupuesto.setPrecioSinDescuento(precioSinDescuento);
        presupuestoRepository.save(presupuesto);

        Map<Integer, Integer> maquinaPorTrabajo = items.stream()
                .collect(Collectors.toMap(AprobarPresupuestoDTO::getIdTrabajo, AprobarPresupuestoDTO::getIdMaquina));

        List<TrabajoPresupuestado> trabajos = trabajoPresupuestadoRepository.findAllById(maquinaPorTrabajo.keySet());
        trabajos.forEach(t -> t.setIdMaquina(maquinaPorTrabajo.get(t.getId())));
        trabajoPresupuestadoRepository.saveAll(trabajos);

        Cliente cliente = clienteRepository.findById(presupuesto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + presupuesto.getIdCliente()));

        Varios varios = variosRepository.findFirstByOrderByIdAsc();
        LocalTime horaInicio = (varios != null && varios.getHoraInicio() != null)
                ? varios.getHoraInicio() : LocalTime.of(8, 0);
        LocalTime horaCierre = (varios != null && varios.getHoraCierre() != null)
                ? varios.getHoraCierre() : LocalTime.of(18, 0);

        for (TrabajoPresupuestado trabajo : trabajos) {
            String materialNombre = "";
            if (trabajo.getIdMateriales() != null) {
                materialNombre = materialRepository.findById(trabajo.getIdMateriales())
                        .map(m -> m.getMateriales() != null ? m.getMateriales() : "")
                        .orElse("");
            }
            String precioStr = trabajo.getPrecioTrabajo() != null
                    ? trabajo.getPrecioTrabajo().toPlainString() : "0";
            String eventName = String.format("%s - %s - %d - %s",
                    cliente.getNombreCliente(), materialNombre,
                    trabajo.getTiempoDeCorte(), precioStr);

            eventsService.createEventForTrabajo(
                    trabajo.getIdMaquina(), idPresupuesto, trabajo.getId(),
                    eventName, trabajo.getTiempoDeCorte(), horaInicio, horaCierre);
        }

        return PresupuestoDTO.toDTO(presupuesto);
    }

    public String getTipoClienteByPresupuestoId(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));

        Cliente cliente = clienteRepository.findById(presupuesto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + presupuesto.getIdCliente()));

        TipoCliente tipoCliente = tipoClienteRepository.findTipoClienteById(cliente.getIdTipoCliente());

        return tipoCliente.getNombreTipo();
    }

    public Cliente getClienteByPresupuestoId(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));

        Cliente cliente = clienteRepository.findById(presupuesto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + presupuesto.getIdCliente()));
        return cliente;
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
