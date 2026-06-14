package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoClienteServiceTest {

    @Mock
    private TipoClienteRepository tipoClienteRepository;

    private TipoClienteService service;

    @BeforeEach
    void setUp() {
        service = new TipoClienteService(tipoClienteRepository);
    }

    @Test
    void getTipoClienteById_existing_returnsDto() {
        TipoCliente tc = new TipoCliente();
        tc.setId(1);
        tc.setNombreTipo("Empresa");

        when(tipoClienteRepository.findById(1)).thenReturn(Optional.of(tc));

        TipoClienteDTO result = service.getTipoClienteById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getNombreTipo()).isEqualTo("Empresa");
    }

    @Test
    void getTipoClienteById_notFound_returnsNull() {
        when(tipoClienteRepository.findById(99)).thenReturn(Optional.empty());

        TipoClienteDTO result = service.getTipoClienteById(99);

        assertThat(result).isNull();
    }
}
