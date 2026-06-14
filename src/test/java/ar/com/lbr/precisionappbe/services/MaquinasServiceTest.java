package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MaquinaDTO;
import ar.com.lbr.precisionappbe.model.Maquina;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaquinasServiceTest {

    @Mock
    private MaquinasRepository maquinasRepository;

    private MaquinasService service;

    @BeforeEach
    void setUp() {
        service = new MaquinasService(maquinasRepository);
    }

    @Test
    void getAllMaquinas_returnsList() {
        Maquina m1 = new Maquina();
        m1.setId(1);
        m1.setNombreMaquina("Laser 1");
        m1.setHabilitada(true);

        when(maquinasRepository.findAll()).thenReturn(List.of(m1));

        List<MaquinaDTO> result = service.getAllMaquinas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Laser 1");
    }

    @Test
    void createMaquina_savesAndReturnsDto() {
        MaquinaDTO dto = new MaquinaDTO();
        dto.setTitle("Laser 2");
        dto.setIsHabilitada(true);

        Maquina saved = new Maquina();
        saved.setId(5);
        saved.setNombreMaquina("Laser 2");
        saved.setHabilitada(true);

        when(maquinasRepository.save(any(Maquina.class))).thenReturn(saved);

        MaquinaDTO result = service.createMaquina(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5);
    }

    @Test
    void updateMaquina_existing_savesUpdated() {
        Maquina existing = new Maquina();
        existing.setId(3);
        existing.setNombreMaquina("Old Name");
        existing.setHabilitada(true);

        MaquinaDTO dto = new MaquinaDTO();
        dto.setId(3);
        dto.setTitle("New Name");
        dto.setIsHabilitada(false);

        when(maquinasRepository.findById(3)).thenReturn(Optional.of(existing));
        when(maquinasRepository.save(existing)).thenReturn(existing);

        MaquinaDTO result = service.updateMaquina(dto);

        assertThat(result).isNotNull();
        assertThat(existing.getNombreMaquina()).isEqualTo("New Name");
        assertThat(existing.getHabilitada()).isFalse();
    }

    @Test
    void updateMaquina_nonExisting_throwsRuntimeException() {
        MaquinaDTO dto = new MaquinaDTO();
        dto.setId(99);

        when(maquinasRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateMaquina(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Maquina no encontrada");
    }
}
