package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.response.ClienteResponse;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.services.ClienteService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "ClientesController", description = "Controller de manejo de Clientes")
public class ClienteController {

    ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(
            summary = "Get All Clientes",
            description = "Trae un listado de todos los clientes",
            tags = {"Clientes"},
            /*requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponse.class)
                    )
            ),*/
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponse.class)
                    )
            )
    )
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> getClientes(@RequestParam(defaultValue = "0") int start,
                                                                     @RequestParam(defaultValue = "10") int limit,
                                                                     @RequestParam(required = false) String nombreCliente,
                                                                     @RequestParam(required = false) Boolean mora,
                                                                     @RequestParam(required = false) Integer idTipoCliente) {


        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit);

        ClienteResponse clienteResponse = clienteService.buscarClientes(nombreCliente, mora, idTipoCliente, pageable);

        if (!clienteResponse.getClientes().isEmpty()) {
            return ResponseBuilder.ok("Listado obtenido con éxito", clienteResponse.getClientes(), clienteResponse.getTotal());
        }

        return ResponseBuilder.error("No se encontraron clientes", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ClienteDTO>> createCliente(@RequestBody ClienteDTO cliente) {
        ClienteDTO clienteDto = clienteService.createCliente(cliente);
        return ResponseBuilder.ok("Listado obtenido con éxito", clienteDto, 0L);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<ClienteDTO>> updateCliente(@RequestBody ClienteDTO cliente) {
        ClienteDTO clienteDto = clienteService.updateCliente(cliente);
        return ResponseBuilder.ok("Listado obtenido con éxito", clienteDto, 0L);
    }

}
