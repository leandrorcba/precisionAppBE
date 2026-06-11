package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.PresupuestoMapper;
import ar.com.lbr.precisionappbe.dto.AprobarPresupuestoDTO;
import ar.com.lbr.precisionappbe.dto.DescuentoDTO;
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
import org.springframework.transaction.annotation.Transactional;
import ar.com.lbr.precisionappbe.services.AuditLogService;

import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final TipoClienteRepository tipoClienteRepository;
    private final PresupuestoMapper presupuestoMapper;
    private final PagoPresupuestoRepository pagoPresupuestoRepository;
    private final DescuentoRepository descuentoRepository;
    private final TrabajoPresupuestadoRepository trabajoPresupuestadoRepository;
    private final ClienteRepository clienteRepository;
    private final MaterialRepository materialRepository;
    private final VariosRepository variosRepository;
    private final EventsService eventsService;
    private final FolderService folderService;
    private final RemitoPdfService remitoPdfService;
    private final AuditLogService auditLogService;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
                              TipoClienteRepository tipoClienteRepository,
                              PresupuestoMapper presupuestoMapper,
                              PagoPresupuestoRepository pagoPresupuestoRepository,
                              DescuentoRepository descuentoRepository,
                              TrabajoPresupuestadoRepository trabajoPresupuestadoRepository,
                              ClienteRepository clienteRepository,
                              MaterialRepository materialRepository,
                              VariosRepository variosRepository,
                              EventsService eventsService,
                              FolderService folderService,
                              RemitoPdfService remitoPdfService,
                              AuditLogService auditLogService
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
        this.folderService = folderService;
        this.remitoPdfService = remitoPdfService;
        this.auditLogService = auditLogService;
    }

    public PresupuestoResponse buscarPresupuestoByIdCliente(Integer idCliente, Boolean habilitado, Pageable pageable) {

        Cliente cliente = clienteRepository.findById(idCliente).orElse(null);
        if (cliente != null) {
            folderService.crearCarpetaCliente(cliente.getNombreCliente());
        }

        Page<Presupuesto> presupuesto = presupuestoRepository.findByIdClienteAndHabilitadoOrderByIdDesc(idCliente, habilitado, pageable);
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
            List<Descuento> descuentos = descuentoRepository.findByIdPresupuesto(dto.getIdPresupuesto()).stream()
                    .filter(d -> d.getIdTipoDescuento() != null && d.getIdTipoDescuento() == 1)
                    .collect(Collectors.toList());
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
            dto.setDescuentos(descuentos.stream().map(d -> {
                DescuentoDTO dDto = new DescuentoDTO();
                dDto.setIdPresupuesto(d.getIdPresupuesto());
                dDto.setIdTipoDescuento(d.getIdTipoDescuento());
                dDto.setMonto(d.getMonto());
                dDto.setIdTrabajoPresupuestado(d.getIdTrabajoPresupuestado());
                dDto.setMinutosPorPunto(d.getMinutosPorPunto());
                dDto.setPrecioMinuto(d.getPrecioMinuto());
                dDto.setMinutosDescontados(d.getMinutosDescontados());
                return dDto;
            }).collect(Collectors.toList()));

            List<TrabajoPresupuestado> seleccionados = trabajoPresupuestadoRepository
                    .findByIdPresupuesto(dto.getIdPresupuesto()).stream()
                    .filter(t -> Boolean.TRUE.equals(t.getSeleccionado()))
                    .collect(Collectors.toList());
            int realizados = (int) seleccionados.stream()
                    .filter(t -> EstadoTrabajo.REALIZADO.equals(t.getEstado()))
                    .count();
            int entregados = (int) seleccionados.stream()
                    .filter(t -> EstadoTrabajo.ENTREGADO.equals(t.getEstado()))
                    .count();
            dto.setTrabajosSeleccionados(seleccionados.size());
            dto.setTrabajosRealizados(realizados);
            dto.setTrabajosEntregados(entregados);

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

        dto.setFechaHoraPresupuesto(Instant.now());
        Presupuesto presupuestoEntity = presupuestoMapper.toEntity(dto, true);

        Presupuesto presupuesto = presupuestoRepository.save(presupuestoEntity);

        // Crear carpeta física para el presupuesto dentro de la carpeta del cliente
        Cliente cliente = clienteRepository.findById(presupuesto.getIdCliente()).orElse(null);
        if (cliente != null) {
            folderService.crearCarpetaPresupuesto(cliente.getNombreCliente(), presupuesto.getId());
        }

        dto.setIdCliente(presupuesto.getId());
        actualizarPdfFisico(presupuesto.getId());

        auditLogService.log("CREAR", "PRESUPUESTOS", presupuesto.getId().toString(),
                "Presupuesto #" + presupuesto.getId() + " creado para Cliente: " + (cliente != null ? cliente.getNombreCliente() : presupuesto.getIdCliente()), presupuesto);

        return dto;
    }

    public PresupuestoDTO updatePresupuesto(PresupuestoDTO dto) {

        Presupuesto presupuestoEntity = presupuestoMapper.toEntity(dto, false);

        Presupuesto presupuesto = presupuestoRepository.save(presupuestoEntity);

        dto.setIdCliente(presupuesto.getId());
        actualizarPdfFisico(presupuesto.getId());

        auditLogService.log("MODIFICAR", "PRESUPUESTOS", presupuesto.getId().toString(),
                "Presupuesto #" + presupuesto.getId() + " actualizado", presupuesto);

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

        actualizarPdfFisico(idPresupuesto);

        auditLogService.log("APROBAR", "PRESUPUESTOS", idPresupuesto.toString(),
                "Presupuesto #" + idPresupuesto + " aprobado para Cliente: " + cliente.getNombreCliente() + " (Monto: $" + precioSinDescuento + ")", items);

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

    @Transactional
    public void cancelarPresupuesto(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));

        // Check if any job is realizado or entregado
        List<TrabajoPresupuestado> trabajos = trabajoPresupuestadoRepository.findByIdPresupuesto(idPresupuesto);
        boolean tieneTrabajoRealizado = trabajos.stream()
                .anyMatch(t -> EstadoTrabajo.REALIZADO.equals(t.getEstado()) || EstadoTrabajo.ENTREGADO.equals(t.getEstado()));

        // Check if budget is cobrado or has enabled payments
        boolean tienePagos = Boolean.TRUE.equals(presupuesto.getCobrado()) || 
                !pagoPresupuestoRepository.findByIdPresupuestoAndEnabledTrue(idPresupuesto).isEmpty();

        if (tieneTrabajoRealizado || tienePagos) {
            throw new IllegalArgumentException("No se puede cancelar el presupuesto porque tiene trabajos realizados o cobros registrados.");
        }

        // 1. Mark all jobs as seleccionado = 0
        trabajos.forEach(t -> t.setSeleccionado(false));
        trabajoPresupuestadoRepository.saveAll(trabajos);

        // 2. Delete calendar events
        eventsService.deleteEventsByPresupuesto(idPresupuesto);

        // 3. Mark budget as habilitado = false and aprobado = false
        presupuesto.setHabilitado(false);
        presupuesto.setAprobado(false);
        presupuestoRepository.save(presupuesto);

        // Actualizar PDF físico del remito para reflejar el estado cancelado
        actualizarPdfFisico(idPresupuesto);

        // Audit log
        auditLogService.log("DESHABILITAR", "PRESUPUESTOS", idPresupuesto.toString(),
                "Presupuesto #" + idPresupuesto + " cancelado/inhabilitado. Trabajos deseleccionados y eventos eliminados.");
    }

    @Transactional
    public void rehabilitarPresupuesto(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));

        presupuesto.setHabilitado(true);
        presupuestoRepository.save(presupuesto);

        // Actualizar PDF físico del remito para reflejar el estado rehabilitado
        actualizarPdfFisico(idPresupuesto);

        // Audit log
        auditLogService.log("MODIFICAR", "PRESUPUESTOS", idPresupuesto.toString(),
                "Presupuesto #" + idPresupuesto + " rehabilitado/habilitado.");
    }

    public void actualizarPdfFisico(Integer idPresupuesto) {
        try {
            Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto).orElse(null);
            if (presupuesto == null) return;
            Cliente cliente = clienteRepository.findById(presupuesto.getIdCliente()).orElse(null);
            if (cliente == null) return;

            byte[] pdfBytes = remitoPdfService.generateRemito(idPresupuesto);
            folderService.guardarPdfEnCarpeta(cliente.getNombreCliente(), idPresupuesto, pdfBytes);
        } catch (Exception e) {
            System.err.println("Error al actualizar PDF físico para presupuesto " + idPresupuesto + ": " + e.getMessage());
        }
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
