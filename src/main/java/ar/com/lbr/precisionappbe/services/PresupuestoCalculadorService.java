package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.dto.VariosDTO;
import ar.com.lbr.precisionappbe.services.strategies.DescuentoStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PresupuestoCalculadorService {


    private MaterialesService materialesService;
    private VariosService variosService;
    private TipoClienteService tipoClienteService;
    private final List<DescuentoStrategy> estrategiasDescuento; // DIP en acción

    @Autowired
    public PresupuestoCalculadorService(MaterialesService materialesService, VariosService variosService,
                                        TipoClienteService tipoClienteService, List<DescuentoStrategy> estrategiasDescuento) {
        this.materialesService = materialesService;
        this.variosService = variosService;
        this.tipoClienteService = tipoClienteService;
        this.estrategiasDescuento = estrategiasDescuento;
    }

    public TrabajoPresupuestadoDTO calcularYValidarTrabajo(TrabajoPresupuestadoDTO trabajo, ClienteDTO cliente) {
        VariosDTO config = variosService.getVarios();
        TrabajoPresupuestadoDTO dto = new TrabajoPresupuestadoDTO();
        String tipoCliente = tipoClienteService.getTipoClienteById(cliente.getIdTipoCliente()).getNombreTipo();

        // 1. Material (Delegado a materialesService)
        dto.setPrecioMaterial(obtenerPrecioMaterial(trabajo));

        // 2. Precio Minuto
        BigDecimal precioMinuto = calcularPrecioMinuto(cliente, config);
        dto.setPrecioMinuto(precioMinuto);


        // 3. Lógica de Corte (SRP + OCP)
        if (esTrabajoManual(trabajo)) {
            BigDecimal precioCorte = nullToZero(trabajo.getPrecioCorte());
            dto.setPrecioCorte(nullToZero(precioCorte));
            dto.setDescuento(calcularDescuento(trabajo, tipoCliente, config, precioCorte));
        } else {
            BigDecimal precioBase = precioMinuto.multiply(new BigDecimal(trabajo.getTiempoDeCorte()));
            dto.setPrecioSinDescuento(precioBase);

            // Buscamos la estrategia que corresponda (Strategy Pattern)
            BigDecimal descuento = calcularDescuento(trabajo, tipoCliente, config, precioBase);

            dto.setDescuento(descuento);
            dto.setPrecioCorte(precioBase.subtract(descuento));
        }

        // 4. Total Final
        dto.setPrecioTrabajo(calcularTotal(dto, trabajo));
        return dto;
    }

    private BigDecimal calcularDescuento(TrabajoPresupuestadoDTO trabajo, String tipoCliente, VariosDTO config, BigDecimal precioBase) {
        BigDecimal descuento = estrategiasDescuento.stream()
                .filter(s -> s.aplicaA(tipoCliente))
                .findFirst()
                .map(s -> s.calcularDescuento(trabajo.getTiempoDeCorte(), precioBase, config))
                .orElse(BigDecimal.ZERO); //
        return descuento;
    }

    // Métodos privados para limpiar el flujo principal (SRP)
    private BigDecimal obtenerPrecioMaterial(TrabajoPresupuestadoDTO t) {
        if (Boolean.TRUE.equals(t.getTraeMaterial())) return BigDecimal.ZERO;
        return materialesService.calcularPrecio(t.getIdMateriales(), t.getIdSuperficie(), t.getUnidades()).getPrecio();
    }

    private BigDecimal calcularTotal(TrabajoPresupuestadoDTO dto, TrabajoPresupuestadoDTO t) {
        return dto.getPrecioCorte()
                .add(dto.getPrecioMaterial())
                .add(nullToZero(t.getVinilo()))
                .add(nullToZero(t.getVectorizado()))
                .add(nullToZero(t.getPosicionador()))
                .add(nullToZero(t.getExtra()));
    }

    private BigDecimal nullToZero(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private boolean esTrabajoManual(TrabajoPresupuestadoDTO t) {
        return t.getGrabado() == true || t.getCarteles() == true || t.getCortesEspeciales() == true;
    }

    private BigDecimal calcularPrecioMinuto(ClienteDTO cliente, VariosDTO config) {
        BigDecimal precioBase;

        BigDecimal porcentajeAjuste = BigDecimal.valueOf(config.getAjuste()).divide(new BigDecimal("100"));

        // 1. Verificar si es Empresa
        if ("EMPRESA".equalsIgnoreCase(tipoClienteService.getTipoClienteById(cliente.getIdTipoCliente()).getNombreTipo())) {
            // Prioridad 1: Precio específico del cliente
            // Prioridad 2: Si es nulo, precio_minuto_empresa de la tabla Varios
            BigDecimal precioMinuto = (cliente.getPrecioMinutoEmpresa() != null)
                    ? cliente.getPrecioMinutoEmpresa()
                    : config.getPrecioMinutoEmpresa();

            BigDecimal montoAjuste = precioMinuto.multiply(porcentajeAjuste);
            precioBase = precioMinuto.add(montoAjuste);
        } else {
            // 2. Cliente Normal o Estudiante
            // Fórmula: precioMinuto + (precioMinuto * ajuste / 100)
            BigDecimal precioMinutoVarios = config.getPrecioMinuto();

            BigDecimal montoAjuste = precioMinutoVarios.multiply(porcentajeAjuste);
            precioBase = precioMinutoVarios.add(montoAjuste);
        }

        // Retornar con 2 decimales para precisión financiera
        return precioBase.setScale(2, RoundingMode.HALF_UP);
    }
}
