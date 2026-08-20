package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

    @Mock
    private VariosRepository variosRepository;

    private FolderService service;

    @BeforeEach
    void setUp() {
        service = new FolderService(variosRepository);
    }

    @Test
    void crearCarpetaCliente_withConfiguredRoot_createsDirectory(@TempDir Path tempDir) {
        Varios varios = new Varios();
        varios.setHabilitarCarpetas(true);
        varios.setDirectorioRaizCarpetas(tempDir.toAbsolutePath().toString());
        when(variosRepository.findAll()).thenReturn(List.of(varios));

        service.crearCarpetaCliente("Cliente Test/Con Barras");

        File expectedDir = new File(tempDir.toFile(), "Cliente Test_Con Barras");
        assertThat(expectedDir).exists();
        assertThat(expectedDir).isDirectory();
    }

    @Test
    void crearCarpetaCliente_noConfiguredRoot_doesNotCreate() {
        when(variosRepository.findAll()).thenReturn(Collections.emptyList());

        service.crearCarpetaCliente("Cliente Test");
        // No exceptions thrown, logs warning and returns
    }

    @Test
    void crearCarpetaPresupuesto_createsSubdirectory(@TempDir Path tempDir) {
        Varios varios = new Varios();
        varios.setHabilitarCarpetas(true);
        varios.setDirectorioRaizCarpetas(tempDir.toAbsolutePath().toString());
        when(variosRepository.findAll()).thenReturn(List.of(varios));

        service.crearCarpetaPresupuesto("Cliente Presupuesto", 123);

        File expectedClientDir = new File(tempDir.toFile(), "Cliente Presupuesto");
        File expectedBudgetDir = new File(expectedClientDir, "123");
        assertThat(expectedBudgetDir).exists();
        assertThat(expectedBudgetDir).isDirectory();
    }

    @Test
    void crearCarpetaTrabajo_createsNestedSubdirectories(@TempDir Path tempDir) {
        Varios varios = new Varios();
        varios.setHabilitarCarpetas(true);
        varios.setDirectorioRaizCarpetas(tempDir.toAbsolutePath().toString());
        when(variosRepository.findAll()).thenReturn(List.of(varios));

        service.crearCarpetaTrabajo("Cliente Trabajo", 123, 456);

        File expectedClientDir = new File(tempDir.toFile(), "Cliente Trabajo");
        File expectedBudgetDir = new File(expectedClientDir, "123");
        File expectedTrabajoDir = new File(expectedBudgetDir, "456");
        assertThat(expectedTrabajoDir).exists();
        assertThat(expectedTrabajoDir).isDirectory();
    }

    @Test
    void getConfiguredRootPath_returnsPath(@TempDir Path tempDir) {
        Varios varios = new Varios();
        varios.setHabilitarCarpetas(true);
        varios.setDirectorioRaizCarpetas(tempDir.toAbsolutePath().toString());
        when(variosRepository.findAll()).thenReturn(List.of(varios));

        String rootPath = service.getConfiguredRootPath();

        assertThat(rootPath).isEqualTo(tempDir.toAbsolutePath().toString());
    }

    @Test
    void getConfiguredRootPath_emptyVarios_returnsNull() {
        when(variosRepository.findAll()).thenReturn(Collections.emptyList());

        String rootPath = service.getConfiguredRootPath();

        assertThat(rootPath).isNull();
    }

    @Test
    void guardarPdfEnCarpeta_writesPdfFile(@TempDir Path tempDir) throws IOException {
        Varios varios = new Varios();
        varios.setHabilitarCarpetas(true);
        varios.setDirectorioRaizCarpetas(tempDir.toAbsolutePath().toString());
        when(variosRepository.findAll()).thenReturn(List.of(varios));

        byte[] pdfBytes = "Dummy PDF Content".getBytes();
        service.guardarPdfEnCarpeta("Cliente PDF", 999, pdfBytes);

        File expectedClientDir = new File(tempDir.toFile(), "Cliente PDF");
        File expectedBudgetDir = new File(expectedClientDir, "999");
        File expectedPdfFile = new File(expectedBudgetDir, "remito-999.pdf");

        assertThat(expectedPdfFile).exists();
        assertThat(Files.readAllBytes(expectedPdfFile.toPath())).isEqualTo(pdfBytes);
    }

    @Test
    void abrirCarpeta_outsideRootPath_deniesAccess(@TempDir Path tempDir) {
        Varios varios = new Varios();
        varios.setHabilitarCarpetas(true);
        varios.setDirectorioRaizCarpetas(tempDir.toAbsolutePath().toString());
        when(variosRepository.findAll()).thenReturn(List.of(varios));

        // Intento de directory traversal pasándole un nombre de cliente que apunta afuera
        service.abrirCarpeta("..", null, null);
        // Debe denegar acceso y loguear error sin abrir explorer
    }

    @Test
    void sanitizeFolderName_returnsSanitizedString() {
        String input = "A\\B/C:D*E?F\"G<H>I|J";
        String expected = "A_B_C_D_E_F_G_H_I_J";
        assertThat(FolderService.sanitizeFolderName(input)).isEqualTo(expected);
    }

    @Test
    void sanitizeFolderName_nullString_returnsEmpty() {
        assertThat(FolderService.sanitizeFolderName(null)).isEmpty();
    }
}
