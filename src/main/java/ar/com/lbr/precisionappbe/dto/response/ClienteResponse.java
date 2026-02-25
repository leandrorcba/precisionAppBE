package ar.com.lbr.precisionappbe.dto.response;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponse {
    List<ClienteDTO> clientes;
    Long total;
}
