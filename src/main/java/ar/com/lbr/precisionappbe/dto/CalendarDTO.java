package ar.com.lbr.precisionappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
//@NoArgsConstructor
public class CalendarDTO {
    private Integer id;
    private String title;

    public CalendarDTO() {
       // this.idCliente = cliente.getId();
       // this.dniCliente = cliente.getDniCliente();
    }
}
