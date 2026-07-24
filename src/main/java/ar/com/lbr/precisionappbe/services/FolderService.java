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
        if (varios == null || varios.getHabilitarCarpetas() == null || !varios.getHabilitarCarpetas()) {
            log.info("Creacion de carpetas deshabilitada en los parametros del sistema. Saltando creacion de carpeta de cliente.");
            return;
        }
        if (varios.getDirectorioRaizCarpetas() == null || varios.getDirectorioRaizCarpetas().trim().isEmpty()) {
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

    public void crearCarpetaPresupuesto(String nombreCliente, Integer idPresupuesto) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty() || idPresupuesto == null) {
            return;
        }

        Varios varios = variosRepository.findAll().stream().findFirst().orElse(null);
        if (varios == null || varios.getHabilitarCarpetas() == null || !varios.getHabilitarCarpetas()) {
            log.info("Creacion de carpetas deshabilitada en los parametros del sistema. Saltando creacion de carpeta de presupuesto.");
            return;
        }
        if (varios.getDirectorioRaizCarpetas() == null || varios.getDirectorioRaizCarpetas().trim().isEmpty()) {
            log.warn("El directorio raiz de carpetas no esta configurado en los parametros del sistema.");
            return;
        }

        String rootPath = varios.getDirectorioRaizCarpetas().trim();
        String sanitizedClientName = sanitizeFolderName(nombreCliente);
        String budgetFolderName = String.valueOf(idPresupuesto);

        try {
            File rootDir = new File(rootPath);
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            }

            File clientDir = new File(rootDir, sanitizedClientName);
            if (!clientDir.exists()) {
                clientDir.mkdir();
            }

            File budgetDir = new File(clientDir, budgetFolderName);
            if (!budgetDir.exists()) {
                boolean created = budgetDir.mkdir();
                if (created) {
                    log.info("Carpeta del presupuesto creada: {}", budgetDir.getAbsolutePath());
                } else {
                    log.error("No se pudo crear la carpeta del presupuesto: {}", budgetDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("Error al crear la carpeta fisica del presupuesto {} para el cliente {}", idPresupuesto, nombreCliente, e);
        }
    }

    public void crearCarpetaTrabajo(String nombreCliente, Integer idPresupuesto, Integer idTrabajo) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty() || idPresupuesto == null || idTrabajo == null) {
            return;
        }

        Varios varios = variosRepository.findAll().stream().findFirst().orElse(null);
        if (varios == null || varios.getHabilitarCarpetas() == null || !varios.getHabilitarCarpetas()) {
            log.info("Creacion de carpetas deshabilitada en los parametros del sistema. Saltando creacion de carpeta de trabajo.");
            return;
        }
        if (varios.getDirectorioRaizCarpetas() == null || varios.getDirectorioRaizCarpetas().trim().isEmpty()) {
            log.warn("El directorio raiz de carpetas no esta configurado en los parametros del sistema.");
            return;
        }

        String rootPath = varios.getDirectorioRaizCarpetas().trim();
        String sanitizedClientName = sanitizeFolderName(nombreCliente);
        String budgetFolderName = String.valueOf(idPresupuesto);
        String trabajoFolderName = String.valueOf(idTrabajo);

        try {
            File rootDir = new File(rootPath);
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            }

            File clientDir = new File(rootDir, sanitizedClientName);
            if (!clientDir.exists()) {
                clientDir.mkdir();
            }

            File budgetDir = new File(clientDir, budgetFolderName);
            if (!budgetDir.exists()) {
                budgetDir.mkdir();
            }

            File trabajoDir = new File(budgetDir, trabajoFolderName);
            if (!trabajoDir.exists()) {
                boolean created = trabajoDir.mkdir();
                if (created) {
                    log.info("Carpeta del trabajo creada: {}", trabajoDir.getAbsolutePath());
                } else {
                    log.error("No se pudo crear la carpeta del trabajo: {}", trabajoDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("Error al crear la carpeta fisica del trabajo {} para el presupuesto {} del cliente {}", 
                    idTrabajo, idPresupuesto, nombreCliente, e);
        }
    }

    public void abrirCarpeta(String nombreCliente, Integer idPresupuesto, Integer idTrabajo) {
        Varios varios = variosRepository.findAll().stream().findFirst().orElse(null);
        if (varios == null || varios.getHabilitarCarpetas() == null || !varios.getHabilitarCarpetas()) {
            log.warn("La lectura/apertura de carpetas esta deshabilitada en los parametros del sistema.");
            return;
        }
        if (varios.getDirectorioRaizCarpetas() == null || varios.getDirectorioRaizCarpetas().trim().isEmpty()) {
            log.warn("El directorio raiz de carpetas no esta configurado en los parametros del sistema.");
            return;
        }

        String rootPath = varios.getDirectorioRaizCarpetas().trim();
        File targetDir = new File(rootPath);

        if (nombreCliente != null && !nombreCliente.trim().isEmpty()) {
            targetDir = new File(targetDir, sanitizeFolderName(nombreCliente));
            if (idPresupuesto != null) {
                targetDir = new File(targetDir, String.valueOf(idPresupuesto));
                if (idTrabajo != null) {
                    targetDir = new File(targetDir, String.valueOf(idTrabajo));
                }
            }
        }

        try {
            if (!targetDir.exists()) {
                targetDir.mkdirs(); // crear si no existe
            }

            if (targetDir.exists() && targetDir.isDirectory()) {
                log.info("Abriendo carpeta física: {}", targetDir.getAbsolutePath());
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("explorer.exe \"" + targetDir.getAbsolutePath() + "\"");
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec("open \"" + targetDir.getAbsolutePath() + "\"");
                } else {
                    Runtime.getRuntime().exec("xdg-open \"" + targetDir.getAbsolutePath() + "\"");
                }
            } else {
                log.error("La ruta no es un directorio válido: {}", targetDir.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error al abrir la carpeta física: {}", targetDir.getAbsolutePath(), e);
        }
    }

    public void guardarPdfEnCarpeta(String nombreCliente, Integer idPresupuesto, byte[] pdfBytes) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty() || idPresupuesto == null || pdfBytes == null) {
            return;
        }

        Varios varios = variosRepository.findAll().stream().findFirst().orElse(null);
        if (varios == null || varios.getHabilitarCarpetas() == null || !varios.getHabilitarCarpetas()) {
            log.info("Creacion de carpetas/archivos deshabilitada. Saltando guardado de PDF.");
            return;
        }
        if (varios.getDirectorioRaizCarpetas() == null || varios.getDirectorioRaizCarpetas().trim().isEmpty()) {
            log.warn("El directorio raiz de carpetas no esta configurado en los parametros del sistema. No se puede guardar el PDF.");
            return;
        }

        String rootPath = varios.getDirectorioRaizCarpetas().trim();
        String sanitizedClientName = sanitizeFolderName(nombreCliente);
        String budgetFolderName = String.valueOf(idPresupuesto);

        try {
            File rootDir = new File(rootPath);
            File clientDir = new File(rootDir, sanitizedClientName);
            File budgetDir = new File(clientDir, budgetFolderName);
            
            if (!budgetDir.exists()) {
                budgetDir.mkdirs();
            }

            File pdfFile = new File(budgetDir, "remito-" + idPresupuesto + ".pdf");
            java.nio.file.Files.write(pdfFile.toPath(), pdfBytes);
            log.info("PDF del remito guardado/actualizado en: {}", pdfFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error al guardar el PDF del remito en la carpeta para el presupuesto {}", idPresupuesto, e);
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
