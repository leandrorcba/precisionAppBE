package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.ClientesMapper;
import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.PuntoDTO;
import ar.com.lbr.precisionappbe.dto.response.ClienteResponse;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Punto;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.PuntoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ar.com.lbr.precisionappbe.services.AuditLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UtilsService utilsService;
    private final ClientesMapper clientesMapper;
    private final PuntoRepository puntoRepository;
    private final FolderService folderService;
    private final AuditLogService auditLogService;

    public ClienteService(ClienteRepository clienteRepository, ClientesMapper clientesMapper,
                          UtilsService utilsService, PuntoRepository puntoRepository,
                          FolderService folderService, AuditLogService auditLogService) {
        this.clienteRepository = clienteRepository;
        this.clientesMapper = clientesMapper;
        this.utilsService = utilsService;
        this.puntoRepository = puntoRepository;
        this.folderService = folderService;
        this.auditLogService = auditLogService;
    }

    public ClienteResponse buscarClientes(String nombreCliente, Boolean mora, Integer idTipoCliente,
                                          Boolean soloDeshabilitados, Pageable pageable) {
        Page<Cliente> clientePage = clienteRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombreCliente != null && !nombreCliente.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombreCliente")), "%" + nombreCliente.toLowerCase() + "%"));
            }

            if (mora != null && mora) {
                predicates.add(cb.equal(root.get("mora"), mora));
            }

            if (idTipoCliente != null && idTipoCliente > 0) {
                predicates.add(cb.equal(root.get("idTipoCliente"), idTipoCliente));
            }

            if (Boolean.TRUE.equals(soloDeshabilitados)) {
                predicates.add(cb.isTrue(root.get("disabled")));
            } else {
                predicates.add(cb.or(cb.isFalse(root.get("disabled")), cb.isNull(root.get("disabled"))));
            }

            query.orderBy(cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        List<ClienteDTO> clienteDTOS = clientesMapper.map(clientePage.getContent());

        List<Integer> ids = clienteDTOS.stream().map(ClienteDTO::getIdCliente).collect(Collectors.toList());
        Map<Integer, PuntoDTO> puntoMap = puntoRepository.findByIdClienteIn(ids).stream()
                .collect(Collectors.toMap(Punto::getIdCliente, PuntoDTO::toDTO));

        clienteDTOS.forEach(dto -> dto.setPunto(puntoMap.get(dto.getIdCliente())));

        return new ClienteResponse(clienteDTOS, clientePage.getTotalElements());
    }

    public ClienteDTO createCliente(ClienteDTO dto) {

        TipoCliente tipoCliente = utilsService.getTipoClienteById(dto.getIdTipoCliente());

        Cliente clienteEntity = clientesMapper.toEntity(dto, tipoCliente, true);

        Cliente cliente = clienteRepository.save(clienteEntity);

        dto.setIdCliente(cliente.getId());

        folderService.crearCarpetaCliente(cliente.getNombreCliente());

        Punto punto = new Punto();
        punto.setIdCliente(cliente.getId());
        punto.setPuntosAcumulados(0);
        punto.setPuntosAcumuladosHistorico(0);
        puntoRepository.save(punto);

        auditLogService.log("CREAR", "CLIENTES", cliente.getId().toString(),
                "Cliente '" + cliente.getNombreCliente() + "' creado con tipo: " + tipoCliente.getNombreTipo(), cliente);

        return dto;
    }

    public void rehabilitarCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));
        cliente.setDisabled(false);
        clienteRepository.save(cliente);
        auditLogService.log("MODIFICAR", "CLIENTES", id.toString(),
                "Cliente '" + cliente.getNombreCliente() + "' habilitado", cliente);
    }

    public void deleteCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));
        cliente.setDisabled(true);
        clienteRepository.save(cliente);
        auditLogService.log("DESHABILITAR", "CLIENTES", id.toString(),
                "Cliente '" + cliente.getNombreCliente() + "' deshabilitado", cliente);
    }

    public ClienteDTO getClienteById(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));
        return new ClienteDTO(cliente);
    }

    public ClienteDTO updateCliente(ClienteDTO dto) {

        TipoCliente tipoCliente = utilsService.getTipoClienteById(dto.getIdTipoCliente());

        Cliente clienteEntity = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + dto.getIdCliente() + " no encontrado"));

        clientesMapper.updateEntityFromDto(dto, clienteEntity, tipoCliente);

        Cliente cliente = clienteRepository.save(clienteEntity);

        dto.setIdCliente(cliente.getId());

        folderService.crearCarpetaCliente(cliente.getNombreCliente());

        auditLogService.log("MODIFICAR", "CLIENTES", cliente.getId().toString(),
                "Cliente '" + cliente.getNombreCliente() + "' modificado (Tipo: " + tipoCliente.getNombreTipo() + ")", cliente);

        return dto;
    }


}
