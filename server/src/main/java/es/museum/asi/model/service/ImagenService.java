package es.museum.asi.model.service;

import es.museum.asi.model.exception.OperationNotAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImagenService {

  private final Logger logger = LoggerFactory.getLogger(ImagenService.class);

  @Value("${app.upload.dir:${user.home}/asi-museo-imagenes}")
  private String uploadDir;

  // Lista limpia de extensiones permitidas (SIN PUNTOS y en minúsculas)
  private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

  public String guardarImagen(MultipartFile file, String carpeta) throws OperationNotAllowed {
    if (file == null || file.isEmpty()) {
      throw new OperationNotAllowed("El archivo está vacío");
    }

    // Limpiar nombre de archivo (seguridad)
    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

    // 1. Extraer extensión correctamente (sin errores de índice)
    String extension = "";
    int i = originalFilename.lastIndexOf('.');
    if (i > 0) {
      extension = originalFilename.substring(i + 1).toLowerCase();
    }

    // 2. Validar contra la lista
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new OperationNotAllowed("Extensión no permitida (" + extension + "). Usa: jpg, png, gif, webp");
    }

    // Validar tamaño (máximo 5MB)
    long maxSize = 5 * 1024 * 1024;
    if (file.getSize() > maxSize) {
      throw new OperationNotAllowed("El archivo no puede superar los 5MB");
    }

    try {
      // Crear directorio si no existe
      Path uploadPath = Paths.get(uploadDir, carpeta);
      Files.createDirectories(uploadPath);

      // Generar nombre único
      String filename = UUID.randomUUID().toString() + "." + extension;

      // Guardar archivo
      Path filePath = uploadPath.resolve(filename);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      // Devolver ruta relativa (ej: "obras/uuid.jpg")
      String relativePath = carpeta + "/" + filename;
      logger.info("Imagen guardada: {}", relativePath);

      return relativePath;

    } catch (IOException e) {
      logger.error("Error IO al guardar imagen: {}", e.getMessage());
      throw new OperationNotAllowed("Error interno al guardar la imagen.");
    }
  }

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

  public Path getImagePath(String rutaRelativa) {
    return Paths.get(uploadDir, rutaRelativa);
  }
}
