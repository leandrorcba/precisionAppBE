package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrabajoPresupuestadoDTO {

    private Integer id;
    private Boolean seleccionado;
    private String archivoCad;
    private String archivoOriginal;
    private Integer idPresupuesto;
    private String material;
    private String notas;
    private Integer tiempoDeCorte;
    private Integer idMateriales;
    private BigDecimal precioMaterial;
    private BigDecimal precioTrabajo;
    private BigDecimal precioCorte;
    private BigDecimal vinilo;
    private BigDecimal extra;
    private BigDecimal vectorizado;
    private BigDecimal precioMinuto;
    private BigDecimal descuento;
    private Integer idSuperficie;
    private Integer idMaquina;
    private Integer unidades;
    private Boolean grabado;
    private Boolean cortesEspeciales;
    private Boolean carteles;
    private BigDecimal posicionador;

    public TrabajoPresupuestadoDTO(TrabajoPresupuestado tp) {
        this.id = tp.getId();
        this.seleccionado = tp.getSeleccionado();
        this.archivoCad = tp.getArchivoCad();
        this.archivoOriginal = tp.getArchivoOriginal();
        this.material = tp.getMaterial();
        this.notas = tp.getNotas();
        this.tiempoDeCorte = tp.getTiempoDeCorte();
        this.precioMaterial = tp.getPrecioMaterial();
        this.precioTrabajo = tp.getPrecioTrabajo();
        this.precioCorte = tp.getPrecioCorte();
        this.vinilo = tp.getVinilo();
        this.extra = tp.getExtra();
        this.vectorizado = tp.getVectorizado();
        this.precioMinuto = tp.getPrecioMinuto();
        this.descuento = tp.getDescuento();

        if (tp.getIdPresupuesto() != null) {
            this.idPresupuesto = tp.getIdPresupuesto();
        }
        if (tp.getIdMateriales() != null) {
            this.idMateriales = tp.getIdMateriales();
        }
        if (tp.getIdSuperficie() != null) {
            this.idSuperficie = tp.getIdSuperficie();
        }
        this.idMaquina = tp.getIdMaquina();
        this.unidades = tp.getUnidades();
        this.grabado = tp.getGrabado();
        this.cortesEspeciales = tp.getCortesEspeciales();
        this.carteles = tp.getCarteles();
        this.posicionador = tp.getPosicionador();
    }

    public static TrabajoPresupuestadoDTO toDTO(TrabajoPresupuestado tp) {
        if (tp == null) {
            return null;
        }
        return new TrabajoPresupuestadoDTO(tp);
    }
}
