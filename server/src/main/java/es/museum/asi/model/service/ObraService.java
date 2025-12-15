package es.museum.asi.model.service;

import es.museum.asi.model.domain.Obra;
import es.museum.asi.model.enums.EstadoObra;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.ObraDTO;
import es.museum.asi.repository.ObraDao;
import es.museum.asi.repository.PiezaExpuestaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Servicio completo para gestión de obras (HU43-HU47)
 * Integración con The MET:  las obras se crean principalmente desde HU49 (importar metadatos)
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ObraService {

  private final Logger logger = LoggerFactory.getLogger(ObraService.class);

  @Autowired
  private ObraDao obraDao;

  @Autowired
  private ImagenService imagenService;

  @Autowired
  private PiezaExpuestaDao  piezaExpuestaDao;


  /**
   * HU44 - Listar obras del catálogo
   * Con filtros opcionales por autor, año, estado, técnica
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<ObraDTO> findAll(String autor, Integer ano, EstadoObra estado, String tecnica) {

    return obraDao.findAll().stream()
      .filter(o -> autor == null ||
        (o.getAutor() != null && o.getAutor().toLowerCase().contains(autor.toLowerCase())))
      .filter(o -> ano == null ||
        (o.getAnoCreacion() != null && o.getAnoCreacion().equals(ano)))
      .filter(o -> estado == null || o.getEstado() == estado)
      .filter(o -> tecnica == null ||
        (o.getTecnica() != null && o.getTecnica().toLowerCase().contains(tecnica.toLowerCase())))
      .sorted((o1, o2) -> o1.getTitulo().compareTo(o2.getTitulo())) // Orden alfabético
      .map(ObraDTO::new)
      .collect(Collectors.toList());
  }


  /**
   * Buscar obras por término general (útil para selectores en formularios)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<ObraDTO> search(String query) {
    if (query == null || query.trim().isEmpty()) {
      return findAll(null, null, null, null);
    }

    String queryLower = query.toLowerCase();
    return obraDao.findAll().stream()
      .filter(o ->
        (o.getTitulo() != null && o.getTitulo().toLowerCase().contains(queryLower)) ||
          (o.getAutor() != null && o.getAutor().toLowerCase().contains(queryLower)) ||
          (o.getTecnica() != null && o.getTecnica().toLowerCase().contains(queryLower))
      )
      .sorted((o1, o2) -> o1.getTitulo().compareTo(o2.getTitulo()))
      .map(ObraDTO::new)
      .collect(Collectors.toList());
  }


  /**
   * HU43 - Crear obra:
   * Flujo principal: usuario importa obra desde MET (HU49) -> precarga formulario -> guarda aquí
   * Flujo alternativo: Usuario crea obra manualmente
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ObraDTO create(String titulo, String autor, Integer anoCreacion, String tecnica,
                        String dimensiones, String imagenUrlMET, MultipartFile imagenFile,
                        Long idExterno, EstadoObra estado) throws OperationNotAllowed {

    //titulo obligatorio
    if(titulo == null || titulo.trim().isEmpty()) {
      throw new OperationNotAllowed("El titulo es obligatorio");
    }

    // idExterno único (si viene de The MET)
    if (idExterno != null) {
      Obra existente =  obraDao.findById(idExterno);
      if (existente != null) {
        throw new OperationNotAllowed("Ya existe una obra con ese idExterno");
      }
    }

    //Procesamos imagen
    String rutaImagen = null;

    if (imagenFile != null && !imagenFile.isEmpty()) {
      //Opcion A: el usuario subió imagen propia
      rutaImagen = imagenService.guardarImagen(imagenFile, "obras");
      logger.info("Imagen local guardada:  {}", rutaImagen);
    } else if (imagenUrlMET != null && !imagenUrlMET.trim().isEmpty()) {
      //Opcion B: Guardar URL de The MET
      rutaImagen = imagenUrlMET;
      logger.info("Usando URL de The MET:  {}", rutaImagen);
    }

    //Creamos obra
    // Crear obra
    Obra obra = new Obra();
    obra.setTitulo(titulo);
    obra.setAutor(autor);
    obra.setAnoCreacion(anoCreacion);
    obra.setTecnica(tecnica);
    obra.setDimensiones(dimensiones);
    obra.setImagen(rutaImagen); // URL de MET o ruta local
    obra.setIdExterno(idExterno);
    obra.setEstado(estado != null ? estado : EstadoObra.EN_ALMACEN);
    obraDao.create(obra);

    logger.info("Obra '{}' creada (ID: {}, idExterno: {}, origen: {})",
      titulo, obra.getIdObra(), idExterno, idExterno != null ? "MET" : "Manual");

    return new ObraDTO(obra);
  }


  /**
   * HU45 - Ver detalle completo de obra del catálogo
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public ObraDTO findById(Long idObra) throws NotFoundException {
    Obra obra = obraDao.findById(idObra);
    if (obra == null) {
      throw new NotFoundException(idObra.toString(), Obra.class);
    }

    return new ObraDTO(obra);
  }


  /**
   * Editar obra del catálogo
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public ObraDTO update(Long idObra, String titulo, String autor, Integer anoCreacion, String tecnica, String dimensiones, MultipartFile nuevaImagen, EstadoObra nuevoEstado)
    throws NotFoundException, OperationNotAllowed  {
    Obra obra = obraDao.findById(idObra);
    if (obra == null) {
      throw new NotFoundException(idObra.toString(), Obra.class);
    }

    //Comprobamos si necesitamos actualizar metadatos

    if (titulo != null && !titulo.trim().isEmpty()) {
      obra.setTitulo(titulo);
    }
    if (autor != null) {
      obra.setAutor(autor);
    }
    if (anoCreacion != null) {
      obra.setAnoCreacion(anoCreacion);
    }
    if (tecnica != null) {
      obra.setTecnica(tecnica);
    }
    if (dimensiones != null) {
      obra.setDimensiones(dimensiones);
    }
    if (nuevoEstado != null) {
      obra.setEstado(nuevoEstado);
    }

    //Cambio de imagen
    if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
      //Eliminamos la anterior SOLO si es local
      if (obra.getImagen() != null && !obra.getImagen().startsWith("http")) {
        imagenService.eliminarImagen(obra.getImagen());
      }
      String nuevaRutaImagen = imagenService.guardarImagen(nuevaImagen, "obras");
    }

    obraDao.update(obra);
    logger.info("Obra {} actualizada", idObra);

    return new ObraDTO(obra);
  }


  /**
   * HU47 - Eliminar obra
   * Solo si NO está en ninguna exposición
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void delete(Long idObra) throws NotFoundException, OperationNotAllowed {
    Obra obra = obraDao.findById(idObra);
    if (obra == null) {
      throw new NotFoundException(idObra.toString(), Obra.class);
    }

    long numPiezas = piezaExpuestaDao.findAll().stream()
      .filter(p -> p.getObra().getIdObra().equals(idObra))
      .count();
    if (numPiezas > 0) {
      throw new OperationNotAllowed("No se puede eliminar la obra está siendo expuesta");
    }

    //Eliminar imagen SOLO si es local
    if (obra.getImagen() != null && !obra.getImagen().startsWith("http")) {
      imagenService.eliminarImagen(obra.getImagen());
    }
    obraDao.delete(obra);
    logger.info("Obra eliminada");
  }

}
