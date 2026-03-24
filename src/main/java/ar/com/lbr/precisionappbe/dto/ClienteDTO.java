package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

    private String id;
    private Integer idCliente;
    private String dniCliente;
    private String emailCliente;
    private String nombreCliente;
    private String telefonoCliente;
    private Instant fechaCreacion;
    private Boolean mora;
    private BigDecimal precioMinutoEmpresa;
    private Integer idTipoCliente;
    private PuntoDTO punto;

    public ClienteDTO(Cliente cliente) {
        this.idCliente = cliente.getId();
        this.dniCliente = cliente.getDniCliente();
        this.emailCliente = cliente.getEmailCliente();
        this.nombreCliente = cliente.getNombreCliente();
        this.telefonoCliente = cliente.getTelefonoCliente();
        this.fechaCreacion = cliente.getFechaCreacion();
        this.mora = cliente.getMora();
        this.precioMinutoEmpresa = cliente.getPrecioMinutoEmpresa();
        this.idTipoCliente = cliente.getIdTipoCliente();
    }
}
