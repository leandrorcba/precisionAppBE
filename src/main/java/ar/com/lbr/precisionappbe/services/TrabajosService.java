package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.Event;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.Superficie;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.EventsRepository;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TrabajosService {

    private final TrabajoPresupuestadoRepository trabajosRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final MaterialeRepository materialeRepository;
    private final SuperficieRepository superficieRepository;
    private final MaquinasRepository maquinasRepository;
    private final PresupuestoService presupuestoService;
    private final VariosService variosService;
    private final ClienteService clienteService;
    private final PresupuestoCalculadorService presupuestoCalculadorService;
    private final DescuentoRepository descuentoRepository;
    private final FolderService folderService;
    private final AuditLogService auditLogService;
    private final EventsRepository eventsRepository;

    public TrabajosService(TrabajoPresupuestadoRepository trabajosRepository,
                           PresupuestoRepository presupuestoRepository,
                           MaterialeRepository materialeRepository,
                           SuperficieRepository superficieRepository,
                           MaquinasRepository maquinasRepository, PresupuestoService presupuestoService,
                           VariosService variosService, ClienteService clienteService,
                           PresupuestoCalculadorService presupuestoCalculadorService,
                           DescuentoRepository descuentoRepository,
                           FolderService folderService,
                           AuditLogService auditLogService,
                           EventsRepository eventsRepository) {
        this.trabajosRepository = trabajosRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.materialeRepository = materialeRepository;
        this.superficieRepository = superficieRepository;
        this.maquinasRepository = maquinasRepository;
        this.presupuestoService = presupuestoService;
        this.variosService = variosService;
        this.clienteService = clienteService;
        this.presupuestoCalculadorService = presupuestoCalculadorService;
        this.descuentoRepository = descuentoRepository;
        this.folderService = folderService;
        this.auditLogService = auditLogService;
        this.eventsRepository = eventsRepository;
    }

    @Transactional
    public TrabajoPresupuestadoDTO createTrabajo(TrabajoPresupuestadoDTO dto) {

        Cliente cliente = presupuestoService.getClienteByPresupuestoId(dto.getIdPresupuesto());
        ClienteDTO clienteDTO = ClienteDTO.toDTO(cliente);

        presupuestoCalculadorService.calcularYValidarTrabajo(dto, clienteDTO);

        TrabajoPresupuestado entity = new TrabajoPresupuestado();
        entity.setIdPresupuesto(dto.getIdPresupuesto());
        entity.setSeleccionado(dto.getSeleccionado() != null ? dto.getSeleccionado() : false);
        entity.setArchivoCad(dto.getArchivoCad());
        entity.setArchivoOriginal(dto.getArchivoOriginal());
        entity.setNotas(dto.getNotas());
        entity.setTiempoDeCorte(dto.getTiempoDeCorte() != null ? dto.getTiempoDeCorte() : 0);
        entity.setIdMateriales(dto.getIdMateriales());
        entity.setPrecioMaterial(dto.getPrecioMaterial() != null ? dto.getPrecioMaterial() : BigDecimal.ZERO);
        entity.setPrecioTrabajo(dto.getPrecioTrabajo() != null ? dto.getPrecioTrabajo() : BigDecimal.ZERO);
        boolean isEspecial = Boolean.TRUE.equals(dto.getGrabado())
                || Boolean.TRUE.equals(dto.getCarteles())
                || Boolean.TRUE.equals(dto.getCortesEspeciales());
        entity.setVinilo(isEspecial ? BigDecimal.ZERO : (dto.getVinilo() != null ? dto.getVinilo() : BigDecimal.ZERO));
        entity.setExtra(isEspecial ? BigDecimal.ZERO : (dto.getExtra() != null ? dto.getExtra() : BigDecimal.ZERO));
        entity.setVectorizado(isEspecial ? BigDecimal.ZERO : (dto.getVectorizado() != null ? dto.getVectorizado() : BigDecimal.ZERO));
        entity.setPrecioMinuto(dto.getPrecioMinuto() != null ? dto.getPrecioMinuto() : BigDecimal.ZERO);
        entity.setDescuento(dto.getDescuento());
        entity.setIdSuperficie(dto.getIdSuperficie());
        entity.setIdMaquina(dto.getIdMaquina());
        entity.setUnidades(dto.getUnidades() != null ? dto.getUnidades() : 0);
        entity.setGrabado(dto.getGrabado() != null ? dto.getGrabado() : false);
        entity.setCortesEspeciales(dto.getCortesEspeciales() != null ? dto.getCortesEspeciales() : false);
        entity.setCarteles(dto.getCarteles() != null ? dto.getCarteles() : false);
        entity.setPosicionador(isEspecial ? BigDecimal.ZERO : (dto.getPosicionador() != null ? dto.getPosicionador() : BigDecimal.ZERO));
        entity.setTraeMaterial(dto.getTraeMaterial() != null ? dto.getTraeMaterial() : false);
        entity.setPrecioSinDescuento(dto.getPrecioSinDescuento() != null ? dto.getPrecioSinDescuento() : BigDecimal.ZERO);
        entity.setEstado(EstadoTrabajo.PENDIENTE);

        TrabajoPresupuestado saved = trabajosRepository.save(entity);

        presupuestoService.actualizarPdfFisico(dto.getIdPresupuesto());

        auditLogService.log("CREAR", "TRABAJOS", saved.getId().toString(),
                "Trabajo #" + saved.getId() + " ("
                        + (saved.getArchivoCad() != null ? saved.getArchivoCad() : "Sin archivo") + ") creado en Presupuesto #"
                        + saved.getIdPresupuesto() + " por $" + saved.getPrecioTrabajo());

        return TrabajoPresupuestadoDTO.toDTO(saved);
    }

    @Transactional
    public TrabajoPresupuestadoDTO updateSeleccionado(Integer idTrabajo, Boolean newValue) {
        TrabajoPresupuestado entity = trabajosRepository.findById(idTrabajo)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado: " + idTrabajo));

        Presupuesto presupuesto = presupuestoRepository.findById(entity.getIdPresupuesto())
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + entity.getIdPresupuesto()));

        boolean wasApproved = Boolean.TRUE.equals(presupuesto.getAprobado());

        if (Boolean.FALSE.equals(newValue)) {
            // Check if any job is realizado or entregado in this budget
            List<TrabajoPresupuestado> trabajos = trabajosRepository.findByIdPresupuesto(entity.getIdPresupuesto());
            boolean tieneTrabajoRealizado = trabajos.stream()
                    .anyMatch(t -> EstadoTrabajo.REALIZADO.equals(t.getEstado())
                            || EstadoTrabajo.ENTREGADO.equals(t.getEstado()));

            // Check if budget has payments
            boolean tienePagos = presupuestoService.tienePagos(entity.getIdPresupuesto());

            if (tieneTrabajoRealizado || tienePagos) {
                throw new IllegalArgumentException("No se puede deseleccionar el trabajo porque el presupuesto " +
                        "tiene trabajos realizados o cobros registrados.");
            }

            // If budget was approved, unmark it as approved and delete all calendar events for this budget
            if (wasApproved) {
                presupuesto.setAprobado(false);
                eventsRepository.deleteAll(eventsRepository.findByIdPresupuesto(entity.getIdPresupuesto()));
            } else {
                // If it wasn't approved, just delete the event of this specific job (if any)
                List<Event> events = eventsRepository.findByIdTrabajo(idTrabajo);
                if (events != null && !events.isEmpty()) {
                    eventsRepository.deleteAll(events);
                }
            }
        }

        entity.setSeleccionado(newValue);
        TrabajoPresupuestado saved = trabajosRepository.save(entity);

        // Recalcular el total si estaba aprobado
        if (wasApproved) {
            BigDecimal precioSinDescuento = trabajosRepository.findByIdPresupuesto(entity.getIdPresupuesto()).stream()
                    .filter(t -> Boolean.TRUE.equals(t.getSeleccionado()))
                    .map(t -> t.getPrecioTrabajo() != null ? t.getPrecioTrabajo() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            presupuesto.setPrecioSinDescuento(precioSinDescuento);
        }
        presupuestoRepository.save(presupuesto);

        propagarEstadosAPresupuesto(entity.getIdPresupuesto());

        presupuestoService.actualizarPdfFisico(entity.getIdPresupuesto());

        auditLogService.log("MODIFICAR", "TRABAJOS", idTrabajo.toString(),
                "Trabajo #" + idTrabajo + " cambió selección a: " + (newValue ? "ACTIVO" : "INACTIVO"));

        return TrabajoPresupuestadoDTO.toDTO(saved);
    }

    @Transactional
    public TrabajoPresupuestadoDTO updateEstado(Integer idTrabajo, EstadoTrabajo nuevoEstado) {
        TrabajoPresupuestado entity = trabajosRepository.findById(idTrabajo)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado: " + idTrabajo));
        entity.setEstado(nuevoEstado);
        TrabajoPresupuestado saved = trabajosRepository.save(entity);

        propagarEstadosAPresupuesto(entity.getIdPresupuesto());

        presupuestoService.actualizarPdfFisico(entity.getIdPresupuesto());

        auditLogService.log("MODIFICAR", "TRABAJOS", idTrabajo.toString(),
                "Trabajo #" + idTrabajo + " cambió estado a: " + nuevoEstado.name());

        return TrabajoPresupuestadoDTO.toDTO(saved);
    }

    private void propagarEstadosAPresupuesto(Integer idPresupuesto) {
        List<TrabajoPresupuestado> trabajos = trabajosRepository.findByIdPresupuesto(idPresupuesto);
        
        // Consider only selected jobs for state propagation
        List<TrabajoPresupuestado> trabajosSeleccionados = trabajos.stream()
                .filter(t -> Boolean.TRUE.equals(t.getSeleccionado()))
                .collect(Collectors.toList());

        if (trabajosSeleccionados.isEmpty()) {
            return;
        }

        boolean todosRealizadosOEntregados = trabajosSeleccionados.stream()
                .allMatch(t -> EstadoTrabajo.REALIZADO.equals(t.getEstado()) || EstadoTrabajo.ENTREGADO.equals(t.getEstado()));

        boolean todosEntregados = trabajosSeleccionados.stream()
                .allMatch(t -> EstadoTrabajo.ENTREGADO.equals(t.getEstado()));

        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto).orElse(null);
        if (presupuesto == null) {
            return;
        }

        if (todosRealizadosOEntregados) {
            presupuesto.setRealizado(true);
            if (presupuesto.getFechaRealizado() == null) {
                presupuesto.setFechaRealizado(Instant.now());
            }
        } else {
            presupuesto.setRealizado(false);
            presupuesto.setFechaRealizado(null);
        }
        
        presupuesto.setEntregado(todosEntregados);
        
        presupuestoRepository.save(presupuesto);
    }

    @Transactional
    public void confirmarPresupuesto(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + idPresupuesto));

        presupuesto.setAprobado(true);
        presupuestoRepository.save(presupuesto);
    }

    public List<TrabajoPresupuestadoDTO> getTrabajosByPresupuesto(Integer idPresupuesto) {
        List<TrabajoPresupuestado> trabajos = trabajosRepository.findByIdPresupuesto(idPresupuesto);

        Set<Integer> idsMateriales = trabajos.stream()
                .filter(t -> t.getIdMateriales() != null)
                .map(TrabajoPresupuestado::getIdMateriales)
                .collect(Collectors.toSet());

        Map<Integer, String> nombrePorId = materialeRepository.findAllById(idsMateriales).stream()
                .collect(Collectors.toMap(Material::getId, Material::getMateriales));

        Set<Integer> idsSuperficies = trabajos.stream()
                .filter(t -> t.getIdSuperficie() != null)
                .map(TrabajoPresupuestado::getIdSuperficie)
                .collect(Collectors.toSet());

        Map<Integer, String> superficiePorId = superficieRepository.findAllById(idsSuperficies).stream()
                .collect(Collectors.toMap(Superficie::getId, Superficie::getValor));

        Set<Integer> idsMaquinas = trabajos.stream()
                .filter(t -> t.getIdMaquina() != null)
                .map(TrabajoPresupuestado::getIdMaquina)
                .collect(Collectors.toSet());

        Map<Integer, String> maquinaPorId = maquinasRepository.findAllById(idsMaquinas).stream()
                .collect(Collectors.toMap(Maquina::getId, Maquina::getNombreMaquina));

        return trabajos.stream()
                .map(t -> {
                    TrabajoPresupuestadoDTO dto = TrabajoPresupuestadoDTO.toDTO(t);
                    if (t.getIdMateriales() != null) {
                        dto.setMaterial(nombrePorId.get(t.getIdMateriales()));
                    }
                    dto.setSuperficie(t.getIdSuperficie() != null ? superficiePorId.get(t.getIdSuperficie()) : "");
                    dto.setMaquina(t.getIdMaquina() != null ? maquinaPorId.get(t.getIdMaquina()) : "");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public TrabajoPresupuestadoDTO updateTrabajo(Integer idTrabajo, TrabajoPresupuestadoDTO dto) {
        TrabajoPresupuestado entity = trabajosRepository.findById(idTrabajo)
                .orElseThrow(() -> new EntityNotFoundException("Trabajo no encontrado: " + idTrabajo));

        Cliente cliente = presupuestoService.getClienteByPresupuestoId(dto.getIdPresupuesto());
        ClienteDTO clienteDTO = ClienteDTO.toDTO(cliente);

        presupuestoCalculadorService.calcularYValidarTrabajo(dto, clienteDTO);

        entity.setArchivoCad(dto.getArchivoCad());
        entity.setArchivoOriginal(dto.getArchivoOriginal());
        entity.setNotas(dto.getNotas());
        entity.setTiempoDeCorte(dto.getTiempoDeCorte() != null ? dto.getTiempoDeCorte() : 0);
        entity.setIdMateriales(dto.getIdMateriales());
        entity.setPrecioMaterial(dto.getPrecioMaterial() != null ? dto.getPrecioMaterial() : BigDecimal.ZERO);
        entity.setPrecioTrabajo(dto.getPrecioTrabajo() != null ? dto.getPrecioTrabajo() : BigDecimal.ZERO);
        entity.setPrecioCorte(dto.getPrecioCorte() != null ? dto.getPrecioCorte() : BigDecimal.ZERO);

        boolean isEspecial = Boolean.TRUE.equals(dto.getGrabado())
                || Boolean.TRUE.equals(dto.getCarteles())
                || Boolean.TRUE.equals(dto.getCortesEspeciales());
        entity.setVinilo(isEspecial ? BigDecimal.ZERO : (dto.getVinilo() != null ? dto.getVinilo() : BigDecimal.ZERO));
        entity.setExtra(isEspecial ? BigDecimal.ZERO : (dto.getExtra() != null ? dto.getExtra() : BigDecimal.ZERO));
        entity.setVectorizado(isEspecial ? BigDecimal.ZERO : (dto.getVectorizado() != null ? dto.getVectorizado() : BigDecimal.ZERO));
        entity.setPrecioMinuto(dto.getPrecioMinuto() != null ? dto.getPrecioMinuto() : BigDecimal.ZERO);
        entity.setDescuento(dto.getDescuento());
        entity.setIdSuperficie(dto.getIdSuperficie());
        entity.setIdMaquina(dto.getIdMaquina());
        entity.setUnidades(dto.getUnidades() != null ? dto.getUnidades() : 0);
        entity.setGrabado(dto.getGrabado() != null ? dto.getGrabado() : false);
        entity.setCortesEspeciales(dto.getCortesEspeciales() != null ? dto.getCortesEspeciales() : false);
        entity.setCarteles(dto.getCarteles() != null ? dto.getCarteles() : false);
        entity.setPosicionador(isEspecial ? BigDecimal.ZERO : (dto.getPosicionador() != null ? dto.getPosicionador() : BigDecimal.ZERO));
        entity.setTraeMaterial(dto.getTraeMaterial() != null ? dto.getTraeMaterial() : false);
        entity.setPrecioSinDescuento(dto.getPrecioSinDescuento() != null ? dto.getPrecioSinDescuento() : BigDecimal.ZERO);

        TrabajoPresupuestado saved = trabajosRepository.save(entity);

        presupuestoService.actualizarPdfFisico(dto.getIdPresupuesto());

        auditLogService.log("EDITAR", "TRABAJOS", saved.getId().toString(),
                "Trabajo #" + saved.getId() + " editado en Presupuesto #"
                        + saved.getIdPresupuesto() + " por $" + saved.getPrecioTrabajo());

        return TrabajoPresupuestadoDTO.toDTO(saved);
    }
}
