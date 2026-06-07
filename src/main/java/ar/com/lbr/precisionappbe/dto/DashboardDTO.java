package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    private List<MonthlyClientes> clientesPorMes;
    private List<Map<String, Object>> minutosPorMaquina;
    private List<MaterialRevenue> cobroPorMaterial;
    private List<MaterialRevenue> cobroPorMaterialTrabajos;
    private List<MonthlyCorte> cobroPorCorte;
    private List<MonthlyServicios> cobroPorServicios;
    private List<MonthlyCompras> comprasMaterialesPorMes;
    private List<MaterialRevenue> comprasPorMaterial;

    // KPI Metrics
    private Long totalClientesNuevos;
    private Long totalMinutosCorte;
    private BigDecimal totalCobradoCorte;
    private BigDecimal totalPagadoCorte;
    private BigDecimal totalCobradoMaterial;
    private BigDecimal totalPagadoMaterial;
    private BigDecimal totalMaterialTrabajos;
    private BigDecimal totalPagadoMaterialTrabajos;
    private BigDecimal totalCobradoServicios;
    private BigDecimal totalPagadoServicios;
    private BigDecimal totalComprasMateriales;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyClientes {
        private String name;
        private Long clientes;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaterialRevenue {
        private String name;
        private BigDecimal value;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyCorte {
        private String name;
        private BigDecimal corte;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyServicios {
        private String name;
        private BigDecimal vectorizado;
        private BigDecimal diseno;
        private BigDecimal vinilo;
        private BigDecimal posicionador;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyCompras {
        private String name;
        private BigDecimal compras;
    }
}
