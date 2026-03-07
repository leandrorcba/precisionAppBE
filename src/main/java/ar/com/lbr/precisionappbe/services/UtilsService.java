package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.Superficie;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.repositories.MaquinasRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class UtilsService {

    private final SuperficieRepository superficieRepository;
    TipoClienteRepository tipoClienteRepository;
    TipoPagoRepository tipoPagoRepository;MaquinasRepository maquinaRepository;

    public UtilsService(TipoClienteRepository tipoClienteRepository,
                        TipoPagoRepository tipoPagoRepository,
                        MaquinasRepository maquinaRepository, SuperficieRepository superficieRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
        this.tipoPagoRepository = tipoPagoRepository;
        this.maquinaRepository = maquinaRepository;
        this.superficieRepository = superficieRepository;
    }

    public List<TipoCliente> getTipoCliente() {
        return tipoClienteRepository.findAll();
    }

    public TipoCliente getTipoClienteById(Integer id) {
        return tipoClienteRepository.findById(id).orElse(null);
    }

    public List<TipoPago> getTipoPago() {
        return tipoPagoRepository.findAll();
    }

    public List<Integer> getYears() {
        return buildYears(2015);
    }

    public static List<Integer> buildYears(int fromYear) {
        int currentYear = Year.now().getValue();

        return IntStream.rangeClosed(fromYear, currentYear)
                .boxed()
                .toList();
    }

    public List<Superficie> getSuperficies() {
        return superficieRepository.findAll();
    }
}
