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
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    ClienteRepository clienteRepository;
    UtilsService utilsService;
    ClientesMapper clientesMapper;
    PuntoRepository puntoRepository;

    public ClienteService(ClienteRepository clienteRepository, ClientesMapper clientesMapper,
            UtilsService utilsService, PuntoRepository puntoRepository) {
        this.clienteRepository = clienteRepository;
        this.clientesMapper = clientesMapper;
        this.utilsService = utilsService;
        this.puntoRepository = puntoRepository;
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

        Punto punto = new Punto();
        punto.setIdCliente(cliente.getId());
        punto.setPuntosAcumulados(0);
        punto.setPuntosAcumuladosHistorico(0);
        puntoRepository.save(punto);

        return dto;
    }

    public void rehabilitarCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));
        cliente.setDisabled(false);
        clienteRepository.save(cliente);
    }

    public void deleteCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));
        cliente.setDisabled(true);
        clienteRepository.save(cliente);
    }

    public ClienteDTO updateCliente(ClienteDTO dto) {

        TipoCliente tipoCliente = utilsService.getTipoClienteById(dto.getIdTipoCliente());

        Cliente clienteEntity = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + dto.getIdCliente() + " no encontrado"));

        clientesMapper.updateEntityFromDto(dto, clienteEntity, tipoCliente);

        Cliente cliente = clienteRepository.save(clienteEntity);

        dto.setIdCliente(cliente.getId());

        return dto;
    }
}
