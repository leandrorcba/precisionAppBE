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
