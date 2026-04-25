package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.SuperficieDTO;
import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class UtilsService {

    private final SuperficieRepository superficieRepository;
    private final MedioPagoRepository medioPagoRepository;
    TipoClienteRepository tipoClienteRepository;
    TipoPagoRepository tipoPagoRepository;
    VariosRepository variosRepository;

    public UtilsService(TipoClienteRepository tipoClienteRepository,
                        TipoPagoRepository tipoPagoRepository,
                        SuperficieRepository superficieRepository,
                        MedioPagoRepository medioPagoRepository,
                        VariosRepository variosRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
        this.tipoPagoRepository = tipoPagoRepository;
        this.superficieRepository = superficieRepository;
        this.medioPagoRepository = medioPagoRepository;
        this.variosRepository = variosRepository;
    }

    public List<TipoClienteDTO> getTipoCliente() {
        return tipoClienteRepository.findAll().stream()
                .map(TipoClienteDTO::toDTO)
                .toList();
    }

    public TipoCliente getTipoClienteById(Integer id) {
        return tipoClienteRepository.findById(id).orElse(null);
    }

    public List<TipoPagoDTO> getTipoPago() {
        return tipoPagoRepository.findAll().stream()
                .map(TipoPagoDTO::toDTO)
                .toList();
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

    public List<SuperficieDTO> getSuperficies() {
        return superficieRepository.findAll().stream()
                .map(SuperficieDTO::toDTO)
                .toList();
    }

    public List<MedioPagoDTO> getMediosPago() {
        return medioPagoRepository.findAll().stream()
                .map(MedioPagoDTO::toDTO)
                .toList();
    }

    public BigDecimal getPrecioMinutoEmpresa() {
        return variosRepository.findAll().getFirst().getPrecioMinutoEmpresa();
    }
}
