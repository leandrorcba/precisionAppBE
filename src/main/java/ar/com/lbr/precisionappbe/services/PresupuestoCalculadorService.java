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

        String tipoCliente = tipoClienteService.getTipoClienteById(cliente.getIdTipoCliente()).getNombreTipo();

        // 1. Material (Delegado a materialesService)
        trabajo.setPrecioMaterial(obtenerPrecioMaterial(trabajo));

        // 2. Precio Minuto
        BigDecimal precioMinuto = calcularPrecioMinuto(cliente, config);
        trabajo.setPrecioMinuto(precioMinuto);


        // 3. Lógica de Corte (SRP + OCP)
        if (esTrabajoManual(trabajo)) {
            BigDecimal precioCorte = nullToZero(trabajo.getPrecioCorte());
            trabajo.setPrecioCorte(nullToZero(precioCorte));
            trabajo.setDescuento(calcularDescuento(trabajo, tipoCliente, config, precioCorte));
        } else {
            BigDecimal precioBase = precioMinuto.multiply(new BigDecimal(trabajo.getTiempoDeCorte()));
            trabajo.setPrecioSinDescuento(precioBase);

            // Buscamos la estrategia que corresponda (Strategy Pattern)
            BigDecimal descuento = calcularDescuento(trabajo, tipoCliente, config, precioBase);

            trabajo.setDescuento(descuento);
            trabajo.setPrecioCorte(precioBase.subtract(descuento));
        }

        // 4. Total Final
        trabajo.setPrecioTrabajo(calcularTotal(trabajo, trabajo));
        return trabajo;
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
        if (Boolean.TRUE.equals(t.getTraeMaterial()) || esTrabajoManual(t) || t.getIdMateriales() == null) {
            return BigDecimal.ZERO;
        }
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
        return Boolean.TRUE.equals(t.getGrabado()) || Boolean.TRUE.equals(t.getCarteles()) || Boolean.TRUE.equals(t.getCortesEspeciales());
    }

    private BigDecimal calcularPrecioMinuto(ClienteDTO cliente, VariosDTO config) {
        BigDecimal precioBase;

        String tipoCliente = tipoClienteService.getTipoClienteById(cliente.getIdTipoCliente()).getNombreTipo();

        if ("EMPRESA".equalsIgnoreCase(tipoCliente)) {
            // Empresa: client price or parameters company price
            precioBase = (cliente.getPrecioMinutoEmpresa() != null && cliente.getPrecioMinutoEmpresa().compareTo(BigDecimal.ZERO) != 0)
                    ? cliente.getPrecioMinutoEmpresa()
                    : config.getPrecioMinutoEmpresa();
        } else if ("ESTUDIANTE".equalsIgnoreCase(tipoCliente)) {
            // Estudiante: precioMinutoParametros * (1 - descuentoEstudiante / 100)
            BigDecimal precioMinutoVarios = config.getPrecioMinuto();
            BigDecimal descPercent = config.getDescuentoEstudiante() != null ? config.getDescuentoEstudiante() : BigDecimal.ZERO;
            BigDecimal factor = BigDecimal.ONE.subtract(descPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            precioBase = precioMinutoVarios.multiply(factor);
        } else {
            // Normal: base price
            precioBase = config.getPrecioMinuto();
        }

        // Retornar con 2 decimales para precisión financiera
        return precioBase.setScale(2, RoundingMode.HALF_UP);
    }
}
