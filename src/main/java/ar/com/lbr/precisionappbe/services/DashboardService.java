package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.DashboardDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String[] MONTH_NAMES = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    public DashboardDTO getStats(int year, String origen) {
        DashboardDTO dto = new DashboardDTO();

        // 1. New clients per month
        List<DashboardDTO.MonthlyClientes> clientesPorMes = getClientesPorMes(year, origen);
        dto.setClientesPorMes(clientesPorMes);

        // 2. Machine minutes per machine per month
        List<Map<String, Object>> minutosPorMaquina = getMinutosPorMaquina(year, origen);
        dto.setMinutosPorMaquina(minutosPorMaquina);

        // 3. Material revenue (Ventas directas)
        List<DashboardDTO.MaterialRevenue> cobroPorMaterial = getCobroPorMaterialVentas(year, origen);
        dto.setCobroPorMaterial(cobroPorMaterial);

        // 3b. Materiales en Trabajos
        List<DashboardDTO.MaterialRevenue> cobroPorMaterialTrabajos = getCobroPorMaterialTrabajos(year, origen);
        dto.setCobroPorMaterialTrabajos(cobroPorMaterialTrabajos);

        // 4. Cutting revenue per month
        List<DashboardDTO.MonthlyCorte> cobroPorCorte = getCobroPorCorte(year, origen);
        dto.setCobroPorCorte(cobroPorCorte);

        // 5. Additional services revenue per month (Vectorizado, Extra, Vinilo)
        List<DashboardDTO.MonthlyServicios> cobroPorServicios = getCobroPorServicios(year, origen);
        dto.setCobroPorServicios(cobroPorServicios);

        // KPI metrics calculations
        dto.setTotalClientesNuevos(clientesPorMes.stream().mapToLong(DashboardDTO.MonthlyClientes::getClientes).sum());
        dto.setTotalMinutosCorte(minutosPorMaquina.stream().mapToLong(map -> {
            long sum = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!entry.getKey().equals("name") && entry.getValue() instanceof Number) {
                    sum += ((Number) entry.getValue()).longValue();
                }
            }
            return sum;
        }).sum());

        BigDecimal totalCorte = cobroPorCorte.stream()
                .map(DashboardDTO.MonthlyCorte::getCorte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalCobradoCorte(totalCorte);

        BigDecimal totalMaterial = cobroPorMaterial.stream()
                .map(DashboardDTO.MaterialRevenue::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalCobradoMaterial(totalMaterial);

        BigDecimal totalMaterialTrabajos = cobroPorMaterialTrabajos.stream()
                .map(DashboardDTO.MaterialRevenue::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalMaterialTrabajos(totalMaterialTrabajos);

        BigDecimal totalServicios = cobroPorServicios.stream()
                .map(s -> s.getVectorizado().add(s.getDiseno()).add(s.getVinilo()).add(s.getPosicionador()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalCobradoServicios(totalServicios);

        // Paid amounts calculations
        Map<String, BigDecimal> budgetPayments = getBudgetPaymentDistribution(year, origen);
        BigDecimal pagadoCorte = budgetPayments.get("pagadoCorte");
        BigDecimal pagadoMaterialTrabajos = budgetPayments.get("pagadoMaterialTrabajos");
        BigDecimal pagadoServicios = budgetPayments.get("pagadoServicios");
        BigDecimal pagadoVentaMateriales = getPagadoVentasMateriales(year, origen);

        if ("HISTORICO".equalsIgnoreCase(origen) || "TODOS".equalsIgnoreCase(origen)) {
            BigDecimal histCorte = getHistoricalCorteTotal(year);
            BigDecimal histMaterialTrabajos = getHistoricalMaterialTrabajosTotal(year);
            BigDecimal histServicios = getHistoricalServiciosTotal(year);

            if ("HISTORICO".equalsIgnoreCase(origen)) {
                pagadoCorte = histCorte;
                pagadoMaterialTrabajos = histMaterialTrabajos;
                pagadoServicios = histServicios;
            } else { // TODOS
                pagadoCorte = pagadoCorte.add(histCorte);
                pagadoMaterialTrabajos = pagadoMaterialTrabajos.add(histMaterialTrabajos);
                pagadoServicios = pagadoServicios.add(histServicios);
            }
        }

        dto.setTotalPagadoCorte(pagadoCorte);
        dto.setTotalPagadoMaterial(pagadoVentaMateriales);
        dto.setTotalPagadoMaterialTrabajos(pagadoMaterialTrabajos);
        dto.setTotalPagadoServicios(pagadoServicios);

        // 6. Material purchases monthly
        List<DashboardDTO.MonthlyCompras> comprasMaterialesPorMes = getComprasMaterialesPorMes(year, origen);
        dto.setComprasMaterialesPorMes(comprasMaterialesPorMes);

        // 7. Material purchases by type
        List<DashboardDTO.MaterialRevenue> comprasPorMaterial = getComprasPorMaterial(year, origen);
        dto.setComprasPorMaterial(comprasPorMaterial);

        // KPI
        BigDecimal totalCompras = comprasMaterialesPorMes.stream()
                .map(DashboardDTO.MonthlyCompras::getCompras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalComprasMateriales(totalCompras);

        return dto;
    }

    private List<DashboardDTO.MonthlyClientes> getClientesPorMes(int year, String origen) {
        String sql = "SELECT MONTH(fecha_creacion) as mes, COUNT(id_cliente) as cant " +
                "FROM clientes " +
                "WHERE YEAR(fecha_creacion) = :year AND disabled = 0 ";
        
        if ("ACTIVO".equalsIgnoreCase(origen)) {
            sql += "AND id_cliente > 20895 ";
        } else if ("HISTORICO".equalsIgnoreCase(origen)) {
            sql += "AND id_cliente <= 20895 ";
        }
        
        sql += "GROUP BY MONTH(fecha_creacion)";

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        long[] monthlyCounts = new long[12];
        for (Object res : results) {
            Object[] row = (Object[]) res;
            int mes = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            if (mes >= 1 && mes <= 12) {
                monthlyCounts[mes - 1] = count;
            }
        }

        List<DashboardDTO.MonthlyClientes> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(new DashboardDTO.MonthlyClientes(MONTH_NAMES[i], monthlyCounts[i]));
        }
        return list;
    }

    private List<Map<String, Object>> getMinutosPorMaquina(int year, String origen) {
        // Fetch all active/enabled machines to pre-populate the keys
        String sqlMaquinas = "SELECT id_maquina, nombre_maquina FROM maquinas";
        List<?> machinesResult = entityManager.createNativeQuery(sqlMaquinas).getResultList();
        Map<Integer, String> machineNames = new HashMap<>();
        for (Object res : machinesResult) {
            Object[] row = (Object[]) res;
            machineNames.put(((Number) row[0]).intValue(), (String) row[1]);
        }

        // Fetch machine minutes from jobs (active + historical)
        String sqlMinutes;
        if ("ACTIVO".equalsIgnoreCase(origen)) {
            sqlMinutes = "SELECT t.id_maquina, MONTH(p.fecha_hora_presupuesto) as mes, SUM(t.tiempo_de_corte) as total_min " +
                    "FROM trabajo_presupuestado t " +
                    "JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "WHERE YEAR(p.fecha_hora_presupuesto) = :year AND p.aprobado = 1 AND t.seleccionado = 1 AND t.id_maquina IS NOT NULL " +
                    "GROUP BY t.id_maquina, MONTH(p.fecha_hora_presupuesto)";
        } else if ("HISTORICO".equalsIgnoreCase(origen)) {
            sqlMinutes = "SELECT h.id_maquina, MONTH(h.fecha) as mes, SUM(h.tiempo_de_corte) as total_min " +
                    "FROM historico_trabajos h " +
                    "WHERE YEAR(h.fecha) = :year AND h.id_maquina IS NOT NULL " +
                    "GROUP BY h.id_maquina, MONTH(h.fecha)";
        } else { // TODOS
            sqlMinutes = "SELECT id_maquina, mes, SUM(tiempo_de_corte) as total_min FROM (" +
                    "  SELECT t.id_maquina, MONTH(p.fecha_hora_presupuesto) as mes, t.tiempo_de_corte, YEAR(p.fecha_hora_presupuesto) as anio " +
                    "  FROM trabajo_presupuestado t " +
                    "  JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "  WHERE p.aprobado = 1 AND t.seleccionado = 1 AND t.id_maquina IS NOT NULL " +
                    "  UNION ALL " +
                    "  SELECT h.id_maquina, MONTH(h.fecha) as mes, h.tiempo_de_corte, YEAR(h.fecha) as anio " +
                    "  FROM historico_trabajos h " +
                    "  WHERE h.id_maquina IS NOT NULL " +
                    ") unified " +
                    "WHERE anio = :year " +
                    "GROUP BY id_maquina, mes";
        }

        List<?> results = entityManager.createNativeQuery(sqlMinutes)
                .setParameter("year", year)
                .getResultList();

        // Initialize 12 months maps
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> monthMap = new LinkedHashMap<>();
            monthMap.put("name", MONTH_NAMES[i]);
            // Pre-fill each machine with 0 minutes
            for (String mName : machineNames.values()) {
                monthMap.put(mName, 0L);
            }
            list.add(monthMap);
        }

        // Fill minutes from results
        for (Object res : results) {
            Object[] row = (Object[]) res;
            int machineId = ((Number) row[0]).intValue();
            int mes = ((Number) row[1]).intValue();
            long minutes = ((Number) row[2]).longValue();

            if (mes >= 1 && mes <= 12) {
                String mName = machineNames.get(machineId);
                if (mName != null) {
                    Map<String, Object> monthMap = list.get(mes - 1);
                    monthMap.put(mName, minutes);
                }
            }
        }

        return list;
    }

    private List<DashboardDTO.MaterialRevenue> getCobroPorMaterialVentas(int year, String origen) {
        if ("HISTORICO".equalsIgnoreCase(origen)) {
            return new ArrayList<>();
        }

        String sqlSales = "SELECT m.materiales, SUM(v.precio_venta) as total " +
                "FROM ventas v " +
                "JOIN materiales m ON v.id_materiales = m.id_materiales " +
                "WHERE YEAR(v.fecha_hora_venta) = :year " +
                "GROUP BY m.materiales";

        List<?> salesResults = entityManager.createNativeQuery(sqlSales)
                .setParameter("year", year)
                .getResultList();

        List<DashboardDTO.MaterialRevenue> list = new ArrayList<>();
        for (Object res : salesResults) {
            Object[] row = (Object[]) res;
            String material = (String) row[0];
            BigDecimal total = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            list.add(new DashboardDTO.MaterialRevenue(material, total));
        }

        // Sort by revenue descending
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return list;
    }

    private List<DashboardDTO.MaterialRevenue> getCobroPorMaterialTrabajos(int year, String origen) {
        String sqlJobs;
        if ("ACTIVO".equalsIgnoreCase(origen)) {
            sqlJobs = "SELECT m.materiales, SUM(t.precio_material) as total " +
                    "FROM trabajo_presupuestado t " +
                    "JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "JOIN materiales m ON t.id_materiales = m.id_materiales " +
                    "WHERE YEAR(p.fecha_hora_presupuesto) = :year AND p.aprobado = 1 AND t.seleccionado = 1 " +
                    "GROUP BY m.materiales";
        } else if ("HISTORICO".equalsIgnoreCase(origen)) {
            sqlJobs = "SELECT h.material, SUM(h.precio_material) as total " +
                    "FROM historico_trabajos h " +
                    "WHERE YEAR(h.fecha) = :year " +
                    "GROUP BY h.material";
        } else { // TODOS
            sqlJobs = "SELECT material, SUM(precio_material) as total FROM (" +
                    "  SELECT m.materiales as material, t.precio_material, YEAR(p.fecha_hora_presupuesto) as anio " +
                    "  FROM trabajo_presupuestado t " +
                    "  JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "  JOIN materiales m ON t.id_materiales = m.id_materiales " +
                    "  WHERE p.aprobado = 1 AND t.seleccionado = 1 " +
                    "  UNION ALL " +
                    "  SELECT h.material, h.precio_material, YEAR(h.fecha) as anio " +
                    "  FROM historico_trabajos h " +
                    ") unified " +
                    "WHERE anio = :year " +
                    "GROUP BY material";
        }

        List<?> jobsResults = entityManager.createNativeQuery(sqlJobs)
                .setParameter("year", year)
                .getResultList();

        List<DashboardDTO.MaterialRevenue> list = new ArrayList<>();
        for (Object res : jobsResults) {
            Object[] row = (Object[]) res;
            String material = (String) row[0];
            BigDecimal total = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            list.add(new DashboardDTO.MaterialRevenue(material, total));
        }

        // Sort by revenue descending
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return list;
    }

    private List<DashboardDTO.MonthlyCorte> getCobroPorCorte(int year, String origen) {
        String sql;
        if ("ACTIVO".equalsIgnoreCase(origen)) {
            sql = "SELECT MONTH(p.fecha_hora_presupuesto) as mes, SUM(COALESCE(t.precio_corte, 0.00)) as total_corte " +
                    "FROM trabajo_presupuestado t " +
                    "JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "WHERE YEAR(p.fecha_hora_presupuesto) = :year AND p.aprobado = 1 AND t.seleccionado = 1 " +
                    "GROUP BY MONTH(p.fecha_hora_presupuesto)";
        } else if ("HISTORICO".equalsIgnoreCase(origen)) {
            sql = "SELECT MONTH(h.fecha) as mes, SUM(COALESCE(h.precio_corte, 0.00)) as total_corte " +
                    "FROM historico_trabajos h " +
                    "WHERE YEAR(h.fecha) = :year " +
                    "GROUP BY MONTH(h.fecha)";
        } else { // TODOS
            sql = "SELECT mes, SUM(precio_corte) as total_corte FROM (" +
                    "  SELECT MONTH(p.fecha_hora_presupuesto) as mes, COALESCE(t.precio_corte, 0.00) as precio_corte, YEAR(p.fecha_hora_presupuesto) as anio " +
                    "  FROM trabajo_presupuestado t " +
                    "  JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "  WHERE p.aprobado = 1 AND t.seleccionado = 1 " +
                    "  UNION ALL " +
                    "  SELECT MONTH(h.fecha) as mes, COALESCE(h.precio_corte, 0.00) as precio_corte, YEAR(h.fecha) as anio " +
                    "  FROM historico_trabajos h " +
                    ") unified " +
                    "WHERE anio = :year " +
                    "GROUP BY mes";
        }

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        BigDecimal[] monthlyAmounts = new BigDecimal[12];
        Arrays.fill(monthlyAmounts, BigDecimal.ZERO);

        for (Object res : results) {
            Object[] row = (Object[]) res;
            int mes = ((Number) row[0]).intValue();
            BigDecimal amount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            if (mes >= 1 && mes <= 12) {
                monthlyAmounts[mes - 1] = amount;
            }
        }

        List<DashboardDTO.MonthlyCorte> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(new DashboardDTO.MonthlyCorte(MONTH_NAMES[i], monthlyAmounts[i]));
        }
        return list;
    }

    private List<DashboardDTO.MonthlyServicios> getCobroPorServicios(int year, String origen) {
        String sql;
        if ("ACTIVO".equalsIgnoreCase(origen)) {
            sql = "SELECT MONTH(p.fecha_hora_presupuesto) as mes, " +
                    "SUM(COALESCE(t.vectorizado, 0.00)) as total_vectorizado, " +
                    "SUM(COALESCE(t.extra, 0.00)) as total_diseno, " +
                    "SUM(COALESCE(t.vinilo, 0.00)) as total_vinilo, " +
                    "SUM(COALESCE(t.posicionador, 0.00)) as total_posicionador " +
                    "FROM trabajo_presupuestado t " +
                    "JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "WHERE YEAR(p.fecha_hora_presupuesto) = :year AND p.aprobado = 1 AND t.seleccionado = 1 " +
                    "GROUP BY MONTH(p.fecha_hora_presupuesto)";
        } else if ("HISTORICO".equalsIgnoreCase(origen)) {
            sql = "SELECT MONTH(h.fecha) as mes, " +
                    "SUM(COALESCE(h.vectorizado, 0.00)) as total_vectorizado, " +
                    "SUM(COALESCE(h.extra, 0.00)) as total_diseno, " +
                    "SUM(COALESCE(h.vinilo, 0.00)) as total_vinilo, " +
                    "0.00 as total_posicionador " +
                    "FROM historico_trabajos h " +
                    "WHERE YEAR(h.fecha) = :year " +
                    "GROUP BY MONTH(h.fecha)";
        } else { // TODOS
            sql = "SELECT mes, SUM(vectorizado) as total_vectorizado, SUM(diseno) as total_diseno, SUM(vinilo) as total_vinilo, SUM(posicionador) as total_posicionador FROM (" +
                    "  SELECT MONTH(p.fecha_hora_presupuesto) as mes, " +
                    "         COALESCE(t.vectorizado, 0.00) as vectorizado, " +
                    "         COALESCE(t.extra, 0.00) as diseno, " +
                    "         COALESCE(t.vinilo, 0.00) as vinilo, " +
                    "         COALESCE(t.posicionador, 0.00) as posicionador, " +
                    "         YEAR(p.fecha_hora_presupuesto) as anio " +
                    "  FROM trabajo_presupuestado t " +
                    "  JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto " +
                    "  WHERE p.aprobado = 1 AND t.seleccionado = 1 " +
                    "  UNION ALL " +
                    "  SELECT MONTH(h.fecha) as mes, " +
                    "         COALESCE(h.vectorizado, 0.00) as vectorizado, " +
                    "         COALESCE(h.extra, 0.00) as diseno, " +
                    "         COALESCE(h.vinilo, 0.00) as vinilo, " +
                    "         0.00 as posicionador, " +
                    "         YEAR(h.fecha) as anio " +
                    "  FROM historico_trabajos h " +
                    ") unified " +
                    "WHERE anio = :year " +
                    "GROUP BY mes";
        }

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        BigDecimal[] vecAmounts = new BigDecimal[12];
        BigDecimal[] disAmounts = new BigDecimal[12];
        BigDecimal[] vinAmounts = new BigDecimal[12];
        BigDecimal[] posAmounts = new BigDecimal[12];
        Arrays.fill(vecAmounts, BigDecimal.ZERO);
        Arrays.fill(disAmounts, BigDecimal.ZERO);
        Arrays.fill(vinAmounts, BigDecimal.ZERO);
        Arrays.fill(posAmounts, BigDecimal.ZERO);

        for (Object res : results) {
            Object[] row = (Object[]) res;
            int mes = ((Number) row[0]).intValue();
            BigDecimal vec = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            BigDecimal dis = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            BigDecimal vin = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;
            BigDecimal pos = row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO;

            if (mes >= 1 && mes <= 12) {
                vecAmounts[mes - 1] = vec;
                disAmounts[mes - 1] = dis;
                vinAmounts[mes - 1] = vin;
                posAmounts[mes - 1] = pos;
            }
        }

        List<DashboardDTO.MonthlyServicios> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(new DashboardDTO.MonthlyServicios(MONTH_NAMES[i], vecAmounts[i], disAmounts[i], vinAmounts[i], posAmounts[i]));
        }
        return list;
    }

    private List<DashboardDTO.MonthlyCompras> getComprasMaterialesPorMes(int year, String origen) {
        if ("HISTORICO".equalsIgnoreCase(origen)) {
            List<DashboardDTO.MonthlyCompras> list = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                list.add(new DashboardDTO.MonthlyCompras(MONTH_NAMES[i], BigDecimal.ZERO));
            }
            return list;
        }

        String sql = "SELECT MONTH(fecha_hora_compra) as mes, SUM(COALESCE(monto_total, 0.00)) as total " +
                "FROM compra_materiales " +
                "WHERE YEAR(fecha_hora_compra) = :year " +
                "GROUP BY MONTH(fecha_hora_compra)";

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        BigDecimal[] monthlyAmounts = new BigDecimal[12];
        Arrays.fill(monthlyAmounts, BigDecimal.ZERO);

        for (Object res : results) {
            Object[] row = (Object[]) res;
            int mes = ((Number) row[0]).intValue();
            BigDecimal amount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            if (mes >= 1 && mes <= 12) {
                monthlyAmounts[mes - 1] = amount;
            }
        }

        List<DashboardDTO.MonthlyCompras> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(new DashboardDTO.MonthlyCompras(MONTH_NAMES[i], monthlyAmounts[i]));
        }
        return list;
    }

    private List<DashboardDTO.MaterialRevenue> getComprasPorMaterial(int year, String origen) {
        if ("HISTORICO".equalsIgnoreCase(origen)) {
            return new ArrayList<>();
        }

        String sql = "SELECT material, SUM(COALESCE(monto_total, 0.00)) as total " +
                "FROM compra_materiales " +
                "WHERE YEAR(fecha_hora_compra) = :year " +
                "GROUP BY material";

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        List<DashboardDTO.MaterialRevenue> list = new ArrayList<>();
        for (Object res : results) {
            Object[] row = (Object[]) res;
            String material = (String) row[0];
            BigDecimal total = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            list.add(new DashboardDTO.MaterialRevenue(material, total));
        }

        // Sort descending
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return list;
    }

    private Map<String, BigDecimal> getBudgetPaymentDistribution(int year, String origen) {
        Map<String, BigDecimal> distribution = new HashMap<>();
        distribution.put("pagadoCorte", BigDecimal.ZERO);
        distribution.put("pagadoMaterialTrabajos", BigDecimal.ZERO);
        distribution.put("pagadoServicios", BigDecimal.ZERO);

        if ("HISTORICO".equalsIgnoreCase(origen)) {
            return distribution;
        }

        String sql = "SELECT p.id_presupuesto, " +
                "       SUM(COALESCE(t.precio_corte, 0.00)) as corte, " +
                "       SUM(COALESCE(t.precio_material, 0.00)) as material, " +
                "       SUM(COALESCE(t.vectorizado, 0.00) + COALESCE(t.extra, 0.00) + COALESCE(t.vinilo, 0.00) + COALESCE(t.posicionador, 0.00)) as servicios, " +
                "       (SELECT SUM(COALESCE(pp.monto, 0.00)) FROM pago_presupuesto pp WHERE pp.id_presupuesto = p.id_presupuesto AND pp.enabled = 1) as pagos, " +
                "       (SELECT SUM(COALESCE(d.monto, 0.00)) FROM descuento d WHERE d.id_presupuesto = p.id_presupuesto AND d.id_tipo_descuento = 1) as descuentos " +
                "FROM presupuesto p " +
                "JOIN trabajo_presupuestado t ON t.id_presupuesto = p.id_presupuesto " +
                "WHERE YEAR(p.fecha_hora_presupuesto) = :year AND p.aprobado = 1 AND t.seleccionado = 1 " +
                "GROUP BY p.id_presupuesto";

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        BigDecimal pagadoCorte = BigDecimal.ZERO;
        BigDecimal pagadoMaterialTrabajos = BigDecimal.ZERO;
        BigDecimal pagadoServicios = BigDecimal.ZERO;

        for (Object res : results) {
            Object[] row = (Object[]) res;
            BigDecimal corte = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            BigDecimal material = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            BigDecimal servicios = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;
            BigDecimal pagos = row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO;
            BigDecimal descuentos = row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO;

            BigDecimal totalCovered = pagos.add(descuentos);
            BigDecimal expectedTotal = corte.add(material).add(servicios);
            if (expectedTotal.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = totalCovered.divide(expectedTotal, 10, java.math.RoundingMode.HALF_UP);
                pagadoCorte = pagadoCorte.add(corte.multiply(ratio));
                pagadoMaterialTrabajos = pagadoMaterialTrabajos.add(material.multiply(ratio));
                pagadoServicios = pagadoServicios.add(servicios.multiply(ratio));
            }
        }

        distribution.put("pagadoCorte", pagadoCorte.setScale(2, java.math.RoundingMode.HALF_UP));
        distribution.put("pagadoMaterialTrabajos", pagadoMaterialTrabajos.setScale(2, java.math.RoundingMode.HALF_UP));
        distribution.put("pagadoServicios", pagadoServicios.setScale(2, java.math.RoundingMode.HALF_UP));

        return distribution;
    }

    private BigDecimal getPagadoVentasMateriales(int year, String origen) {
        if ("HISTORICO".equalsIgnoreCase(origen)) {
            return BigDecimal.ZERO;
        }

        String sql = "SELECT SUM(COALESCE(pv.monto, 0.00)) " +
                "FROM pago_venta pv " +
                "JOIN ventas v ON pv.id_venta = v.id_ventas " +
                "WHERE YEAR(v.fecha_hora_venta) = :year";

        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        if (results != null && !results.isEmpty() && results.get(0) != null) {
            return new BigDecimal(results.get(0).toString());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getHistoricalCorteTotal(int year) {
        String sql = "SELECT SUM(COALESCE(h.precio_corte, 0.00)) FROM historico_trabajos h WHERE YEAR(h.fecha) = :year";
        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();
        if (results != null && !results.isEmpty() && results.get(0) != null) {
            return new BigDecimal(results.get(0).toString());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getHistoricalMaterialTrabajosTotal(int year) {
        String sql = "SELECT SUM(COALESCE(h.precio_material, 0.00)) FROM historico_trabajos h WHERE YEAR(h.fecha) = :year";
        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();
        if (results != null && !results.isEmpty() && results.get(0) != null) {
            return new BigDecimal(results.get(0).toString());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getHistoricalServiciosTotal(int year) {
        String sql = "SELECT SUM(COALESCE(h.vectorizado, 0.00) + COALESCE(h.extra, 0.00) + COALESCE(h.vinilo, 0.00)) " +
                "FROM historico_trabajos h WHERE YEAR(h.fecha) = :year";
        List<?> results = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();
        if (results != null && !results.isEmpty() && results.get(0) != null) {
            return new BigDecimal(results.get(0).toString());
        }
        return BigDecimal.ZERO;
    }
}
