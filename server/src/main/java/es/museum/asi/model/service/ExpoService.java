package es.museum.asi.model.service;

import es.museum.asi.model.domain.Exposicion;
import es.museum.asi.model.domain.Gestion;
import es.museum.asi.model.domain.User;
import es.museum.asi.model.enums.EstadoEdicion;
import es.museum.asi.model.enums.EstadoExpo;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.ExposicionDTO;
import es.museum.asi.model.service.dto.ExposicionDetalleDTO;
import es.museum.asi.repository.*;
import es.museum.asi.security.SecurityUtils;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Servicio para la gestión de exposiciones.
 * HU9-HU16 con sistema de permisos de dos niveles
 */

@Service
@Transactional(readOnly=true, rollbackFor = Exception.class)
public class ExpoService {

  private final Logger logger = LoggerFactory.getLogger(ExpoService.class);

  @Autowired
  private ExposicionDao exposicionDao;

  @Autowired
  private GestionDao gestionDao;

  @Autowired
  private EdicionDao edicionDao;

  @Autowired
  private UserDao userDao;

  /**
   * HU9 - Listado de exposiciones (Vista ADMIN)
   * Muestra TODAS las exposiciones, excluyendo archivadas por defecto
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public Collection<ExposicionDTO> findAllForAdmin(boolean incluirArchivadas) {
    return exposicionDao.findAll().stream()
      .filter(expo -> incluirArchivadas || expo.getEstadoExpo() != EstadoExpo.ARCHIVADA)
      .map(expo -> new ExposicionDTO(expo, true)) //true = incluir info de permisos
      .collect(Collectors.toList());
  }

  /**
   * HU10 - Listado de exposiciones (Vista GESTOR)
   * Muestran sólo las expos en las que tenga permisos de CREADOR o EDITOR
   */
  @PreAuthorize("hasAuthority('GESTOR')")
  public Collection<ExposicionDTO> findAllForGestor(boolean incluirArchivadas) {
    User currentUser = getCurrentUser();

    return gestionDao.findByUser(currentUser.getIdUser()).stream()
      .map(Gestion::getExposicion)
      .filter(expo -> incluirArchivadas || expo.getEstadoExpo() != EstadoExpo.ARCHIVADA)
      .map(expo -> {
        TipoPermiso permiso = getPermisoUsuario(expo.getIdExposicion(), currentUser.getIdUser());
        return new ExposicionDTO(expo, permiso);
      })
      .collect(Collectors.toList());
  }

  /**
   * HU11 - Listado de exposiciones (Vista VISITANTE/PÚBLICA)
   * Solo exposiciones ACTIVAS con al menos una edición publicada vigente
   * Ordenadas por proximidad de fecha de inicio
   */
  public Collection<ExposicionDTO> findAllPublic() {
    return exposicionDao.findByEstado(EstadoExpo.ACTIVA).stream()
      .filter(this::tieneEdicionPublicadaVigente)
      .map(expo -> new ExposicionDTO(expo, false)) //false = sin info de permisos
      .sorted((e1,e2) -> comparePorProximidad(e1,e2))
      .collect(Collectors.toList());
  }


  /**
   * HU59 - Buscar exposiciones en catálogo público
   */
  public Collection<ExposicionDTO> searchPublic(String termino) {
    return findAllPublic().stream()
      .filter(expo ->
        expo.getTitulo().toLowerCase().contains(termino.toLowerCase()) ||
          (expo.getDescripcion() != null && expo.getDescripcion().toLowerCase().contains(termino.toLowerCase()))
      )
      .collect(Collectors.toList());
  }


  /**
   * HU12 - CREAR EXPOSICION (ADMIN o GESTOR)
   * Si es GESTOR, se le asigna automáticamente permiso CREADOR
   */
  @PreAuthorize("hasAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public ExposicionDTO create(String titulo, String descripcion) {
    Exposicion exposicion = new Exposicion(titulo, descripcion);
    exposicionDao.create(exposicion);

    //Si es GESTOR, asignar permiso CREADOR automáticamente
    User currentUser = getCurrentUser();
    if (currentUser.getAutoridad() == UserAuthority.GESTOR) {
      Gestion gestion = new Gestion();
      gestion.setUser(currentUser);
      gestion.setExposicion(exposicion);
      gestion.setPermiso(TipoPermiso.CREADOR);
      gestionDao.create(gestion);

      logger.info("Exposición '{}' creada por gestor {} con permiso CREADOR",
        titulo, currentUser.getLogin());
    } else  {
      logger.info("Exposición '{}' creada por administrador {}",
        titulo, currentUser.getLogin());
    }
    return new ExposicionDTO(exposicion, false);
  }


