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
    private Instant fechaCierre;
    private BigDecimal senia;
    private BigDecimal ventas;
    private BigDecimal montoCompraMateriales;
    private String mesCierre;
    private Integer idUsuario;

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
        this.fechaCierre = c.getFechaCierre();
        this.senia = c.getSenia();
        this.ventas = c.getVentas();
        this.montoCompraMateriales = c.getMontoCompraMateriales();
        this.mesCierre = c.getMesCierre();
    }

    public static CierreDTO toDTO(Cierre c) {
        if (c == null) {
            return null;
        }

        return new CierreDTO(c);
    }
}
