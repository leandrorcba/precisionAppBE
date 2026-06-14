package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CostoFijoDTO;
import ar.com.lbr.precisionappbe.dto.CostoFijoRequestDTO;
import ar.com.lbr.precisionappbe.dto.TipoCostoFijoDTO;
import ar.com.lbr.precisionappbe.model.CostoFijo;

import ar.com.lbr.precisionappbe.model.TipoCostoFijo;
import ar.com.lbr.precisionappbe.repositories.CostoFijoDetalleRepository;
import ar.com.lbr.precisionappbe.repositories.CostoFijoRepository;
import ar.com.lbr.precisionappbe.repositories.TipoCostoFijoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostosFijosServiceTest {

    @Mock
    private CostoFijoRepository costoFijoRepository;

    @Mock
    private CostoFijoDetalleRepository costoFijoDetalleRepository;

    @Mock
    private TipoCostoFijoRepository tipoCostoFijoRepository;

    private CostosFijosService service;

    @BeforeEach
    void setUp() {
        service = new CostosFijosService(costoFijoRepository, costoFijoDetalleRepository, tipoCostoFijoRepository);
    }

    @Test
    void getAll_returnsTwelveMonthsOfCostosFijos() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        CostoFijo cf = new CostoFijo();
        cf.setId(1);
        cf.setPeriodo(LocalDate.of(2026, 5, 1));
        cf.setTotal(BigDecimal.valueOf(1500));

        when(costoFijoRepository.findByPeriodoBetween(start, end)).thenReturn(List.of(cf));
        when(costoFijoDetalleRepository.findByIdCostoFijo(cf)).thenReturn(Collections.emptyList());

        List<CostoFijoDTO> result = service.getAll(2026);

        assertThat(result).hasSize(12);
        CostoFijoDTO may = result.get(Month.MAY.getValue() - 1);
        assertThat(may.getTotal()).isEqualTo(BigDecimal.valueOf(1500));
    }

    @Test
    void getById_existingId_returnsDto() {
        CostoFijo cf = new CostoFijo();
        cf.setId(1);
        cf.setPeriodo(LocalDate.of(2026, 1, 1));
        cf.setTotal(BigDecimal.TEN);

        when(costoFijoRepository.findById(1)).thenReturn(Optional.of(cf));
        when(costoFijoDetalleRepository.findByIdCostoFijo(cf)).thenReturn(Collections.emptyList());

        CostoFijoDTO result = service.getById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getById_nonExistingId_throwsNotFoundException() {
        when(costoFijoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("CostoFijo no encontrado");
    }

    @Test
    void create_savesCostoFijoAndDetalles() {
        CostoFijoRequestDTO request = new CostoFijoRequestDTO();
        request.setPeriodo(LocalDate.of(2026, 6, 1));
        CostoFijoRequestDTO.DetalleItem item = new CostoFijoRequestDTO.DetalleItem();
        item.setIdTipoCostoFijo(3);
        item.setMonto(BigDecimal.valueOf(250));
        request.setDetalles(List.of(item));

        TipoCostoFijo tipo = new TipoCostoFijo();
        tipo.setId(3);
        tipo.setNombre("Luz");

        CostoFijo savedEntity = new CostoFijo();
        savedEntity.setId(10);
        savedEntity.setPeriodo(request.getPeriodo());

        when(costoFijoRepository.save(any(CostoFijo.class))).thenReturn(savedEntity);
        when(tipoCostoFijoRepository.findById(3)).thenReturn(Optional.of(tipo));

        CostoFijoDTO result = service.create(request);

        assertThat(result).isNotNull();
        verify(costoFijoDetalleRepository).saveAll(any(List.class));
    }

    @Test
    void update_existingId_savesUpdatedDetailsAndParent() {
        CostoFijo cf = new CostoFijo();
        cf.setId(2);
        cf.setPeriodo(LocalDate.of(2026, 1, 1));

        CostoFijoRequestDTO request = new CostoFijoRequestDTO();
        request.setPeriodo(LocalDate.of(2026, 1, 1));
        request.setDetalles(new ArrayList<>());

        when(costoFijoRepository.findById(2)).thenReturn(Optional.of(cf));
        when(costoFijoRepository.save(cf)).thenReturn(cf);

        CostoFijoDTO result = service.update(2, request);

        assertThat(result).isNotNull();
        verify(costoFijoDetalleRepository).deleteByIdCostoFijo(cf);
        verify(costoFijoDetalleRepository).saveAll(any(List.class));
    }

    @Test
    void delete_existingId_deletesDetailsAndParent() {
        CostoFijo cf = new CostoFijo();
        cf.setId(3);

        when(costoFijoRepository.findById(3)).thenReturn(Optional.of(cf));

        service.delete(3);

        verify(costoFijoDetalleRepository).deleteByIdCostoFijo(cf);
        verify(costoFijoRepository).delete(cf);
    }

    @Test
    void getTipos_returnsList() {
        TipoCostoFijo t1 = new TipoCostoFijo();
        t1.setId(1);
        t1.setActivo(true);

        when(tipoCostoFijoRepository.findByActivoTrue()).thenReturn(List.of(t1));

        List<TipoCostoFijoDTO> result = service.getTipos(true);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTipos_all_returnsAll() {
        TipoCostoFijo t1 = new TipoCostoFijo();
        t1.setId(1);
        t1.setActivo(false);

        when(tipoCostoFijoRepository.findAll()).thenReturn(List.of(t1));

        List<TipoCostoFijoDTO> result = service.getTipos(false);

        assertThat(result).hasSize(1);
    }

    @Test
    void createTipo_validDto_savesTipo() {
        TipoCostoFijoDTO dto = new TipoCostoFijoDTO();
        dto.setCodigo("LUZ");
        dto.setNombre("Luz de Local");

        TipoCostoFijo saved = new TipoCostoFijo();
        saved.setId(1);
        saved.setCodigo("LUZ");
        saved.setNombre("Luz de Local");
        saved.setActivo(true);

        when(tipoCostoFijoRepository.findAll()).thenReturn(Collections.emptyList());
        when(tipoCostoFijoRepository.save(any(TipoCostoFijo.class))).thenReturn(saved);

        TipoCostoFijoDTO result = service.createTipo(dto);

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("LUZ");
    }

    @Test
    void createTipo_missingCode_throwsBadRequest() {
        TipoCostoFijoDTO dto = new TipoCostoFijoDTO();
        dto.setNombre("Luz");

        assertThatThrownBy(() -> service.createTipo(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("El código es requerido");
    }

    @Test
    void createTipo_existingCode_throwsBadRequest() {
        TipoCostoFijoDTO dto = new TipoCostoFijoDTO();
        dto.setCodigo("LUZ");
        dto.setNombre("Luz");

        TipoCostoFijo existing = new TipoCostoFijo();
        existing.setCodigo("LUZ");

        when(tipoCostoFijoRepository.findAll()).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.createTipo(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe un concepto con el código");
    }

    @Test
    void updateTipo_existingId_savesUpdated() {
        TipoCostoFijo tipo = new TipoCostoFijo();
        tipo.setId(1);
        tipo.setCodigo("AGUA");
        tipo.setNombre("Agua vieja");

        TipoCostoFijoDTO dto = new TipoCostoFijoDTO();
        dto.setCodigo("AGUA");
        dto.setNombre("Agua nueva");

        when(tipoCostoFijoRepository.findById(1)).thenReturn(Optional.of(tipo));
        when(tipoCostoFijoRepository.save(tipo)).thenReturn(tipo);

        TipoCostoFijoDTO result = service.updateTipo(1, dto);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Agua nueva");
    }

    @Test
    void deleteTipo_existingId_marksAsInactive() {
        TipoCostoFijo tipo = new TipoCostoFijo();
        tipo.setId(2);
        tipo.setActivo(true);

        when(tipoCostoFijoRepository.findById(2)).thenReturn(Optional.of(tipo));

        service.deleteTipo(2);

        assertThat(tipo.getActivo()).isFalse();
        verify(tipoCostoFijoRepository).save(tipo);
    }
}
