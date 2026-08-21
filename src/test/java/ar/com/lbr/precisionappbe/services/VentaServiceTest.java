package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.dto.VentaDTO;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.PagoVenta;
import ar.com.lbr.precisionappbe.model.Venta;
import ar.com.lbr.precisionappbe.repositories.MaterialeRepository;
import ar.com.lbr.precisionappbe.repositories.PagoVentaRepository;
import ar.com.lbr.precisionappbe.repositories.VentaRepository;
import ar.com.lbr.precisionappbe.services.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private MaterialeRepository materialeRepository;

    @Mock
    private PagoVentaRepository pagoVentaRepository;

    @Mock
    private AuditLogService auditLogService;

    private VentaService service;

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    @BeforeEach
    void setUp() {
        service = new VentaService(ventaRepository, materialeRepository, pagoVentaRepository, auditLogService);
    }

    @Test
    void getAllVentas_hoyTrue_callsBetweenAndMapsPagos() {
        // Arrange
        Venta v1 = new Venta();
        v1.setId(10);
        v1.setPrecioMaterial(new BigDecimal("100.00"));
        v1.setCantidad(2);
        v1.setPrecioVenta(new BigDecimal("200.00"));
        v1.setFechaHoraVenta(Instant.now());

        when(ventaRepository.findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(Collections.singletonList(v1));

        PagoVenta pago = new PagoVenta();
        pago.setIdVenta(v1);
        pago.setMonto(new BigDecimal("50.00"));
        when(pagoVentaRepository.findByIdVenta_IdInAndEnabledTrue(Collections.singletonList(10)))
                .thenReturn(Collections.singletonList(pago));

        // Act
        List<VentaDTO> result = service.getAllVentas(null, null, true);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10);
        assertThat(result.get(0).getMontoAbonado()).isEqualByComparingTo("50.00");
        verify(ventaRepository).findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class));
        verify(ventaRepository, never()).findAll();
    }

    @Test
    void getAllVentas_fechaFromAndTo_callsBetweenAndMapsPagos() {
        // Arrange
        Venta v1 = new Venta();
        v1.setId(20);
        v1.setPrecioMaterial(new BigDecimal("150.00"));
        v1.setFechaHoraVenta(Instant.now());

        LocalDate from = LocalDate.of(2026, 6, 1);
        LocalDate to = LocalDate.of(2026, 6, 10);

        when(ventaRepository.findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(Collections.singletonList(v1));
        when(pagoVentaRepository.findByIdVenta_IdInAndEnabledTrue(Collections.singletonList(20)))
                .thenReturn(Collections.emptyList());

        // Act
        List<VentaDTO> result = service.getAllVentas(from, to, false);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(20);
        assertThat(result.get(0).getMontoAbonado()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(ventaRepository).findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class));
    }

    @Test
    void getAllVentas_fechaFromOnly_callsBetweenAndMapsPagos() {
        // Arrange
        Venta v1 = new Venta();
        v1.setId(30);
        v1.setPrecioMaterial(new BigDecimal("180.00"));
        v1.setFechaHoraVenta(Instant.now());

        LocalDate from = LocalDate.of(2026, 6, 5);

        when(ventaRepository.findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(Collections.singletonList(v1));
        when(pagoVentaRepository.findByIdVenta_IdInAndEnabledTrue(Collections.singletonList(30)))
                .thenReturn(Collections.emptyList());

        // Act
        List<VentaDTO> result = service.getAllVentas(from, null, false);

        // Assert
        assertThat(result).hasSize(1);
        verify(ventaRepository).findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class));
    }

    @Test
    void getAllVentas_noFilters_usesDefaultDateRange() {
        Venta v1 = new Venta();
        v1.setId(40);
        v1.setFechaHoraVenta(Instant.now());

        when(ventaRepository.findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(Collections.singletonList(v1));
        when(pagoVentaRepository.findByIdVenta_IdInAndEnabledTrue(Collections.singletonList(40)))
                .thenReturn(Collections.emptyList());

        List<VentaDTO> result = service.getAllVentas(null, null, false);

        assertThat(result).hasSize(1);
        verify(ventaRepository).findByFechaHoraVentaBetween(any(Instant.class), any(Instant.class));
        verify(ventaRepository, never()).findAll();
    }

    @Test
    void getVentaById_ventaExists_returnsDtoWithMontoAbonado() {
        // Arrange
        Venta v = new Venta();
        v.setId(50);
        v.setFechaHoraVenta(Instant.now());
        when(ventaRepository.findById(50)).thenReturn(Optional.of(v));

        PagoVenta p1 = new PagoVenta();
        p1.setMonto(new BigDecimal("100.00"));
        PagoVenta p2 = new PagoVenta();
        p2.setMonto(new BigDecimal("150.00"));
        when(pagoVentaRepository.findByIdVenta_IdAndEnabledTrue(50)).thenReturn(Arrays.asList(p1, p2));

        // Act
        VentaDTO result = service.getVentaById(50);

        // Assert
        assertThat(result.getId()).isEqualTo(50);
        assertThat(result.getMontoAbonado()).isEqualByComparingTo("250.00");
    }

    @Test
    void getVentaById_ventaDoesNotExist_throwsRuntimeException() {
        // Arrange
        when(ventaRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getVentaById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venta no encontrada");
    }

    @Test
    void createVenta_validDtoWithMaterial_savesAndReturnsDtoAndLogsAudit() {
        // Arrange
        Material m = new Material();
        m.setId(5);
        m.setMateriales("MDF 3mm");

        VentaDTO inputDto = new VentaDTO();
        inputDto.setPrecioMaterial(new BigDecimal("10.00"));
        inputDto.setCantidad(5);
        inputDto.setPrecioVenta(new BigDecimal("50.00"));
        inputDto.setSuperficie("1");
        inputDto.setFechaVenta(LocalDate.of(2026, 6, 14));
        inputDto.setHoraVenta(LocalTime.of(10, 0));
        inputDto.setIdMateriales(5);

        when(materialeRepository.findById(5)).thenReturn(Optional.of(m));

        Venta mockSaved = new Venta();
        mockSaved.setId(60);
        mockSaved.setPrecioMaterial(new BigDecimal("10.00"));
        mockSaved.setCantidad(5);
        mockSaved.setPrecioVenta(new BigDecimal("50.00"));
        mockSaved.setSuperficie("1");
        mockSaved.setFechaHoraVenta(LocalDate.of(2026, 6, 14).atTime(LocalTime.of(10,0)).atZone(ZONE).toInstant());
        mockSaved.setIdMateriales(m);

        when(ventaRepository.save(any(Venta.class))).thenReturn(mockSaved);

        // Act
        VentaDTO result = service.createVenta(inputDto);

        // Assert
        assertThat(result.getId()).isEqualTo(60);
        assertThat(result.getMontoAbonado()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(ventaRepository).save(any(Venta.class));
        verify(auditLogService).log(eq("CREAR"), eq("VENTAS"), eq("60"), anyString());
    }

    @Test
    void createVenta_dtoWithNullFecha_usesInstantNow() {
        // Arrange
        VentaDTO inputDto = new VentaDTO();
        inputDto.setPrecioMaterial(new BigDecimal("10.00"));
        inputDto.setFechaVenta(null);

        Venta mockSaved = new Venta();
        mockSaved.setId(70);
        mockSaved.setFechaHoraVenta(Instant.now());

        when(ventaRepository.save(any(Venta.class))).thenReturn(mockSaved);

        // Act
        VentaDTO result = service.createVenta(inputDto);

        // Assert
        assertThat(result.getId()).isEqualTo(70);
        verify(ventaRepository).save(any(Venta.class));
        verify(auditLogService).log(eq("CREAR"), eq("VENTAS"), eq("70"), anyString());
    }

    @Test
    void createVenta_materialNotFound_throwsRuntimeException() {
        // Arrange
        VentaDTO inputDto = new VentaDTO();
        inputDto.setIdMateriales(99);

        when(materialeRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createVenta(inputDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Material no encontrado");
    }

    @Test
    void updateVenta_validDtoNoPayments_savesAndReturnsDtoAndLogsAudit() {
        // Arrange
        Venta existing = new Venta();
        existing.setId(80);
        existing.setPrecioVenta(new BigDecimal("100.00"));
        when(ventaRepository.findById(80)).thenReturn(Optional.of(existing));
        when(pagoVentaRepository.findByIdVenta_IdAndEnabledTrue(80)).thenReturn(Collections.emptyList());

        VentaDTO inputDto = new VentaDTO();
        inputDto.setPrecioVenta(new BigDecimal("120.00"));

        Venta mockSaved = new Venta();
        mockSaved.setId(80);
        mockSaved.setPrecioVenta(new BigDecimal("120.00"));
        when(ventaRepository.save(any(Venta.class))).thenReturn(mockSaved);

        // Act
        VentaDTO result = service.updateVenta(80, inputDto);

        // Assert
        assertThat(result.getId()).isEqualTo(80);
        assertThat(result.getMontoAbonado()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(ventaRepository).save(existing);
        verify(auditLogService).log(eq("MODIFICAR"), eq("VENTAS"), eq("80"), anyString());
    }

    @Test
    void updateVenta_ventaNotFound_throwsRuntimeException() {
        // Arrange
        when(ventaRepository.findById(80)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.updateVenta(80, new VentaDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venta no encontrada");
    }

    @Test
    void updateVenta_withPayments_throwsIllegalArgumentException() {
        // Arrange
        Venta existing = new Venta();
        existing.setId(80);
        when(ventaRepository.findById(80)).thenReturn(Optional.of(existing));

        PagoVenta pago = new PagoVenta();
        pago.setMonto(new BigDecimal("10.00"));
        when(pagoVentaRepository.findByIdVenta_IdAndEnabledTrue(80)).thenReturn(Collections.singletonList(pago));

        // Act & Assert
        assertThatThrownBy(() -> service.updateVenta(80, new VentaDTO()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede editar una venta con pagos registrados");
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void deleteVenta_ventaExistsWithPayments_deletesPaymentsThenVentaAndLogsAudit() {
        // Arrange
        Venta existing = new Venta();
        existing.setId(90);
        existing.setPrecioVenta(new BigDecimal("300.00"));
        when(ventaRepository.findById(90)).thenReturn(Optional.of(existing));

        PagoVenta p1 = new PagoVenta();
        p1.setId(101);
        PagoVenta p2 = new PagoVenta();
        p2.setId(102);
        List<PagoVenta> pagos = Arrays.asList(p1, p2);
        when(pagoVentaRepository.findByIdVenta_Id(90)).thenReturn(pagos);

        // Act
        service.deleteVenta(90);

        // Assert
        verify(pagoVentaRepository).deleteAll(pagos);
        verify(auditLogService).log(eq("ELIMINAR"), eq("PAGOS"), eq("101"), anyString());
        verify(auditLogService).log(eq("ELIMINAR"), eq("PAGOS"), eq("102"), anyString());
        verify(auditLogService).log(eq("ELIMINAR"), eq("VENTAS"), eq("90"), anyString());
        verify(ventaRepository).delete(existing);
    }

    @Test
    void deleteVenta_ventaExistsNoPayments_deletesOnlyVentaAndLogsAudit() {
        // Arrange
        Venta existing = new Venta();
        existing.setId(90);
        existing.setPrecioVenta(new BigDecimal("300.00"));
        when(ventaRepository.findById(90)).thenReturn(Optional.of(existing));
        when(pagoVentaRepository.findByIdVenta_Id(90)).thenReturn(Collections.emptyList());

        // Act
        service.deleteVenta(90);

        // Assert
        verify(pagoVentaRepository, never()).deleteAll(anyList());
        verify(auditLogService, never()).log(eq("ELIMINAR"), eq("PAGOS"), anyString(), anyString());
        verify(auditLogService).log(eq("ELIMINAR"), eq("VENTAS"), eq("90"), anyString());
        verify(ventaRepository).delete(existing);
    }

    @Test
    void deleteVenta_ventaDoesNotExist_throwsRuntimeException() {
        // Arrange
        when(ventaRepository.findById(90)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.deleteVenta(90))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venta no encontrada");
        verify(ventaRepository, never()).delete(any(Venta.class));
    }
}
