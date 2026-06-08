package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.PrecioMaterialDTO;
import ar.com.lbr.precisionappbe.model.PrecioMateriale;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PrecioMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaterialesServiceTest {

    @Mock
    private PrecioMaterialRepository precioMaterialRepository;

    @Mock
    private MaterialeRepository materialeRepository;

    @Mock
    private AuditLogService auditLogService;

    private MaterialesService service;

    @BeforeEach
    void setUp() {
        service = new MaterialesService(materialeRepository, precioMaterialRepository, auditLogService);
    }

    // --- Por superficie ---

    @Test
    void calcularPrecio_porSuperficie_retornaPrecio() {
        PrecioMateriale entrada = precioMaterialConSuperficie(1, 3, new BigDecimal("120.00"));
        when(precioMaterialRepository.findByIdMaterialesAndIdSuperficie(1, 3)).thenReturn(entrada);

        PrecioMaterialDTO resultado = service.calcularPrecio(1, 3, null);

        assertThat(resultado.getPrecio()).isEqualByComparingTo("120.00");
    }

    @Test
    void calcularPrecio_porSuperficie_noEncontrado_lanzaExcepcion() {
        when(precioMaterialRepository.findByIdMaterialesAndIdSuperficie(1, 99)).thenReturn(null);

        assertThatThrownBy(() -> service.calcularPrecio(1, 99, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontró precio");
    }

    // --- Por cantidad: escala = 1 unidad ---

    @Test
    void calcularPrecio_porCantidad_escala1_cantidad3_retorna30() {
        // Material con precio $10 por 1 unidad → 3 unidades = $30
        PrecioMateriale entrada = precioMaterialConUnidades(5, (short) 1, new BigDecimal("10.00"));
        when(precioMaterialRepository.findFirstByIdMateriales(5)).thenReturn(entrada);

        PrecioMaterialDTO resultado = service.calcularPrecio(5, null, 3);

        assertThat(resultado.getPrecio()).isEqualByComparingTo("30.00");
    }

    // --- Por cantidad: escala = 5 unidades ---

    @Test
    void calcularPrecio_porCantidad_escala5_cantidad6_retorna54() {
        // Material con precio $45 por 5 unidades
        // 6 unidades = 1 pack ($45) + 1 suelta (45/5 = $9) = $54
        PrecioMateriale entrada = precioMaterialConUnidades(7, (short) 5, new BigDecimal("45.00"));
        when(precioMaterialRepository.findFirstByIdMateriales(7)).thenReturn(entrada);

        PrecioMaterialDTO resultado = service.calcularPrecio(7, null, 6);

        assertThat(resultado.getPrecio()).isEqualByComparingTo("54.00");
    }

    @Test
    void calcularPrecio_porCantidad_escala5_cantidad3_retorna27() {
        // 3 unidades = (3/5) * $45 = $27
        PrecioMateriale entrada = precioMaterialConUnidades(7, (short) 5, new BigDecimal("45.00"));
        when(precioMaterialRepository.findFirstByIdMateriales(7)).thenReturn(entrada);

        PrecioMaterialDTO resultado = service.calcularPrecio(7, null, 3);

        assertThat(resultado.getPrecio()).isEqualByComparingTo("27.00");
    }

    @Test
    void calcularPrecio_porCantidad_escala5_cantidad10_retorna90() {
        // 10 unidades = 2 packs completos → 2 * $45 = $90
        PrecioMateriale entrada = precioMaterialConUnidades(7, (short) 5, new BigDecimal("45.00"));
        when(precioMaterialRepository.findFirstByIdMateriales(7)).thenReturn(entrada);

        PrecioMaterialDTO resultado = service.calcularPrecio(7, null, 10);

        assertThat(resultado.getPrecio()).isEqualByComparingTo("90.00");
    }

    @Test
    void calcularPrecio_porCantidad_noEncontrado_lanzaExcepcion() {
        when(precioMaterialRepository.findFirstByIdMateriales(99)).thenReturn(null);

        assertThatThrownBy(() -> service.calcularPrecio(99, null, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontró precio");
    }

    // --- Helpers ---

    private PrecioMateriale precioMaterialConSuperficie(Integer idMaterial, Integer idSuperficie, BigDecimal precio) {
        PrecioMateriale pm = new PrecioMateriale();
        pm.setIdMateriales(idMaterial);
        pm.setIdSuperficie(idSuperficie);
        pm.setPrecioMaterial(precio);
        return pm;
    }

    private PrecioMateriale precioMaterialConUnidades(Integer idMaterial, Short unidades, BigDecimal precio) {
        PrecioMateriale pm = new PrecioMateriale();
        pm.setIdMateriales(idMaterial);
        pm.setUnidades(unidades);
        pm.setPrecioMaterial(precio);
        return pm;
    }
}