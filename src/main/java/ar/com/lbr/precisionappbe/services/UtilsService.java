package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CuentaBancariaDTO;
import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.MercadoPagoDTO;
import ar.com.lbr.precisionappbe.dto.SuperficieDTO;
import ar.com.lbr.precisionappbe.dto.TarjetaDTO;
import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.repositories.CuentaBancariaRepository;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.MercadoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TarjetaRepository;
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
    private final TipoClienteRepository tipoClienteRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final VariosRepository variosRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final MercadoPagoRepository mercadoPagoRepository;

    public UtilsService(TipoClienteRepository tipoClienteRepository,
                         TipoPagoRepository tipoPagoRepository,
                         SuperficieRepository superficieRepository,
                         MedioPagoRepository medioPagoRepository,
                         VariosRepository variosRepository,
                         CuentaBancariaRepository cuentaBancariaRepository,
                         TarjetaRepository tarjetaRepository,
                         MercadoPagoRepository mercadoPagoRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
        this.tipoPagoRepository = tipoPagoRepository;
        this.superficieRepository = superficieRepository;
        this.medioPagoRepository = medioPagoRepository;
        this.variosRepository = variosRepository;
        this.cuentaBancariaRepository = cuentaBancariaRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.mercadoPagoRepository = mercadoPagoRepository;
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

    public List<CuentaBancariaDTO> getCuentasBancarias() {
        return cuentaBancariaRepository.findByHabilitadaTrue().stream()
                .map(CuentaBancariaDTO::toDTO)
                .toList();
    }

    public List<TarjetaDTO> getTarjetas() {
        return tarjetaRepository.findAll().stream()
                .map(TarjetaDTO::toDTO)
                .toList();
    }

    public List<MercadoPagoDTO> getMercadoPagos() {
        return mercadoPagoRepository.findByDisabledFalse().stream()
                .map(MercadoPagoDTO::toDTO)
                .toList();
    }
}
