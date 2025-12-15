package es.museum.asi.model.service;

import es.museum.asi.model.exception.OperationNotAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Servicio para gestión de imágenes (subida y almacenamiento local)
 * Basado en el ejemplo de clase
 */
@Service
public class ImagenService {

  private final Logger logger = LoggerFactory.getLogger(ImagenService.class);

  @Value("${app. upload.dir:${user.home}/asi-museo-imagenes}")
  private String uploadDir;

  // Extensiones permitidas
  private static final String[] ALLOWED_EXTENSIONS = {". jpg", ".jpeg", ".png", ".gif", ".webp"};


  /**
   * Guardar imagen subida y devolver la ruta relativa
   *
   * @param file Archivo MultipartFile
   * @return Ruta relativa para almacenar en BD (ej: "obras/abc123.jpg")
   */
  public String guardarImagen(MultipartFile file, String carpeta) throws OperationNotAllowed {
    if (file == null || file.isEmpty()) {
      throw new OperationNotAllowed("El archivo está vacío");
    }

    // Validar extensión
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || ! isValidExtension(originalFilename)) {
      throw new OperationNotAllowed("Extensión de archivo no permitida.  Usa:  jpg, png, gif, webp");
    }

    // Validar tamaño (máximo 5MB)
    long maxSize = 5 * 1024 * 1024; // 5MB
    if (file.getSize() > maxSize) {
      throw new OperationNotAllowed("El archivo no puede superar los 5MB");
    }

    try {
      // Crear directorio si no existe
      Path uploadPath = Paths.get(uploadDir, carpeta);
      Files.createDirectories(uploadPath);

      // Generar nombre único
      String extension = originalFilename.substring(originalFilename.lastIndexOf(". "));
      String filename = UUID.randomUUID().toString() + extension;

      // Guardar archivo
      Path filePath = uploadPath.resolve(filename);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      // Devolver ruta relativa
      String relativePath = carpeta + "/" + filename;
      logger.info("Imagen guardada:  {}", relativePath);

      return relativePath;

    } catch (IOException e) {
      logger.error("Error al guardar imagen: {}", e.getMessage());
      throw new OperationNotAllowed("Error al guardar la imagen:  " + e.getMessage());
    }
  }

  /**
   * Eliminar imagen del sistema de archivos
   *
   * @param rutaRelativa Ruta relativa (ej: "obras/abc123.jpg")
   */
  public void eliminarImagen(String rutaRelativa) {
    if (rutaRelativa == null || rutaRelativa.isEmpty()) {
      return;
    }

    try {
      Path filePath = Paths.get(uploadDir, rutaRelativa);
      Files.deleteIfExists(filePath);
      logger.info("Imagen eliminada: {}", rutaRelativa);
    } catch (IOException e) {
      logger.error("Error al eliminar imagen {}: {}", rutaRelativa, e.getMessage());
    }
  }

  /**
   * Obtener Path completo de una imagen
   */
  public Path getImagePath(String rutaRelativa) {
    return Paths.get(uploadDir, rutaRelativa);
  }

  /**
   * Validar extensión permitida
   */
  private boolean isValidExtension(String filename) {
    String lowerFilename = filename.toLowerCase();
    for (String ext : ALLOWED_EXTENSIONS) {
      if (lowerFilename.endsWith(ext)) {
        return true;
      }
    }
    return false;
  }


}
