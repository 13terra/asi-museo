package es.museum.asi.model.service;


import es.museum.asi.model.domain.*;
import es.museum.asi.model.enums.*;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.SalaOrdenDTO;
import es.museum.asi.model.service.dto.SesionDTO;
import es.museum.asi.model.service.dto.SesionDetalleDTO;
import es.museum.asi.model.service.dto.SesionUpdateDTO;
import es.museum.asi.repository.*;
import es.museum.asi.security.SecurityUtils;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class SesionService {

  private final Logger logger =  LoggerFactory.getLogger(SesionService.class);

  @Autowired
  private SesionDao sesionDao;

  @Autowired
  private EdicionDao edicionDao;

  @Autowired
  private SalaDao salaDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private GestionDao gestionDao;

  @Autowired
  private OrdenSalaSesionDao ordenSalaSesionDao;

  @Autowired
  private ReservaDao reservaDao;



  /**
   * HU31 - CREAR SESIÓN
   * GESTOR/ADMIN -> no se puede crear sin asignarle al menos una sala
   */
  //HAY QUE PASAR COMO PARÁMETRO LA FECHA TAMBIÉN
  @PreAuthorize("hasAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public SesionDTO create(Long idEdicion, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                          int aforo, List<Long> idSalas)
    throws NotFoundException, InvalidPermissionException,
    OperationNotAllowed {
    Edicion edicion =  edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    // Verificar permisos
    verificarPermisoSobreExposicion(edicion.getExposicion().getIdExposicion(), "crear sesiones");

    //Validación 1: Al menos una sala
    if (idSalas == null || idSalas.isEmpty()) {
      throw new OperationNotAllowed("Una sesión debe tener al menos una sala asignada");
    }

    //Validación 2: horas
    if (horaInicio.isAfter(horaFin) || horaInicio.equals(horaFin)) {
      throw new OperationNotAllowed("La hora de inicio debe ser anterior a la hora de fin");
    }

    // Validación 3: Fecha dentro del rango de la edición
    if (fecha.isBefore(edicion.getFechaInicio()) || fecha.isAfter(edicion.getFechaFin())) {
      throw new OperationNotAllowed(
        String.format("La fecha de la sesión debe estar entre %s y %s",
          edicion. getFechaInicio(), edicion.getFechaFin())
      );
    }

    //Validación 4: aforo positivo
    if (aforo <= 0) {
      throw new OperationNotAllowed("El aforo debe ser mayor que 0");
    }

    // Validación 5: Salas existen
    List<Sala> salas = new ArrayList<>();
    for (Long idSala : idSalas) {
      Sala sala = salaDao.findById(idSala);
      if(sala == null) {
        throw new NotFoundException(idSala.toString(), Sala.class);
      }
      salas.add(sala);
    }

    //Combinar fecha + horas -> LocalDateTime
    LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, horaInicio);
    LocalDateTime fechaHoraFin = LocalDateTime.of(fecha, horaFin);

    //Crear sesión
    Sesion sesion = new Sesion();
    sesion.setEdicion(edicion);
    sesion.setHoraInicio(fechaHoraInicio);
    sesion.setHoraFin(fechaHoraFin);
    sesion.setAforo(aforo);
    sesion.setEstadoSesion(EstadoSesion.DISPONIBLE);
    sesionDao.create(sesion);

    for(int i = 0; i < salas.size(); i++) {
      OrdenSalaSesion orden = new OrdenSalaSesion();
      orden.setSesion(sesion);
      orden.setSala(salas.get(i));
      orden.setOrden(i+1);
      ordenSalaSesionDao.create(orden);

    }
    logger.info("Sesión creada para edición {} en fecha {} ({} - {}) con {} salas",
      idEdicion, fecha, horaInicio, horaFin, salas.size());

    return new SesionDTO(sesion);
  }


  /**
   * HU32 - Listar sesiones
   * Gestor con permisos o admin
   * Con filtros por fecha, sala y estado
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<SesionDTO> findByEdicion(Long idEdicion, LocalDate fecha, Long idSala,
                                             EstadoSesion estado)
    throws NotFoundException, OperationNotAllowed, InvalidPermissionException {

    Edicion edicion =  edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    verificarPermisoSobreExposicion(edicion.getExposicion().getIdExposicion(), "ver sesiones");

    Collection<Sesion> sesiones = sesionDao.findByEdicion(idEdicion);

    //Filtramos y devolvemos al mismo tiempo, si es null, se salta al siguiente filtro
    return sesiones.stream()
      .filter(s -> fecha == null || s.getHoraInicio().toLocalDate().equals(fecha))
      .filter(s -> estado == null || s.getEstadoSesion() == estado)
      .filter(s -> idSala == null ||
        s.getOrdenes().stream().anyMatch(o -> o.getSala().getIdSala().equals(idSala)))
      .map(SesionDTO::new)
      .collect(Collectors.toList());
  }

  /**
   * HU33 - Ver detalle sesion
   * Admin o gestor
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public SesionDetalleDTO findDetalle(Long idSesion)
    throws NotFoundException, OperationNotAllowed, InvalidPermissionException {
    Sesion sesion = sesionDao.findById(idSesion);

    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }

    verificarPermisoSobreExposicion(sesion.getEdicion().getExposicion().getIdExposicion(), "Ver detalle sesion");

    return new SesionDetalleDTO(sesion, true);
  }

  /**
   * Ver detalle sesión (público)
   * Sólo si edición de la sesión publicada y vigente
   */
  public SesionDetalleDTO findDetallePublic(Long idSesion)
    throws NotFoundException, InvalidPermissionException {

    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }

    Edicion edicion = sesion.getEdicion();

    if (edicion.getEstado() != EstadoEdicion.PUBLICADA) {
      throw new InvalidPermissionException("Ver detalle sesión", "La edición no está publicada.");
    }

    return new SesionDetalleDTO(sesion, false);
  }


  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public SesionDTO update(Long idSesion, SesionUpdateDTO dto)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }

    verificarPermisoSobreExposicion(
      sesion.getEdicion().getExposicion().getIdExposicion(), "editar sesiones"
    );

    // 1. LÓGICA DE FECHAS Y HORAS
    // Tomamos el valor nuevo si existe, si no, mantenemos el actual
    LocalDate fechaFinal = (dto.getFecha() != null) ? dto.getFecha() : sesion.getHoraInicio().toLocalDate();
    LocalTime horaInicioFinal = (dto.getHoraInicio() != null) ? dto.getHoraInicio() : sesion.getHoraInicio().toLocalTime();
    LocalTime horaFinFinal = (dto.getHoraFin() != null) ? dto.getHoraFin() : sesion.getHoraFin().toLocalTime();

    // Solo validamos si hubo algún cambio en fecha u horas
    boolean cambioHorario = dto.getFecha() != null || dto.getHoraInicio() != null || dto.getHoraFin() != null;

    if (cambioHorario) {
      // Validación 1: Coherencia horas
      if (horaInicioFinal.isAfter(horaFinFinal) || horaInicioFinal.equals(horaFinFinal)) {
        throw new OperationNotAllowed("La hora de inicio debe ser anterior a la hora de fin");
      }

      // Validación 2: Rango edición
      Edicion edicion = sesion.getEdicion();
      if (fechaFinal.isBefore(edicion.getFechaInicio()) || fechaFinal.isAfter(edicion.getFechaFin())) {
        throw new OperationNotAllowed(String.format("La sesión debe estar entre %s y %s",
          edicion.getFechaInicio(), edicion.getFechaFin()));
      }

      sesion.setHoraInicio(LocalDateTime.of(fechaFinal, horaInicioFinal));
      sesion.setHoraFin(LocalDateTime.of(fechaFinal, horaFinFinal));
    }

    // --- 2. LÓGICA DE SALAS ---
    if (dto.getIdSalas() != null) {
      if (dto.getIdSalas().isEmpty()) {
        throw new OperationNotAllowed("La sesión debe tener al menos una sala asignada");
      }

      // Limpiar anteriores
      ordenSalaSesionDao.deleteBySesion(idSesion); // Optimización: crear método deleteBySesion en DAO

      // Asignar nuevas
      for (int i = 0; i < dto.getIdSalas().size(); i++) {
        Long idSala = dto.getIdSalas().get(i);
        Sala sala = salaDao.findById(idSala);
        if (sala == null) throw new NotFoundException(idSala.toString(), Sala.class);

        OrdenSalaSesion orden = new OrdenSalaSesion();
        orden.setSesion(sesion);
        orden.setSala(sala);
        orden.setOrden(i + 1);
        ordenSalaSesionDao.create(orden);
      }
    }

    // --- 3. LÓGICA DE AFORO ---
    if (dto.getAforo() != null) {
      // Usamos la relación ya cargada en JPA si es posible para evitar query extra
      int entradasEmitidas = sesion.getReservas().stream()
        .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
        .mapToInt(r -> r.getEntradas() != null ? r.getEntradas().size() : 0)
        .sum();

      if (dto.getAforo() < entradasEmitidas) {
        throw new OperationNotAllowed(
          String.format("El aforo (%d) no puede ser menor que las entradas emitidas (%d)",
            dto.getAforo(), entradasEmitidas));
      }

      sesion.setAforo(dto.getAforo());

      // Recálculo automático de estado
      if (entradasEmitidas >= dto.getAforo()) {
        sesion.setEstadoSesion(EstadoSesion.COMPLETA);
      } else if (sesion.getEstadoSesion() == EstadoSesion.COMPLETA) {
        sesion.setEstadoSesion(EstadoSesion.DISPONIBLE);
      }
    }

    // --- 4. LÓGICA DE ESTADO MANUAL (Prevalece sobre el automático) ---
    if (dto.getEstado() != null) {
      sesion.setEstadoSesion(dto.getEstado());
    }

    sesionDao.update(sesion);
    logger.info("Sesión {} actualizada correctamente", idSesion);

    return new SesionDTO(sesion);
  }


  /**
   * HU35 - Cancelar sesión
   * Las reservas pasan a estado cancelado automáticamente
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public SesionDTO cancelar(Long idSesion)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }

    verificarPermisoSobreExposicion(sesion.getEdicion().getExposicion().getIdExposicion(), "Cancelar sesiones");

    if (sesion.getEstadoSesion() == EstadoSesion.CANCELADA) {
      throw new OperationNotAllowed("Sesión ya cancelada");
    }

    //Cancelar todas las reservas
    Collection<Reserva> reservas = sesion.getReservas();
    int reservasCanceladas = 0;
    for (Reserva reserva : reservas) {
      if (reserva.getEstadoReserva() != EstadoReserva.CANCELADA) {
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaDao.update(reserva);
        reservasCanceladas++;
      }
    }

    //Cancelamos sesion
    sesion.setEstadoSesion(EstadoSesion.CANCELADA);
    sesionDao.update(sesion);

    logger.info("Sesión {} cancelada.   {} reservas canceladas automáticamente",
      idSesion, reservasCanceladas);

    return new SesionDTO(sesion);

  }


  /**
   * HU36 - Eliminar sesión
   * Se pueden eliminar sesiones SIN reservas
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void delete(Long idSesion)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }

    verificarPermisoSobreExposicion(sesion.getEdicion().getExposicion().getIdExposicion(), "Eliminar sesiones");

    //Validamos tema reservas
    Collection<Reserva> reservas = sesion.getReservas();
    if (!reservas.isEmpty()) {
      throw new OperationNotAllowed("No se pueden eliminar sesiones con reservas asociadas. Considere cancelar la sesion en su lugar");
    }

    //Eliminamos manualmente
    ordenSalaSesionDao.findBySesion(idSesion).forEach(orden -> {
      ordenSalaSesionDao.delete(orden);
    });

    //Eliminar sesion
    sesionDao.delete(sesion);

    logger.info("Sesión {} eliminada correctamente", idSesion);
  }


  /**
   * HU41 - Asignar sala a sesion
   * ADMIN/GESTOR podré asignar salas a una sesion.
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void asignarSala(Long idSesion, List<Long> idSalas)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }

    verificarPermisoSobreExposicion(sesion.getEdicion().getExposicion().getIdExposicion(), "Asignar sala a sesion");

    if (idSalas == null || idSalas.isEmpty()) {
      throw new OperationNotAllowed("Debe asignar al menos una sala");
    }

    // Validar que no estén duplicadas
    for (Long idSala : idSalas) {
      OrdenSalaSesion existente = ordenSalaSesionDao.findBySesionAndSala(idSesion, idSala);
      if (existente != null) {
        Sala sala = salaDao.findById(idSala);
        throw new OperationNotAllowed(
          String.format("La sala '%s' ya está asignada", sala != null ? sala.getNombre() : idSala)
        );
      }
    }

    // Obtener siguiente orden
    int siguienteOrden = ordenSalaSesionDao.findBySesion(idSesion).size() + 1;

    // Crear asignaciones
    for (Long idSala : idSalas) {
      Sala sala = salaDao.findById(idSala);
      if (sala == null) {
        throw new NotFoundException(idSala.toString(), Sala.class);
      }

      OrdenSalaSesion orden = new OrdenSalaSesion();
      orden.setSesion(sesion);
      orden.setSala(sala);
      orden.setOrden(siguienteOrden++);
      ordenSalaSesionDao.create(orden);
    }

    logger.info("{} salas asignadas a sesión {}", idSalas.size(), idSesion);
  }


  /**
   * HU42 - Desasignar sala a sesión
   * ADMIN/GESTOR podré desasignar salas a una sesion.
   * Sólo si no hay sesiones ni piezas expuestas en esa sala
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void desasignarSala(Long idSesion, Long idSala)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion.toString(), Sesion.class);
    }
    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala.toString(), Sala.class);
    }

    verificarPermisoSobreExposicion(sesion.getEdicion().getExposicion().getIdExposicion(), "Desasignar sala a sesion");

    //Validar que está asignada
    OrdenSalaSesion ordenSalaSesion = ordenSalaSesionDao.findBySesionAndSala(idSesion, idSala);
    if (ordenSalaSesion == null) {
      throw new OperationNotAllowed(
        String.format("La sala '%s' no está asignada a esta sesión", sala.getNombre())
      );
    }

    // Validación 1: No puede ser la última sala
    Collection<OrdenSalaSesion> salasAsignadas = ordenSalaSesionDao.findBySesion(idSesion);
    if (salasAsignadas.size() <= 1) {
      throw new OperationNotAllowed("Una sesión debe tener al menos una sala asignada");
    }

    // Validación 2: No debe tener piezas expuestas en esa sala
    long numPiezas = sesion.getEdicion().getPiezasExpuestas().stream()
      .filter(p -> p.getSala().getIdSala().equals(idSala))
      .count();

    if (numPiezas > 0) {
      throw new OperationNotAllowed(
        String.format("No se puede desasignar la sala '%s' porque tiene %d piezas expuestas en esta edición",
          sala.getNombre(), numPiezas)
      );
    }

    // Desasignar
    ordenSalaSesionDao.delete(ordenSalaSesion);

    logger.info("Sala '{}' desasignada de sesión {}", sala.getNombre(), idSesion);
  }


  /**
   * Listar salas asignadas a una sesión (ordenadas)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public List<SalaOrdenDTO> listarSalasAsignadas(Long idSesion)
    throws NotFoundException, InvalidPermissionException {

    Sesion sesion = sesionDao.findById(idSesion);
    if (sesion == null) {
      throw new NotFoundException(idSesion. toString(), Sesion.class);
    }

    verificarPermisoSobreExposicion(
      sesion.getEdicion().getExposicion().getIdExposicion(),
      "ver salas de sesiones"
    );

    return ordenSalaSesionDao.findBySesion(idSesion).stream()
      .sorted((o1, o2) -> o1.getOrden().compareTo(o2.getOrden()))
      .map(SalaOrdenDTO::new)
      .collect(Collectors.toList());
  }



  // ==================== MÉTODOS AUXILIARES ====================



  private User getCurrentUser() {
    String login = SecurityUtils.getCurrentUserLogin();
    return userDao.findByLogin(login);
  }

  private TipoPermiso getPermisoSobreExposicion(Long idExposicion) {
    User currentUser = getCurrentUser();

    if (currentUser. getAutoridad() == UserAuthority.ADMIN) {
      return TipoPermiso. CREADOR;
    }

    Gestion gestion = gestionDao.findByUserAndExpo(
      currentUser.getIdUser(),
      idExposicion
    );
    return gestion != null ? gestion.getPermiso() : null;
  }

  private void verificarPermisoSobreExposicion(Long idExposicion, String operacion)
    throws InvalidPermissionException {
    TipoPermiso permiso = getPermisoSobreExposicion(idExposicion);
    if (permiso == null) {
      throw new InvalidPermissionException(operacion, "No tiene permisos sobre esta exposición");
    }
  }

}
