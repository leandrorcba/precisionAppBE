package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.Mapper.ClientesMapper;
import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.response.ClienteResponse;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    ClienteRepository clienteRepository;
    UtilsService utilsService;
    ClientesMapper clientesMapper;

    public ClienteService(ClienteRepository clienteRepository, ClientesMapper clientesMapper, UtilsService utilsService) {
        this.clienteRepository = clienteRepository;
        this.clientesMapper = clientesMapper;
        this.utilsService = utilsService;
    }

    public ClienteResponse buscarClientes(String nombreCliente, Boolean mora, Integer idTipoCliente, Pageable pageable) {
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

            query.orderBy(cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        List<ClienteDTO> clienteDTOS = clientesMapper.map(clientePage.getContent());
        return new ClienteResponse(clienteDTOS, clientePage.getTotalElements());
    }

    public ClienteDTO createCliente(ClienteDTO dto) {

        TipoCliente tipoCliente = utilsService.getTipoClienteById(dto.getIdTipoCliente());

        Cliente clienteEntity = clientesMapper.toEntity(dto, tipoCliente, true);

        Cliente cliente = clienteRepository.save(clienteEntity);

        dto.setIdCliente(cliente.getId());

        return dto;
    }

    public ClienteDTO updateCliente(ClienteDTO dto) {

        TipoCliente tipoCliente = utilsService.getTipoClienteById(dto.getIdTipoCliente());

        Cliente clienteEntity = clientesMapper.toEntity(dto, tipoCliente, false);

        Cliente cliente = clienteRepository.save(clienteEntity);

        dto.setIdCliente(cliente.getId());

        return dto;
    }
}
