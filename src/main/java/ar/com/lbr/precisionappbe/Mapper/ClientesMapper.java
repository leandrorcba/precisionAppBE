package ar.com.lbr.precisionappbe.Mapper;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientesMapper {

    public List<ClienteDTO> map(List<Cliente> clientes) {

        return clientes.stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());
    }

    public Cliente toEntity(ClienteDTO dto, TipoCliente tipoCliente, boolean nuevo) {
        Cliente cliente = new Cliente();
        if (!nuevo) {
            cliente.setId(dto.getIdCliente()); // solo si estás editando
        }
        cliente.setDniCliente(dto.getDniCliente());
        cliente.setEmailCliente(dto.getEmailCliente());
        cliente.setNombreCliente(dto.getNombreCliente());
        cliente.setTelefonoCliente(dto.getTelefonoCliente());
        // cliente.setFechaCreacion(dto.getFechaCreacion()); // opcional
        cliente.setMora(dto.getMora());
        cliente.setPrecioMinutoEmpresa(dto.getPrecioMinutoEmpresa());
        cliente.setIdTipoCliente(tipoCliente.getId()); // entidad ya buscada
        return cliente;
    }

    public void updateEntityFromDto(ClienteDTO dto, Cliente cliente, TipoCliente tipoCliente) {
        cliente.setDniCliente(dto.getDniCliente());
        cliente.setEmailCliente(dto.getEmailCliente());
        cliente.setNombreCliente(dto.getNombreCliente());
        cliente.setTelefonoCliente(dto.getTelefonoCliente());
        cliente.setMora(dto.getMora());
        cliente.setPrecioMinutoEmpresa(dto.getPrecioMinutoEmpresa());
        cliente.setIdTipoCliente(tipoCliente.getId());
    }
}
