package ar.com.lbr.precisionappbe.Mapper;

import ar.com.lbr.precisionappbe.dto.CierreDTO;
import ar.com.lbr.precisionappbe.model.Cierre;
import ar.com.lbr.precisionappbe.services.ClienteService;
import ar.com.lbr.precisionappbe.services.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CierresMapper {

    UserService userService;

    public CierresMapper(UserService userService) {
        this.userService = userService;
    }

    public List<CierreDTO> map(List<Cierre> clientes) {

        return clientes.stream()
                .map(CierreDTO::new)
                .collect(Collectors.toList());
    }

    public Cierre toEntity(CierreDTO dto, boolean nuevo) {
        Cierre cierre = new Cierre();
        if (!nuevo) {
            cierre.setId(dto.getId()); // solo si estás editando
        }

        cierre.setFechaCierre(dto.getFechaCierre());
        cierre.setMesCierre(dto.getMesCierre());
        cierre.setArqueo(dto.getArqueo());
        cierre.setDiferencia(dto.getDiferencia());
        cierre.setDescuentoEfectivo(dto.getDescuentoEfectivo());
        cierre.setMontoCompraMateriales(dto.getMontoCompraMateriales());
        cierre.setMontoExtracciones(dto.getMontoExtracciones());
        cierre.setDiferencia(dto.getDiferencia());
        cierre.setMontoInicial(dto.getMontoInicial());
        cierre.setMontoFinal(dto.getMontoFinal());
        cierre.setMontoPresupuestos(dto.getMontoPresupuestos());

        if (dto.getIdUsuario() != null) {
            cierre.setResponsable(userService.getUserById(dto.getIdUsuario()).getUsername());
        } else {
            cierre.setResponsable(dto.getResponsable());
        }

        cierre.setSenia(dto.getSenia());
        cierre.setVentas(dto.getVentas());

        return cierre;
    }
}
