package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.CuentaBancariaDTO;
import ar.com.lbr.precisionappbe.dto.MedioPagoDTO;
import ar.com.lbr.precisionappbe.dto.MercadoPagoDTO;
import ar.com.lbr.precisionappbe.dto.SuperficieDTO;
import ar.com.lbr.precisionappbe.dto.TarjetaDTO;
import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.dto.TipoPagoDTO;
import ar.com.lbr.precisionappbe.model.CuentaBancaria;
import ar.com.lbr.precisionappbe.model.MedioPago;
import ar.com.lbr.precisionappbe.model.MercadoPago;
import ar.com.lbr.precisionappbe.model.Superficie;
import ar.com.lbr.precisionappbe.model.Tarjeta;
import ar.com.lbr.precisionappbe.model.TipoCliente;
import ar.com.lbr.precisionappbe.model.TipoPago;
import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.repositories.CuentaBancariaRepository;
import ar.com.lbr.precisionappbe.repositories.MedioPagoRepository;
import ar.com.lbr.precisionappbe.repositories.MercadoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.SuperficieRepository;
import ar.com.lbr.precisionappbe.repositories.TarjetaRepository;
import ar.com.lbr.precisionappbe.repositories.TipoClienteRepository;
import ar.com.lbr.precisionappbe.repositories.TipoPagoRepository;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilsServiceTest {

    @Mock
    private SuperficieRepository superficieRepository;
    @Mock
    private MedioPagoRepository medioPagoRepository;
    @Mock
    private TipoClienteRepository tipoClienteRepository;
    @Mock
    private TipoPagoRepository tipoPagoRepository;
    @Mock
    private VariosRepository variosRepository;
    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;
    @Mock
    private TarjetaRepository tarjetaRepository;
    @Mock
    private MercadoPagoRepository mercadoPagoRepository;

    private UtilsService service;

    @BeforeEach
    void setUp() {
        service = new UtilsService(tipoClienteRepository, tipoPagoRepository, superficieRepository, medioPagoRepository,
                variosRepository, cuentaBancariaRepository, tarjetaRepository, mercadoPagoRepository);
    }

    @Test
    void getTipoCliente_returnsList() {
        TipoCliente tc = new TipoCliente();
        tc.setId(1);
        tc.setNombreTipo("Particular");

        when(tipoClienteRepository.findAll()).thenReturn(List.of(tc));

        List<TipoClienteDTO> result = service.getTipoCliente();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombreTipo()).isEqualTo("Particular");
    }

    @Test
    void getTipoClienteById_returnsEntity() {
        TipoCliente tc = new TipoCliente();
        tc.setId(2);

        when(tipoClienteRepository.findById(2)).thenReturn(Optional.of(tc));

        TipoCliente result = service.getTipoClienteById(2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
    }

    @Test
    void getTipoPago_returnsList() {

        TipoPago tp = new TipoPago();
        tp.setId(1);
        tp.setTipo("SEÑA");

        when(tipoPagoRepository.findAll()).thenReturn(List.of(tp));

        List<TipoPagoDTO> result = service.getTipoPago();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo("SEÑA");
    }

    @Test
    void getYears_returnsListFrom2015ToCurrentYear() {
        int currentYear = Year.now().getValue();
        List<Integer> result = service.getYears();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).isEqualTo(2015);
        assertThat(result.get(result.size() - 1)).isEqualTo(currentYear);
    }

    @Test
    void getSuperficies_returnsList() {
        Superficie sup = new Superficie();
        sup.setId(1);
        sup.setValor("1");

        when(superficieRepository.findAll()).thenReturn(List.of(sup));

        List<SuperficieDTO> result = service.getSuperficies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValor()).isEqualTo("1");
    }

    @Test
    void getMediosPago_returnsList() {
        MedioPago mp = new MedioPago();
        mp.setId(1);
        mp.setTipo("EFECTIVO");
        mp.setDescripcion("Efectivo");

        when(medioPagoRepository.findAll()).thenReturn(List.of(mp));

        List<MedioPagoDTO> result = service.getMediosPago();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo("EFECTIVO");
    }

    @Test
    void getPrecioMinutoEmpresa_returnsBigDecimal() {
        Varios varios = new Varios();
        varios.setPrecioMinutoEmpresa(BigDecimal.valueOf(12.5));

        when(variosRepository.findAll()).thenReturn(List.of(varios));

        BigDecimal result = service.getPrecioMinutoEmpresa();

        assertThat(result).isEqualTo(BigDecimal.valueOf(12.5));
    }

    @Test
    void getCuentasBancarias_returnsEnabledList() {
        CuentaBancaria cb = new CuentaBancaria();
        cb.setId(1);
        cb.setBanco("Galicia");
        cb.setHabilitada(true);

        when(cuentaBancariaRepository.findByHabilitadaTrue()).thenReturn(List.of(cb));

        List<CuentaBancariaDTO> result = service.getCuentasBancarias();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBanco()).isEqualTo("Galicia");
    }

    @Test
    void getTarjetas_returnsList() {
        Tarjeta t = new Tarjeta();
        t.setId(1);
        t.setNombre("Visa");

        when(tarjetaRepository.findAll()).thenReturn(List.of(t));

        List<TarjetaDTO> result = service.getTarjetas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Visa");
    }

    @Test
    void getMercadoPagos_returnsEnabledList() {
        MercadoPago mp = new MercadoPago();
        mp.setId(1);
        mp.setTitular("MP Personal");
        mp.setDisabled(false);

        when(mercadoPagoRepository.findByDisabledFalse()).thenReturn(List.of(mp));

        List<MercadoPagoDTO> result = service.getMercadoPagos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitular()).isEqualTo("MP Personal");
    }
}
