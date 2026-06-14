package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.repositories.DescuentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DescuentoServiceTest {

    @Mock
    private DescuentoRepository descuentoRepository;

    @Test
    void instantiateService() {
        DescuentoService service = new DescuentoService(descuentoRepository);
        assertThat(service).isNotNull();
    }
}
