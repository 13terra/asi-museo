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
import es.museum.asi.model.service.dto.GestorPermisoDTO;
import es.museum.asi.repository.*;
import es.museum.asi.security.SecurityUtils;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

  @Autowired
  private PiezaExpuestaDao piezaExpuestaDao;

  @Autowired
  private SesionDao sesionDao;

  @Autowired
  private OrdenSalaSesionDao ordenSalaSesionDao;

  @Autowired
  private ReservaDao reservaDao;

  @Autowired
  private EntradaDao entradaDao;

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
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
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
      .sorted((e1,e2) -> comparePorProximidad(e1, e2))
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
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public ExposicionDTO create(String titulo, String descripcion) {
    Exposicion exposicion = new Exposicion(titulo, descripcion);
    exposicionDao.create(exposicion);

    //Si es GESTOR, asignar permiso CREADOR automáticamente
    User currentUser = getCurrentUser();
    if (currentUser.getAutoridad() == UserAuthority.GESTOR) {
      Gestion gestion = new Gestion(currentUser, exposicion, TipoPermiso.CREADOR);
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
  public ExposicionDTO update(Long idExposicion, String titulo, String descripcion, EstadoExpo nuevoEstado, String portadaUrl)
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
    if (portadaUrl != null) {
      exposicion.setPortadaUrl(portadaUrl);
    }

    // Solo CREADOR o ADMIN pueden cambiar el estado
    if (nuevoEstado != null && nuevoEstado != exposicion.getEstadoExpo()) {
      if (permiso != TipoPermiso.CREADOR && currentUser.getAutoridad() != UserAuthority.ADMIN) {
        throw new InvalidPermissionException("cambiar el estado de la exposición", "Solo el CREADOR o ADMIN pueden modificar el estado");
      }

      // NUEVA VALIDACIÓN: Evitar bypass de lógica de negocio
      if (nuevoEstado == EstadoExpo.ARCHIVADA) {
        throw new OperationNotAllowed("No se puede cambiar a ARCHIVADA desde la edición. Use el botón 'Archivar' específico.");
      }

      // Paso inverso para forzar el uso de desarchivar
      if (exposicion.getEstadoExpo() == EstadoExpo.ARCHIVADA && nuevoEstado == EstadoExpo.ACTIVA) {
        throw new OperationNotAllowed("Para reactivar una exposición archivada, use el botón 'Desarchivar'.");
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


    String tituloExposicion = exposicion.getTitulo();

    // 1. Eliminar gestiones
    gestionDao.findByExpo(idExposicion).forEach(gestion -> gestionDao.delete(gestion));

    // 2. Para cada edición, eliminar sus dependencias
    edicionDao. findByExposicion(idExposicion).forEach(edicion -> {
      Long idEdicion = edicion.getIdEdicion();

      // 2.1 Eliminar piezas expuestas de la edición
      piezaExpuestaDao.findByEdicion(idEdicion).forEach(pieza -> {
        piezaExpuestaDao.delete(pieza);
      });

      // 2.2 Para cada sesión de la edición
      sesionDao.findByEdicion(idEdicion).forEach(sesion -> {
        Long idSesion = sesion.getIdSesion();

        // 2.2.1 Eliminar OrdenSalaSesion (relación N:N Sesion-Sala)
        ordenSalaSesionDao.findBySesion(idSesion).forEach(orden -> {
          ordenSalaSesionDao.delete(orden);
        });

        // 2.2.2 Eliminar reservas y sus entradas
        reservaDao. findBySesion(idSesion).forEach(reserva -> {
          // Primero eliminar entradas de la reserva
          entradaDao.findByReserva(reserva. getIdReserva()).forEach(entrada -> {
            entradaDao.delete(entrada);
          });
          // Después eliminar la reserva
          reservaDao.delete(reserva);
        });

        // 2.2.3 Eliminar la sesión
        sesionDao. delete(sesion);
      });

      // 2.3 Eliminar la edición
      edicionDao.delete(edicion);
    });

    // 3. Finalmente eliminar la exposición
    exposicionDao.delete(exposicion);

    logger.info("Exposición '{}' eliminada correctamente con todas sus dependencias", tituloExposicion);
  }


  // ==================== HU17-HU19: GESTIÓN DE PERMISOS ====================

  /**
   * HU17 - Listar permisos de una exposición (Solo CREADOR o ADMIN)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<GestorPermisoDTO> listarPermisos(Long idExposicion)
    throws NotFoundException, InvalidPermissionException {
    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    verificarPermisoCreador(idExposicion, "listar permisos de la exposición");

    return gestionDao.findByExpo(idExposicion).stream()
      .map(GestorPermisoDTO::new)
      .collect(Collectors.toList());
  }

  /**
   * HU18 - Asignar o modificar permiso (Solo CREADOR o ADMIN)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public GestorPermisoDTO asignarPermisoDTO(Long idExposicion,
                                            Long idUserGestor,
                                            TipoPermiso permiso)
    throws NotFoundException, InvalidPermissionException,
    OperationNotAllowed {
    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    User gestor = userDao.findById(idUserGestor);
    if(gestor == null) {
      throw new NotFoundException(idUserGestor.toString(), User.class);
    }

    //Solo se pueden asignar permisos a usuarios GESTOR
    // Solo se pueden asignar permisos a usuarios GESTOR
    if (gestor. getAutoridad() != UserAuthority.GESTOR) {
      throw new OperationNotAllowed("Solo se pueden asignar permisos a usuarios con rol GESTOR");
    }

    verificarPermisoCreador(idExposicion, "asignar permisos");

    //Verificar si ya existe una gestión
    Gestion gestionExistente = gestionDao.findByUserAndExpo(idUserGestor, idExposicion);

    if (gestionExistente != null) {
      //Modificar permiso existente
      gestionExistente.setPermiso(permiso);
      gestionDao.update(gestionExistente);
      logger.info("Permiso de {} sobre exposición {} modificado a {}",
        gestor.getLogin(), exposicion.getTitulo(), permiso);
      return new GestorPermisoDTO(gestionExistente);
    } else {
      //Crear nueva gestión
      Gestion nuevaGestion = new Gestion(gestor, exposicion, permiso);
      gestionDao.create(nuevaGestion);
      logger.info("Permiso {} asignado a {} sobre exposición {}",
        permiso, gestor.getLogin(), exposicion.getTitulo());
      return new GestorPermisoDTO(nuevaGestion);
    }
  }

  /**
   * HU19 - Revocar permiso (Solo CREADOR o ADMIN)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void revocarPermiso(Long idExposicion, Long idUserGestor)
    throws NotFoundException, InvalidPermissionException,
    OperationNotAllowed {
    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    verificarPermisoCreador(idExposicion, "revocar permisos");

    Gestion gestion = gestionDao.findByUserAndExpo(idUserGestor, idExposicion);
    if (gestion == null) {
      throw new NotFoundException("No existe permiso para este gestor en esta exposición", Gestion.class);
    }

    // No se puede revocar al CREADOR principal sin transferir el rol primero
    long numCreadores = gestionDao.findByExpoAndPermiso(idExposicion, TipoPermiso. CREADOR).size();
    if (gestion.getPermiso() == TipoPermiso.CREADOR && numCreadores <= 1) {
      throw new OperationNotAllowed(
        "No se puede revocar al único CREADOR de la exposición. " +
          "Debe transferir el rol a otro gestor primero."
      );
    }

    User gestor = userDao.findById(idUserGestor);
    gestionDao.delete(gestion);
    logger.info("Permiso de {} sobre exposición {} revocado",
      gestor.getLogin(), exposicion.getTitulo());
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
   * Calcular la próxima fecha de inicio de edición publicada vigente
   * Devuelve la fecha de inicio de la edición publicada más próxima (futura o en curso)
   */
  private LocalDate calcularProximaFechaInicio(Exposicion expo) {
    LocalDate hoy = LocalDate.now();

    return expo.getEdiciones().stream()
      .filter(ed -> ed.getEstado() == EstadoEdicion.PUBLICADA)
      .filter(ed -> !ed.getFechaFin().isBefore(hoy)) // Ediciones vigentes (no finalizadas)
      .map(ed -> ed.getFechaInicio())
      .min(LocalDate::compareTo) // La fecha de inicio más temprana
      .orElse(null); // Si no hay ediciones vigentes, devuelve null
  }

  /**
   * Comparar exposiciones por proximidad de fecha de inicio
   * (para ordenar en vista pública)
   */
  private int comparePorProximidad(ExposicionDTO e1, ExposicionDTO e2) {

    Exposicion expo1 = exposicionDao.findById(e1.getIdExposicion());
    Exposicion expo2 = exposicionDao.findById(e2.getIdExposicion());

    if (expo1 == null || expo2 == null) return 0;

    // Calcular la próxima fecha de inicio para cada exposición
    LocalDate fecha1 = calcularProximaFechaInicio(expo1);
    LocalDate fecha2 = calcularProximaFechaInicio(expo2);

    // Si ninguna tiene fecha, son iguales
    if (fecha1 == null && fecha2 == null) return 0;

    // Las que no tienen fecha van al final
    if (fecha1 == null) return 1;
    if (fecha2 == null) return -1;

    LocalDate hoy = LocalDate.now();

    // Caso 1: Ambas son FUTURAS → la más cercana primero
    if (fecha1.isAfter(hoy) && fecha2.isAfter(hoy)) {
      return fecha1.compareTo(fecha2);
    }

    // Caso 2: Ambas están EN CURSO (inicio <= hoy, fin >= hoy) → las más recientes primero
    if (! fecha1.isAfter(hoy) && !fecha2.isAfter(hoy)) {
      return fecha2.compareTo(fecha1); // Orden inverso:  más reciente primero
    }

    // Caso 3: Una futura, otra en curso → FUTURA primero
    if (fecha1.isAfter(hoy)) return -1;
    return 1;

  }

}
