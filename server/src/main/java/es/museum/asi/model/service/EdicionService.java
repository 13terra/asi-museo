package es.museum.asi.model.service;

import es.museum.asi.model.domain.*;
import es.museum.asi.model.enums.*;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.EdicionDTO;
import es.museum.asi.model.service.dto.EdicionDetalleDTO;
import es.museum.asi.repository.*;
import es.museum.asi.security.SecurityUtils;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import jdk.jfr.Percentage;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class EdicionService {

  private final Logger logger = LoggerFactory.getLogger(EdicionService.class);

  @Autowired
  private UserDao userDao;

  @Autowired
  private EdicionDao edicionDao;

  @Autowired
  private ExposicionDao exposicionDao;

  @Autowired
  private SesionDao sesionDao;

  @Autowired
  private PiezaExpuestaDao piezaExpuestaDao;

  @Autowired
  private OrdenSalaSesionDao ordenSalaSesionDao;

  @Autowired
  private ReservaDao reservaDao;

  @Autowired
  private EntradaDao entradaDao;

  @Autowired
  private GestionDao gestionDao;


  /**
   * HU20 - Crear edición
   * Sólo admin o gestor con permisos
   * Nace en estado BORRADOR
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public EdicionDTO create(Long idExposicion, LocalDate fechaInicio, LocalDate fechaFin)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    //Verificar permisos sobre expo
    verificarPermisoSobreExposicion(idExposicion, "crear ediciones");

    //Validar fechas
    if (fechaInicio.isAfter(fechaFin)) {
      throw new OperationNotAllowed("La fecha de inicio debe ser anterior a la fecha de fin");
    }

    if (fechaFin.isBefore(fechaInicio)) {
      throw new OperationNotAllowed("La fecha de fin debe ser posterior a la fecha de inicio");
    }

    // Crear edición
    Edicion edicion = new Edicion(exposicion, fechaInicio, fechaFin);
    edicionDao.create(edicion);

    logger.info("Edición creada para exposición '{}':  {} - {}",
      exposicion.getTitulo(), fechaInicio, fechaFin);

    return new EdicionDTO(edicion);
  }

  /**
   * HU21 - Detalle edición (admin/gestor)
   * Con info completa de gestión
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public EdicionDetalleDTO findDetalleForAdmin(Long idEdicion)
    throws NotFoundException, InvalidPermissionException {

    Edicion  edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    //Verificar permisos sobre la expo de esta edición
    verificarPermisoSobreExposicion(edicion.getExposicion().getIdExposicion(), "ver detalle de ediciones");

    return new EdicionDetalleDTO(edicion, true);
  }


  /**
   * HU21 - Detalle edición visitante
   * Solo si publicada y vigente
   */
  public EdicionDetalleDTO findDetallePublic(Long idEdicion)
    throws NotFoundException, OperationNotAllowed {

    Edicion  edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    //Validar que la edicion es visible
    if (edicion.getEstado() != EstadoEdicion.PUBLICADA) {
      throw new OperationNotAllowed("Esta edición NO está publicada");
    }
    if (edicion.getFechaFin().isBefore(LocalDate.now())) {
      throw new OperationNotAllowed("Esta edición ya ha finalizado.");
    }

    return new EdicionDetalleDTO(edicion, false);
  }


  /**
   * Listado de ediciones de una expo (para ADMIN/GESTOR) --> FUTURA HU?
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<EdicionDTO> findByExpo(Long idExposicion)
    throws NotFoundException,  InvalidPermissionException {

    Exposicion exposicion = exposicionDao.findById(idExposicion);
    if (exposicion == null) {
      throw new NotFoundException(idExposicion.toString(), Exposicion.class);
    }

    verificarPermisoSobreExposicion(idExposicion, "ver ediciones");

    return edicionDao.findByExposicion(idExposicion).stream()
      .map(EdicionDTO::new)
      .collect(Collectors.toList());
  }


  /**
   * HU22 - Editar edición
   * EDITOR puede editar fechas
   * Sólo CREADOR/ADMIN pueden cambiar el estado
   */
  @PreAuthorize("hasAnyAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public EdicionDTO update(Long idEdicion, LocalDate nuevaFechaInicio, LocalDate nuevaFechaFin, EstadoEdicion nuevoEstado)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    Long idExpo = edicion.getExposicion().getIdExposicion();
    TipoPermiso permiso = getPermisoSobreExposicion(idExpo);

    if(permiso == null) {
      throw new InvalidPermissionException("Editar edición", "No tiene permisos");
    }

    //Validar y actualizar fechas
    if (nuevaFechaInicio != null && nuevaFechaFin != null) {
      if (nuevaFechaInicio.isAfter(nuevaFechaFin)) {
        throw new OperationNotAllowed("La fecha de inicio no puede ser posterior a la fecha de fin");
      }

      // Validar sesiones existentes sigan dentro del rango
      Collection<Sesion> sesiones = sesionDao.findByEdicion(idEdicion);
      for (Sesion sesion : sesiones) {
        LocalDate fechaSesion = sesion.getHoraInicio().toLocalDate();
        if (fechaSesion.isBefore(nuevaFechaInicio) || fechaSesion.isAfter(nuevaFechaFin)) {
          throw new OperationNotAllowed(String.format("La sesión del %s está fuera del nuevo rango de fechas", fechaSesion));
        }
      }
    }

    edicion.setFechaInicio(nuevaFechaInicio);
    edicion.setFechaFin(nuevaFechaFin);

    //Solo CREADOR/ADMIN puede modificar estado
    if (nuevoEstado != null && nuevoEstado != edicion.getEstado()) {
      User currentUser = getCurrentUser();

      if (permiso != TipoPermiso.CREADOR && currentUser.getAutoridad() != UserAuthority.ADMIN) {
        throw new InvalidPermissionException("cambiar estado edición", "Solo el CREADOR o un ADMIN puede modificar el estado");
      }
      edicion.setEstado(nuevoEstado);
    }

    edicionDao.update(edicion);
    logger.info("Edición {} actualizada", idEdicion);

    return new EdicionDTO(edicion);
  }


  /**
   * HU23 - PUBLICAR EDICION
   * CREADOR/ADMIN --> al fin y al cabo es cambiar estado -> PUBLICADA
   * Validar 1 sala, 1 sesión DISPONIBLE y 1 PE
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public EdicionDTO publicar(Long idEdicion) throws
    NotFoundException, InvalidPermissionException,  OperationNotAllowed {

    Edicion edicion = edicionDao.findById(idEdicion);

    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    //Para pasar a PUBLICADA sólo puede estar en un estado: BORRADOR
    if (edicion.getEstado() != EstadoEdicion.BORRADOR) {
      throw new OperationNotAllowed("Solo se pueden publicar ediciones en estado BORRADOR");
    }

    //Verificar permisos CREADOR
    verificarPermisoCreador(edicion.getExposicion().getIdExposicion(), "publicar ediciones");

    //Validamos al menos 1 Pieza Expuesta
    long numPiezas = piezaExpuestaDao.findByEdicion(idEdicion).size();
    if (numPiezas == 0) {
      throw new OperationNotAllowed("La edición debe tener al menos una pieza expuesta para publicarse");
    }

    //Validamos al menos 1 sesión DISPONIBLE
    Collection<Sesion> sesionesDisponibles = sesionDao.findByEdicion(idEdicion).stream()
      .filter(s -> s.getEstadoSesion() == EstadoSesion.DISPONIBLE)
      .filter(s -> {
        LocalDate fechaSesion = s.getHoraInicio().toLocalDate();  // casteamos a LocalDate para hacer la comparación con el rango de días de la ED
        //entonces devuelve las sesiones que están entre fechaInicio y fechaFin
        return ! fechaSesion.isBefore(edicion.getFechaInicio())
          && ! fechaSesion.isAfter(edicion.getFechaFin());
      })
      .collect(Collectors.toList());

    if (sesionesDisponibles.isEmpty()) {
      throw new OperationNotAllowed("La edición debe tener al menos una sesión DISPONIBLE dentro del rango de fechas");
    }

    //Validamos que cada sesión debe tener al menos 1 sala asociada
    for (Sesion sesion : sesionesDisponibles) {
      long numSalas = ordenSalaSesionDao.findBySesion(sesion.getIdSesion()).size();
      if (numSalas == 0) {
        throw new OperationNotAllowed(
          String.format("La sesion del %s no tiene salas asignadas",  sesion.getHoraInicio())
        );
      }
    }

    //Publicamos
    edicion.setEstado(EstadoEdicion.PUBLICADA);
    edicionDao.update(edicion);

    logger.info("Edición {} publicada.  Exposición:  '{}'",
      idEdicion, edicion.getExposicion().getTitulo());

    return new EdicionDTO(edicion);

  }


  /**
   * HU24 - FINALIZAR EDICIÓN AUTOMÁTICAMENTE
   * El sistema se encarga automáticamente de cambiar el estado de una edición a FINALIZADA
   * cuando la fecha de fin de la edición haya pasado
   */
  @Transactional(readOnly = false)
  @Scheduled(cron = "0 0 0 * * ?") //0 s 0' 0h *(cualquier dia mes) *(cualquier mes) ?(dia semana)
  public void finalizarEdicionesVencidas() {
    LocalDate hoy = LocalDate.now();

    Collection<Edicion> edicionesPublicadasPasadas = edicionDao.findAll().stream()
      .filter(e -> e.getEstado() == EstadoEdicion.PUBLICADA)
      .filter(e -> e.getFechaFin().isBefore(hoy))
      .collect(Collectors.toList());

    for (Edicion edicion : edicionesPublicadasPasadas) {
      edicion.setEstado(EstadoEdicion.FINALIZADA);
      edicionDao.update(edicion);
      logger.info("Edición {} finalizada automáticamente (fecha fin: {})",
        edicion.getIdEdicion(), edicion.getFechaFin());
    }

    if (!edicionesPublicadasPasadas.isEmpty()) {
      logger.info("Total de ediciones finalizadas automáticamente: {}", edicionesPublicadasPasadas.size());
    }
  }


  /**
   * HU25 - CANCELAR EDICIÓN
   * Como CREADOR o ADMIN, cancelar ed PUBLICADA --> Sesiones y Reservas se cancelan
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public EdicionDTO cancelar(Long idEdicion)
    throws NotFoundException,  InvalidPermissionException, OperationNotAllowed {

    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    if (edicion.getEstado() != EstadoEdicion.PUBLICADA) {
      throw new OperationNotAllowed("No se puede cancelar una edición que no está publicada");
    }

    verificarPermisoCreador(edicion.getExposicion().getIdExposicion(), "Cancelar edición");

    //Cancelar todas sesiones y reservas
    Collection<Sesion> sesiones = sesionDao.findByEdicion(idEdicion);
    int totalReservasCanceladas = 0;

    for (Sesion sesion : sesiones) {
      //Cancelar reservas sesion
      Collection<Reserva> reservas = reservaDao.findBySesion(sesion.getIdSesion());
      for (Reserva reserva : reservas) {
        if (EstadoReserva.CANCELADA != reserva.getEstadoReserva()) {
          reserva.setEstadoReserva(EstadoReserva.CANCELADA);
          reservaDao.update(reserva);
          totalReservasCanceladas++;
        }

        //Cancelamos sesion
        sesion.setEstadoSesion(EstadoSesion.CANCELADA);
        sesionDao.update(sesion);
      }
    }

      // Cancelamos edición
      edicion.setEstado(EstadoEdicion.CANCELADA);
      edicionDao.update(edicion);
      logger.info("Edición {} cancelada.  {} sesiones y {} reservas canceladas automáticamente",
        idEdicion, sesiones.size(), totalReservasCanceladas);

      return new EdicionDTO(edicion);
  }


  /**
   * HU26 - Eliminar Edición, sólo CREADOR o ADMIN
   * Solo se puede eliminar si está en BORRADOR
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void delete(Long idEdicion)
    throws NotFoundException, OperationNotAllowed,
    InvalidPermissionException {

    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    if (edicion.getEstado() != EstadoEdicion.BORRADOR) {
      throw new OperationNotAllowed("Sólo se pueden eliminar ediciones que estén estado BORRADOR.");
    }

    verificarPermisoCreador(edicion.getExposicion().getIdExposicion(), "Eliminar ediciones");

    //Eliminamos piezas expuestas
    piezaExpuestaDao.findByEdicion(idEdicion).
      forEach(pieza -> piezaExpuestaDao.delete(pieza));

    //Para cada sesión (eliminamos orden + propia sesion)

    sesionDao.findByEdicion(idEdicion).forEach(sesion -> {
      //Eliminar ordenSalaSesion
      ordenSalaSesionDao.findBySesion(sesion.getIdSesion()).forEach(orden -> {
        ordenSalaSesionDao.delete(orden);
      });

      //Eliminas sesión
      sesionDao.delete(sesion);
    });

    edicionDao.delete(edicion);

    String tituloExposicion = edicion.getExposicion().getTitulo();

    logger.info("Edición {} de exposición '{}' eliminada correctamente", idEdicion, tituloExposicion);
  }


  // ==================== MÉTODOS AUXILIARES ====================


  /**
   * Obtener usuario actual autenticado
   */
  private User getCurrentUser() {
    String login = SecurityUtils.getCurrentUserLogin();
    return userDao.findByLogin(login);
  }

  /**
   * Verificar que el usuario actual tiene permisos sobre la exposición
   */
  private void verificarPermisoSobreExposicion(Long idExposicion, String operacion)
    throws InvalidPermissionException {
    TipoPermiso permiso = getPermisoSobreExposicion(idExposicion);
    if (permiso == null) {
      throw new InvalidPermissionException(operacion, "No tiene permisos sobre esta exposición");
    }
  }

  /**
   * Obtener permiso actual sobre una exposición
   */
  private TipoPermiso getPermisoSobreExposicion(Long idExposicion) {
    User currentUser = getCurrentUser();

    // ADMIN tiene permisos implícitos de CREADOR
    if (currentUser.getAutoridad() == UserAuthority.ADMIN) {
      return TipoPermiso. CREADOR;
    }

    // Buscar permiso explícito
    Gestion gestion = gestionDao.findByUserAndExpo(currentUser.getIdUser(), idExposicion);
    return gestion != null ? gestion.getPermiso() : null;
  }

  /**
   * Verificar que el usuario actual tiene permiso de CREADOR
   */
  private void verificarPermisoCreador(Long idExposicion, String operacion)
    throws InvalidPermissionException {
    User currentUser = getCurrentUser();
    TipoPermiso permiso = getPermisoSobreExposicion(idExposicion);

    if (permiso != TipoPermiso.CREADOR && currentUser.getAutoridad() != UserAuthority.ADMIN) {
      throw new InvalidPermissionException(operacion, "Se requiere permiso de CREADOR");
    }
  }

}
