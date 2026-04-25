package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.ClienteDTO;
import ar.com.lbr.precisionappbe.dto.PrecioMaterialDTO;
import ar.com.lbr.precisionappbe.dto.TipoClienteDTO;
import ar.com.lbr.precisionappbe.dto.TrabajoPresupuestadoDTO;
import ar.com.lbr.precisionappbe.dto.VariosDTO;
import ar.com.lbr.precisionappbe.services.strategies.DescuentoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresupuestoCalculadorServiceTest {

    @Mock
    private MaterialesService materialesService;
    @Mock
    private VariosService variosService;
    @Mock
    private TipoClienteService tipoClienteService;

    @Mock
    private List<DescuentoStrategy> estrategias;

    @InjectMocks
    private PresupuestoCalculadorService calculadorService;

    private VariosDTO configGlobal;
    private TipoClienteDTO tipoEstudiante;
    private TipoClienteDTO tipoEmpresa;
    private TipoClienteDTO tipoNormal;

    @BeforeEach
    void setUp() {
        // Configuración global de la tabla "Varios"
        configGlobal = new VariosDTO();
        configGlobal.setPrecioMinuto(new BigDecimal("300.00"));
        configGlobal.setAjuste(10); // 10%
        configGlobal.setDescuentoPorPunto(300);
        configGlobal.setPrecioMinutoEmpresa(new BigDecimal("1500.00"));
        configGlobal.setMinutosPorPunto(5);

        // Mocks de Tipos de Cliente
        tipoEstudiante = new TipoClienteDTO(1, "ESTUDIANTE");
        tipoEmpresa = new TipoClienteDTO(2, "EMPRESA");
        tipoNormal = new TipoClienteDTO(3, "NORMAL");
    }

    @Test
    void calcularYValidarTrabajo_CuandoNoHayEstrategia_DescuentoDebeSerCero() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(3);

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setTiempoDeCorte(10);
        trabajo.setTraeMaterial(true);
        trabajo.setVinilo(BigDecimal.ZERO);
        trabajo.setVectorizado(BigDecimal.ZERO);
        trabajo.setPosicionador(BigDecimal.ZERO);
        trabajo.setExtra(BigDecimal.ZERO);
        trabajo.setGrabado(false);
        trabajo.setCortesEspeciales(false);
        trabajo.setCarteles(false);

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(3)).thenReturn(tipoNormal);

        // Simulamos que ninguna estrategia aplica
        when(estrategias.stream()).thenReturn(Stream.empty());

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        assertEquals(BigDecimal.ZERO, resultado.getDescuento());
        assertEquals(new BigDecimal("3300.00"), resultado.getPrecioTrabajo());
    }

    @Test
    void calcularYValidarTrabajo_DebeUsarEstrategiaCorrecta() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(1);

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setTiempoDeCorte(10);
        trabajo.setTraeMaterial(true);
        trabajo.setVinilo(BigDecimal.ZERO);
        trabajo.setVectorizado(BigDecimal.ZERO);
        trabajo.setPosicionador(BigDecimal.ZERO);
        trabajo.setExtra(BigDecimal.ZERO);
        trabajo.setGrabado(false);
        trabajo.setCortesEspeciales(false);
        trabajo.setCarteles(false);

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(1)).thenReturn(tipoEstudiante);

        // MOCKS DE ESTRATEGIA:
        // Simulamos una estrategia que sí aplica y devuelve un descuento de 100
        DescuentoStrategy mockStrategy = mock(DescuentoStrategy.class);
        when(mockStrategy.aplicaA("ESTUDIANTE")).thenReturn(true);
        when(mockStrategy.calcularDescuento(anyInt(), any(), any())).thenReturn(new BigDecimal("100.00"));

        // Configuramos el stream de la lista de estrategias
        when(estrategias.stream()).thenReturn(Stream.of(mockStrategy));

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        // Precio Minuto: 330 | Corte: 3300 | Descuento: 100 | Total: 3200
        assertEquals(new BigDecimal("3200.00"), resultado.getPrecioTrabajo());
        assertEquals(new BigDecimal("100.00"), resultado.getDescuento());

        // Verificamos que se interactuó con la estrategia
        verify(mockStrategy).calcularDescuento(eq(10), any(), eq(configGlobal));
    }

    @Test
    void calcularTrabajo_ClienteEmpresa_DebeUsarPrecioMinutoEmpresa() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(2);
        clienteNormal.setPrecioMinutoEmpresa(new BigDecimal("2000.00"));

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setTiempoDeCorte(10);
        trabajo.setTraeMaterial(true);
        trabajo.setVinilo(BigDecimal.ZERO);
        trabajo.setVectorizado(BigDecimal.ZERO);
        trabajo.setPosicionador(BigDecimal.ZERO);
        trabajo.setExtra(BigDecimal.ZERO);
        trabajo.setGrabado(false);
        trabajo.setCortesEspeciales(false);
        trabajo.setCarteles(false);

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(2)).thenReturn(tipoEmpresa);

        // Simulamos que ninguna estrategia aplica
        when(estrategias.stream()).thenReturn(Stream.empty());

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        assertEquals(BigDecimal.ZERO, resultado.getDescuento());
        assertEquals(new BigDecimal("22000.00"), resultado.getPrecioTrabajo());
    }

    @Test
    void calcularTrabajo_Manual_DebeRespetarPrecioFijoFE() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(3);

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setGrabado(true); // Es trabajo manual
        trabajo.setPrecioCorte(new BigDecimal("5000.00")); // Precio puesto a mano en React
        trabajo.setTiempoDeCorte(10);
        trabajo.setTraeMaterial(true);
        trabajo.setVinilo(BigDecimal.ZERO);
        trabajo.setVectorizado(BigDecimal.ZERO);
        trabajo.setPosicionador(BigDecimal.ZERO);
        trabajo.setExtra(BigDecimal.ZERO);
        trabajo.setCortesEspeciales(false);
        trabajo.setCarteles(false);

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(3)).thenReturn(tipoNormal);

        // Simulamos que ninguna estrategia aplica
        when(estrategias.stream()).thenReturn(Stream.empty());

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        assertEquals(BigDecimal.ZERO, resultado.getDescuento());
        assertEquals(new BigDecimal("5000.00"), resultado.getPrecioTrabajo());
    }

    @Test
    void calcularTrabajo_ConMaterial_DebeSumarPrecioDesdeServicio() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(3);

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setTraeMaterial(false);
        trabajo.setIdMateriales(128);
        trabajo.setIdSuperficie(2); // Supongamos que es "1/2"
        trabajo.setUnidades(1);
        trabajo.setTiempoDeCorte(10);
        // Inicializamos el resto en cero para facilitar el assert
        trabajo.setVinilo(BigDecimal.ZERO);
        trabajo.setVectorizado(BigDecimal.ZERO);
        trabajo.setPosicionador(BigDecimal.ZERO);
        trabajo.setExtra(BigDecimal.ZERO);
        trabajo.setGrabado(false);
        trabajo.setCortesEspeciales(false);
        trabajo.setCarteles(false);

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(3)).thenReturn(tipoNormal);

        // Mock de MaterialesService (Simulamos que el material cuesta 1500.50)
        PrecioMaterialDTO mockPrecioMaterial = new PrecioMaterialDTO();
        mockPrecioMaterial.setPrecio(new BigDecimal("1500.50"));

        when(materialesService.calcularPrecio(128, 2, 1)).thenReturn(mockPrecioMaterial);

        // Simulamos que ninguna estrategia aplica
        when(estrategias.stream()).thenReturn(Stream.empty());

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        // Precio Corte: 330 * 10 = 3300.00
        // Precio Material: 1500.50
        // Total: 3300.00 + 1500.50 = 4800.50
        assertEquals(new BigDecimal("1500.50"), resultado.getPrecioMaterial());
        assertEquals(new BigDecimal("4800.50"), resultado.getPrecioTrabajo());

        // Verificamos que se llamó al servicio de materiales con los datos exactos
        verify(materialesService).calcularPrecio(128, 2, 1);
    }

    @Test
    void calcularTrabajo_ConCamposExtra_DebeSumarTodoAlTotal() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(3);

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setTraeMaterial(true);
        trabajo.setTiempoDeCorte(0);
        trabajo.setGrabado(true);
        trabajo.setPrecioCorte(new BigDecimal("1000.00"));

        // Agregamos adicionales
        trabajo.setVinilo(new BigDecimal("500.00"));
        trabajo.setVectorizado(new BigDecimal("200.00"));
        trabajo.setPosicionador(new BigDecimal("100.00"));
        trabajo.setExtra(new BigDecimal("50.00"));

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(3)).thenReturn(tipoNormal);

        // Simulamos que ninguna estrategia aplica
        when(estrategias.stream()).thenReturn(Stream.empty());

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        // Total = 1000 (corte) + 500 + 200 + 100 + 50 = 1850
        assertEquals(new BigDecimal("1850.00"), resultado.getPrecioTrabajo());
    }

    @Test
    void calcularTrabajo_TraeMaterialTrue_DebeForzarPrecioCero() {
        // GIVEN
        ClienteDTO clienteNormal = new ClienteDTO();
        clienteNormal.setIdTipoCliente(3);

        // GIVEN
        TrabajoPresupuestadoDTO trabajo = new TrabajoPresupuestadoDTO();
        trabajo.setTraeMaterial(true); // El cliente trae el material
        trabajo.setTiempoDeCorte(5);
        trabajo.setPrecioCorte(new BigDecimal("1000.00"));
        trabajo.setGrabado(true);
        trabajo.setCortesEspeciales(false);
        trabajo.setCarteles(false);

        when(variosService.getVarios()).thenReturn(configGlobal);
        when(tipoClienteService.getTipoClienteById(3)).thenReturn(tipoNormal);

        // Simulamos que ninguna estrategia aplica
        when(estrategias.stream()).thenReturn(Stream.empty());

        // WHEN
        TrabajoPresupuestadoDTO resultado = calculadorService.calcularYValidarTrabajo(trabajo, clienteNormal);

        // THEN
        assertEquals(BigDecimal.ZERO, resultado.getPrecioMaterial());
        // Verificamos que NO se llamó al servicio de materiales (ahorro de recursos)
        verifyNoInteractions(materialesService);
    }
}