  /**
   * HU13 - Detalla exposición (ADMIN/GESTOR)
   * Con información completa de gestión
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public ExposicionDetalleDTO findDetalleForAdmin(Long idExposicion)
    throws NotFoundException, InvalidPermissionException {

    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion. toString(), Exposicion.class);
    }

    User currentUser = getCurrentUser();

    //Verificar permisos si es GESTOR
    if (currentUser.getAutoridad() == UserAuthority.GESTOR) {
      Gestion gestion = gestionDao.findByUserAndExpo(currentUser.getIdUser(), idExposicion);
      if (gestion == null) {
        throw new InvalidPermissionException("Ver detalle de la exposición",
          "No tiene permisos sobre esta exposición");
      }
    }
    return new ExposicionDetalleDTO(exposicion, true);
  }


  /**
   * HU13 - Detalle exposición (Vista pública)
   * Solo información básica
   */
  public ExposicionDetalleDTO findDetallePublic(Long idExposicion)
    throws NotFoundException, OperationNotAllowed {
    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion. toString(), Exposicion.class);
    }

    //Validar que sea visible públicamente
    if (exposicion.getEstadoExpo() != EstadoExpo.ACTIVA || !tieneEdicionPublicadaVigente(exposicion)) {
      throw new OperationNotAllowed("Esta exposición no está disponible públicamente");
    }

    return new ExposicionDetalleDTO(exposicion, false);
  }


  /**
   * HU14 - Editar exposición (ADMIN o GESTOR con permisos)
   * Solo CREADOR/ADMIN pueden modificar el estado
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public ExposicionDTO update(Long idExposicion, String titulo, String descripcion, EstadoExpo nuevoEstado)
   throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion. toString(), Exposicion.class);
    }

    User currentUser = getCurrentUser();
    TipoPermiso permiso = getPermisoUsuario(idExposicion, currentUser.getIdUser());

    // EDITOR Y CREADOR pueden editar título y descripción
    if (permiso == null) {
      throw new InvalidPermissionException("editar la exposición", "No tiene permisos");
    }
    if (titulo != null) {
      exposicion.setTitulo(titulo);

    }
    if (descripcion != null) {
      exposicion.setDescripcion(descripcion);
    }

    // Solo CREADOR o ADMIN pueden cambiar el estado
    if (nuevoEstado != null && nuevoEstado != exposicion.getEstadoExpo()) {
      if (permiso != TipoPermiso.CREADOR && currentUser.getAutoridad() != UserAuthority.ADMIN) {
        throw new InvalidPermissionException("cambiar el estado de la exposición", "Solo el CREADOR o ADMIN pueden modificar el estado");
      }
      exposicion.setEstadoExpo(nuevoEstado);
    }

    exposicionDao. update(exposicion);
    logger.info("Exposición {} actualizada por {}", exposicion.getTitulo(), currentUser.getLogin());

    return new ExposicionDTO(exposicion, permiso);
  }


  /**
   * HU15 - Archivar Exposición (solo CREADOR o ADMIN)
   * No se puede archivar si tiene ediciones PUBLICADAS activas
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public ExposicionDTO archivar(Long idExposicion)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    // Verificamos permisos
    verificarPermisoCreador(idExposicion, "archivar la exposición");

    // Validar que no tenga ediciones PUBLICADAS activas
    boolean tieneEdicionesPublicadas = edicionDao.findByExposicion(idExposicion).stream()
      .anyMatch(edicion -> edicion.getEstado() == EstadoEdicion.PUBLICADA);
    if (tieneEdicionesPublicadas) {
      throw new OperationNotAllowed("No se puede archivar la exposición porque tiene ediciones PUBLICADAS activas. " +
        "Debe cancelarlas o finalizarlas primero.");
    }

    exposicion.setEstadoExpo(EstadoExpo.ARCHIVADA);
    exposicionDao.update(exposicion);

    logger.info("Exposición '{}' archivada por {}",
      exposicion.getTitulo(), getCurrentUser().getLogin());

    return new ExposicionDTO(exposicion, false);
  }

  /**
   * Desarchivar exposición
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public ExposicionDTO desarchivar(Long idExposicion)
    throws NotFoundException, InvalidPermissionException {
    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    verificarPermisoCreador(idExposicion, "desarchivar la exposición");

    exposicion.setEstadoExpo(EstadoExpo.ACTIVA);
    exposicionDao.update(exposicion);

    logger.info("Exposición '{}' desarchivada por {}",
      exposicion.getTitulo(), getCurrentUser().getLogin());

    return new ExposicionDTO(exposicion, false);
  }


  /**
   * HU16 - Eliminar exposición (Solo CREADOR o ADMIN)
   * No se puede eliminar si tiene ediciones con reservas/entradas
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void delete(Long idExposicion)
    throws NotFoundException, InvalidPermissionException,
    OperationNotAllowed {

    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    verificarPermisoCreador(idExposicion, "eliminar la exposición");

    // Validar que no tenga ediciones con reservas
    boolean tieneReservas = edicionDao.findByExposicion(idExposicion).stream()
      .flatMap(edicion -> edicion.getSesiones().stream())
      .anyMatch(sesion -> !sesion.getReservas().isEmpty());

    if (tieneReservas) {
      throw new OperationNotAllowed("No se puede eliminar la exposición porque tiene ediciones con reservas/entradas asociadas.");
    }

    //Eliminación manual
    //Eliminar gestiones
    gestionDao.findByExpo(idExposicion).forEach(gestion -> gestionDao.delete(gestion));
    //Eliminar ediciones (y dependencias)
    edicionDao.findByExposicion(idExposicion).forEach(edicion -> edicionDao.delete(edicion));

    //Eliminar exposicion
    String tituloExpo = exposicion.getTitulo();
    exposicionDao.delete(exposicion);

    logger.info("Exposición '{}' eliminada por {}",
      tituloExpo, getCurrentUser().getLogin());
  }



  // ==================== MÉTODOS AUXILIARES ====================

  /**
   * Obtener el permiso del usuario actual sobre una expo
   * ADMIN tiene permisos de CREADOR implícitos
   */

  /**
   * Obtener usuario actual autenticado
   */
  private User getCurrentUser() {
    String login = SecurityUtils.getCurrentUserLogin();
    return userDao.findByLogin(login);
  }

  private TipoPermiso getPermisoUsuario(Long idExposicion, Long idUser) {
    User user = userDao.findById(idUser);

    if (user.getAutoridad() == UserAuthority.ADMIN) {
      return TipoPermiso.CREADOR;
    }
    //Buscar permiso explícito en Gestión
    Gestion gestion =  gestionDao.findByUserAndExpo(idUser, idExposicion);
    return gestion != null ? gestion.getPermiso() : null;
  }

  /**
   * Verificar que el usuario actual tiene permiso de CREADOR
   */
  private void verificarPermisoCreador(Long idExposicion, String operacion)
    throws InvalidPermissionException {

    User currentUser = getCurrentUser();
    TipoPermiso permiso = getPermisoUsuario(idExposicion, currentUser.getIdUser());

    if (permiso != TipoPermiso.CREADOR) {
      throw new InvalidPermissionException(operacion, "Se require permiso de CREADOR");
    }
  }


  /**
   * Verificar si una expo tiene al menos una edición publicada vigente (fecha fin >= hoy)
   */
  private boolean tieneEdicionPublicadaVigente(Exposicion exposicion) {
    return exposicion.getEdiciones().stream()
      .anyMatch(edicion ->
        edicion.getEstado() == EstadoEdicion.PUBLICADA &&
        ! edicion.getFechaFin().isBefore(java.time.LocalDate.now())
      );
  }

  /**
   * Comparar exposiciones por proximidad de fecha de inicio
   * (para ordenar en vista pública)
   */
  private int comparePorProximidad(ExposicionDTO e1, ExposicionDTO e2) {
    // Implementar lógica de ordenación por proximidad
    // TODO: Necesitaréis añadir la próxima fecha de inicio en el DTO
    return 0; // Placeholder
  }

}
