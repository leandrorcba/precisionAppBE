package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.Varios;
import ar.com.lbr.precisionappbe.repositories.VariosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FolderService {

    private static final Logger log = LoggerFactory.getLogger(FolderService.class);
    private final VariosRepository variosRepository;

    public FolderService(VariosRepository variosRepository) {
        this.variosRepository = variosRepository;
    }

    public void crearCarpetaCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            return;
        }

        Varios varios = variosRepository.findAll().stream().findFirst().orElse(null);
        if (varios == null || varios.getDirectorioRaizCarpetas() == null || varios.getDirectorioRaizCarpetas().trim().isEmpty()) {
            log.warn("El directorio raiz de carpetas no esta configurado en los parametros del sistema.");
            return;
        }

        String rootPath = varios.getDirectorioRaizCarpetas().trim();
        String sanitizedFolderName = sanitizeFolderName(nombreCliente);

        try {
            File rootDir = new File(rootPath);
            if (!rootDir.exists()) {
                boolean created = rootDir.mkdirs();
                if (created) {
                    log.info("Directorio raiz creado: {}", rootPath);
                }
            }

            File clientDir = new File(rootDir, sanitizedFolderName);
            if (!clientDir.exists()) {
                boolean created = clientDir.mkdir();
                if (created) {
                    log.info("Carpeta del cliente creada: {}", clientDir.getAbsolutePath());
                } else {
                    log.error("No se pudo crear la carpeta del cliente: {}", clientDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("Error al crear la carpeta fisica del cliente {}", nombreCliente, e);
        }
    }

    public static String sanitizeFolderName(String name) {
        if (name == null) {
            return "";
        }
        // Reemplazar caracteres invalidos en Windows/Linux para nombres de carpeta por guion bajo
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
}
