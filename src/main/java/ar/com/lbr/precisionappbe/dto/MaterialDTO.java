package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Material;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {

    private Integer id;
    private String materiales;
    private Boolean isMaterial;
    private Boolean isGrabado;
    private Boolean disabled;
    private BigDecimal stock;
    private BigDecimal stockMinimo;

    // Campos de precio unificados
    private BigDecimal precioPorUnidad;
    private Short unidades;
    private BigDecimal precioSup1;   // ID 4
    private BigDecimal precioSup3_4; // ID 3
    private BigDecimal precioSup1_2; // ID 2
    private BigDecimal precioSup1_4; // ID 1

    public MaterialDTO(Material m) {
        this.id = m.getId();
        this.materiales = m.getMateriales();
        this.isMaterial = m.getIsMaterial();
        this.isGrabado = m.getIsGrabado();
        this.disabled = m.getDisabled();
        this.stock = m.getStock();
        this.stockMinimo = m.getStockMinimo();
    }

    public static MaterialDTO toDTO(Material m) {
        if (m == null) {
            return null;
        }
        return new MaterialDTO(m);
    }
}
