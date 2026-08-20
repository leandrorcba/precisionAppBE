package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Cierre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CierreDTO {

    private Integer id;
    private BigDecimal montoInicial;
    private BigDecimal montoFinal;
    private BigDecimal montoExtracciones;
    private BigDecimal montoPresupuestos;
    private BigDecimal descuentoEfectivo;
    private BigDecimal arqueo;
    private BigDecimal diferencia;
    private String responsable;
    private Instant fechaCreacion;
    private Instant fechaCierre;
    private BigDecimal senia;
    private BigDecimal ventas;
    private BigDecimal montoCompraMateriales;
    private BigDecimal gastos;
    private Boolean cerrado;
    private String mesCierre;
    private Integer idUsuario;
    private BigDecimal montoTotal;

    public CierreDTO(Cierre c) {
        this.id = c.getId();
        this.montoInicial = c.getMontoInicial();
        this.montoFinal = c.getMontoFinal();
        this.montoExtracciones = c.getMontoExtracciones();
        this.montoPresupuestos = c.getMontoPresupuestos();
        this.descuentoEfectivo = c.getDescuentoEfectivo();
        this.arqueo = c.getArqueo();
        this.diferencia = c.getDiferencia();
        this.responsable = c.getResponsable();
        this.fechaCreacion = c.getFechaCreacion() != null ? c.getFechaCreacion() : c.getFechaCierre();
        this.fechaCierre = c.getFechaCierre();
        this.senia = c.getSenia();
        this.ventas = c.getVentas();
        this.montoCompraMateriales = c.getMontoCompraMateriales();
        this.gastos = c.getGastos();
        this.cerrado = c.getCerrado();
        this.mesCierre = c.getMesCierre();
        BigDecimal init = c.getMontoInicial() != null ? c.getMontoInicial() : BigDecimal.ZERO;
        BigDecimal arq = c.getArqueo() != null ? c.getArqueo() : BigDecimal.ZERO;
        this.montoTotal = init.add(arq);
    }

    public static CierreDTO toDTO(Cierre c) {
        if (c == null) {
            return null;
        }

        return new CierreDTO(c);
    }
}
