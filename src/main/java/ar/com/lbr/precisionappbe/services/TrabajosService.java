package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Descuento;
import ar.com.lbr.precisionappbe.model.EstadoTrabajo;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.Superficie;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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

    public TrabajosService(TrabajoPresupuestadoRepository trabajosRepository,
                           PresupuestoRepository presupuestoRepository,
                           MaterialeRepository materialeRepository,
                           SuperficieRepository superficieRepository,
                           MaquinasRepository maquinasRepository, PresupuestoService presupuestoService,
                           VariosService variosService, ClienteService clienteService,
                           PresupuestoCalculadorService presupuestoCalculadorService,
                           DescuentoRepository descuentoRepository) {
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
    }

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
        entity.setPrecioCorte(dto.getPrecioCorte() != null ? dto.getPrecioCorte() : BigDecimal.ZERO);
        entity.setVinilo(dto.getVinilo() != null ? dto.getVinilo() : BigDecimal.ZERO);
        entity.setExtra(dto.getExtra() != null ? dto.getExtra() : BigDecimal.ZERO);
        entity.setVectorizado(dto.getVectorizado() != null ? dto.getVectorizado() : BigDecimal.ZERO);
        entity.setPrecioMinuto(dto.getPrecioMinuto() != null ? dto.getPrecioMinuto() : BigDecimal.ZERO);
        entity.setDescuento(dto.getDescuento());
        entity.setIdSuperficie(dto.getIdSuperficie());
        entity.setIdMaquina(dto.getIdMaquina());
        entity.setUnidades(dto.getUnidades() != null ? dto.getUnidades() : 0);
        entity.setGrabado(dto.getGrabado() != null ? dto.getGrabado() : false);
        entity.setCortesEspeciales(dto.getCortesEspeciales() != null ? dto.getCortesEspeciales() : false);
        entity.setCarteles(dto.getCarteles() != null ? dto.getCarteles() : false);
        entity.setPosicionador(dto.getPosicionador() != null ? dto.getPosicionador() : BigDecimal.ZERO);
        entity.setTraeMaterial(dto.getTraeMaterial() != null ? dto.getTraeMaterial() : false);
        entity.setPrecioSinDescuento(dto.getPrecioSinDescuento() != null ? dto.getPrecioSinDescuento() : BigDecimal.ZERO);
        entity.setMinutosPorPunto(dto.getMinutosPorPuntos());
        entity.setEstado(EstadoTrabajo.PENDIENTE);

        TrabajoPresupuestado saved = trabajosRepository.save(entity);

        Descuento descuento = new Descuento();
        descuento.setIdTipoDescuento(2);
        descuento.setIdTrabajoPresupuestado(saved.getId());
        descuento.setIdPresupuesto(dto.getIdPresupuesto());
        descuento.setMinutosDescontados(dto.getMinutosDescontados() != null ? dto.getMinutosDescontados() : 0);
        descuento.setMinutosPorPunto(dto.getMinutosPorPuntos());
        descuento.setPrecioMinuto(dto.getPrecioMinuto() != null ? dto.getPrecioMinuto() : BigDecimal.ZERO);
        descuento.setMonto(dto.getDescuento());

        descuentoRepository.save(descuento);

        return TrabajoPresupuestadoDTO.toDTO(saved);
    }

    public TrabajoPresupuestadoDTO updateSeleccionado(Integer idTrabajo, Boolean newValue) {
        TrabajoPresupuestado entity = trabajosRepository.findById(idTrabajo)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado: " + idTrabajo));
        entity.setSeleccionado(newValue);
        return TrabajoPresupuestadoDTO.toDTO(trabajosRepository.save(entity));
    }

    public TrabajoPresupuestadoDTO updateEstado(Integer idTrabajo, EstadoTrabajo nuevoEstado) {
        TrabajoPresupuestado entity = trabajosRepository.findById(idTrabajo)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado: " + idTrabajo));
        entity.setEstado(nuevoEstado);
        return TrabajoPresupuestadoDTO.toDTO(trabajosRepository.save(entity));
    }

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
}
