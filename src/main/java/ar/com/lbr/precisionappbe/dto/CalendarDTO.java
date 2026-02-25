package ar.com.lbr.precisionappbe.dto;

import ar.com.lbr.precisionappbe.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

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
