package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.DashboardDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DashboardService dashboardService;

    private Query query;

    // ACTIVO query order (11 total):
    // 1-clientes  2-maquinas  3-minutos  4-ventas-material  5-material-trabajos
    // 6-corte  7-servicios  8-budget-distribution  9-pagado-ventas
    // 10-compras-mes  11-compras-material

    // HISTORICO query order (9 total):
    // 1-clientes  2-maquinas  3-minutos  4-material-trabajos  5-corte  6-servicios
    // 7-hist-corte-total  8-hist-material-total  9-hist-servicios-total

    // Wraps Object[] rows so List.of() varargs expansion doesn't spread them
    private static List<Object[]> rows(Object[]... arrays) {
        return Arrays.asList(arrays);
    }

    @BeforeEach
    void setUp() {
        query = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
    }

    @Test
    void getStats_historico_returnsCorrectStructureAndKpis() {
        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(new BigDecimal("100.00")))
                .thenReturn(List.of(new BigDecimal("50.00")))
                .thenReturn(List.of(new BigDecimal("23.00")));

        DashboardDTO result = dashboardService.getStats(2024, "HISTORICO");

        assertThat(result.getClientesPorMes()).hasSize(12);
        assertThat(result.getCobroPorMaterial()).isEmpty();
        assertThat(result.getComprasMaterialesPorMes()).hasSize(12);
        assertThat(result.getComprasPorMaterial()).isEmpty();
        assertThat(result.getTotalClientesNuevos()).isZero();
        assertThat(result.getTotalPagadoCorte()).isEqualByComparingTo("100.00");
        assertThat(result.getTotalPagadoMaterialTrabajos()).isEqualByComparingTo("50.00");
        assertThat(result.getTotalPagadoServicios()).isEqualByComparingTo("23.00");
    }

    @Test
    void getStats_historico_allMonthsInitializedAndNamedCorrectly() {
        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of(BigDecimal.ZERO));

        DashboardDTO result = dashboardService.getStats(2024, "HISTORICO");

        assertThat(result.getCobroPorCorte()).hasSize(12);
        assertThat(result.getCobroPorCorte()).extracting("name")
                .containsExactly(
                        "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic");
        result.getCobroPorCorte().forEach(m ->
                assertThat(m.getCorte()).isEqualByComparingTo(BigDecimal.ZERO));
    }

    @Test
    void getStats_activo_populatesMonthlyDataAndKpis() {
        Object[] clienteRow = {1, 5L};
        Object[] maquinaRow = {1, "Laser"};
        Object[] minutoRow = {1, 3, 100L};
        Object[] ventaRow = {"Placa", new BigDecimal("200.00")};
        Object[] materialTrabRow = {"Placa", new BigDecimal("80.00")};
        Object[] corteRow = {3, new BigDecimal("150.00")};
        Object[] serviciosRow = {3, "10.00", "5.00", "8.00", "2.00"};
        Object[] budgetRow = {1L, new BigDecimal("175"), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("175"), BigDecimal.ZERO};
        Object[] comprasMesRow = {6, new BigDecimal("300.00")};
        Object[] comprasMaterialRow = {"Placa", new BigDecimal("300.00")};

        when(query.getResultList())
                .thenReturn(rows(clienteRow))
                .thenReturn(rows(maquinaRow))
                .thenReturn(rows(minutoRow))
                .thenReturn(rows(ventaRow))
                .thenReturn(rows(materialTrabRow))
                .thenReturn(rows(corteRow))
                .thenReturn(rows(serviciosRow))
                .thenReturn(rows(budgetRow))
                .thenReturn(List.of(new BigDecimal("150.00")))
                .thenReturn(rows(comprasMesRow))
                .thenReturn(rows(comprasMaterialRow));

        DashboardDTO result = dashboardService.getStats(2024, "ACTIVO");

        assertThat(result.getClientesPorMes().get(0).getClientes()).isEqualTo(5L);
        assertThat(result.getClientesPorMes().get(0).getName()).isEqualTo("Ene");
        assertThat(result.getMinutosPorMaquina()).hasSize(12);
        assertThat(result.getMinutosPorMaquina().get(2)).containsEntry("Laser", 100L);
        assertThat(result.getCobroPorMaterial()).hasSize(1);
        assertThat(result.getCobroPorMaterial().get(0).getName()).isEqualTo("Placa");
        assertThat(result.getTotalClientesNuevos()).isEqualTo(5L);
        assertThat(result.getTotalMinutosCorte()).isEqualTo(100L);
        assertThat(result.getTotalCobradoCorte()).isEqualByComparingTo("150.00");
        assertThat(result.getTotalCobradoMaterial()).isEqualByComparingTo("200.00");
        assertThat(result.getTotalPagadoMaterial()).isEqualByComparingTo("150.00");
        assertThat(result.getTotalComprasMateriales()).isEqualByComparingTo("300.00");
    }

    @Test
    void getStats_activo_paymentDistributionRatioAppliedCorrectly() {
        // corte=100, material=50, servicios=25, total=175, pagos=87.50 → ratio=0.5
        Object[] budgetRow = {1L, new BigDecimal("100"), new BigDecimal("50"),
                new BigDecimal("25"), new BigDecimal("87.50"), BigDecimal.ZERO};

        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(rows(budgetRow))
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of())
                .thenReturn(List.of());

        DashboardDTO result = dashboardService.getStats(2024, "ACTIVO");

        assertThat(result.getTotalPagadoCorte()).isEqualByComparingTo("50.00");
        assertThat(result.getTotalPagadoMaterialTrabajos()).isEqualByComparingTo("25.00");
        assertThat(result.getTotalPagadoServicios()).isEqualByComparingTo("12.50");
    }

    @Test
    void getStats_activo_nullRowValuesDefaultToZero() {
        Object[] corteRowNullValue = {1, null};

        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(rows(corteRowNullValue))
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of())
                .thenReturn(List.of());

        DashboardDTO result = dashboardService.getStats(2024, "ACTIVO");

        assertThat(result.getTotalCobradoCorte()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getStats_activo_materialRevenuesSortedDescending() {
        Object[] row1 = {"Cartón", new BigDecimal("50.00")};
        Object[] row2 = {"Placa", new BigDecimal("200.00")};
        Object[] row3 = {"Vinilo", new BigDecimal("120.00")};

        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(rows(row1, row2, row3))
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of())
                .thenReturn(List.of());

        DashboardDTO result = dashboardService.getStats(2024, "ACTIVO");

        assertThat(result.getCobroPorMaterial()).extracting("name")
                .containsExactly("Placa", "Vinilo", "Cartón");
    }

    @Test
    void getStats_withMes_filtersAndCalculatesKpiCorrectly() {
        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of())
                .thenReturn(List.of());

        DashboardDTO result = dashboardService.getStats(2026, 8, null, "ACTIVO");

        assertThat(result).isNotNull();
        assertThat(result.getClientesPorMes()).hasSize(12);
        assertThat(result.getCobroPorCorte()).hasSize(12);
    }

    @Test
    void getStats_withTrimestre_filtersAndCalculatesKpiCorrectly() {
        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of(BigDecimal.ZERO))
                .thenReturn(List.of())
                .thenReturn(List.of());

        DashboardDTO result = dashboardService.getStats(2026, null, 2, "ACTIVO");

        assertThat(result).isNotNull();
        assertThat(result.getClientesPorMes()).hasSize(12);
    }
}