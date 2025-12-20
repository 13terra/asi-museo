package es.museum.asi.model.service;

import es.museum.asi.model.domain.*;
import es.museum.asi.model.enums.*;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.ReservaDTO;
import es.museum.asi.model.service.dto.ReservaDetalleDTO;
import es.museum.asi.model.service.dto.ReservarEntradasDTO;
import es.museum.asi.repository.*;
import es.museum.asi.security.SecurityUtils;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ReservaService {

  private final Logger logger = LoggerFactory.getLogger(ReservaService.class);

  @Autowired
  private ReservaDao reservaDao;

  @Autowired
  private SesionDao sesionDao;

  @Autowired
  private TipoEntradaDao tipoEntradaDao;

  @Autowired
  private EntradaDao entradaDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private GestionDao gestionDao;

  @Autowired
  private ExposicionDao exposicionDao;

  @Autowired
  private EdicionDao edicionDao;

  // Margen mínimo de cancelación (24 horas)
  private static final long HORAS_MINIMAS_CANCELACION = 24;



  /**
   * HU51 - Reservar entradas
   * Sólo visitantes autenticados
   * Validaciones: aforo disponible, sesion DISPONIBLE, datos completos
   */
  @PreAuthorize("hasAuthority('VISITANTE')")
  @Transactional(readOnly = false)
  public ReservaDetalleDTO reservarEntradas(ReservarEntradasDTO request)
    throws NotFoundException, OperationNotAllowed {

    Sesion sesion = sesionDao.findById(request.getIdSesion());
    if (sesion == null) {
      throw new NotFoundException(request.getIdSesion().toString(), Sesion.class);
    }

    if (sesion.getEstadoSesion() != EstadoSesion.DISPONIBLE) {
      throw new OperationNotAllowed("No se pueden reservar entradas para una sesión que no está disponible");
    }

    int totalEntradasSolicitadas = request.getEntradasPorTipo().values().stream()
      .mapToInt(Integer::intValue).sum();

    if (totalEntradasSolicitadas == 0) {
      throw new OperationNotAllowed("Debes solicitar al menos una entrada");
    }

    int aforoOcupado = reservaDao.findBySesion(sesion.getIdSesion()).stream()
      .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
      .mapToInt(r -> r.getEntradas() != null ? r.getEntradas().size() : 0)
      .sum();

    int aforoDisponible = sesion.getAforo() - aforoOcupado;

    if (totalEntradasSolicitadas > aforoDisponible) {
      throw new OperationNotAllowed(
        String.format("Aforo insuficiente.  Disponible: %d, Solicitado: %d",
          aforoDisponible, totalEntradasSolicitadas)
      );
    }

    // Validación 4: Datos del comprador completos
    if (request.getNombrePila() == null || request.getApellido1() == null ||
      request.getEmail() == null || request.getTelefono() == null ||
      request.getPais() == null) {
      throw new OperationNotAllowed("Los datos del comprador son obligatorios");
    }

    // Validación 5: Datos de asistentes (deben coincidir con número de entradas)
    if (request.getDatosAsistentes() == null ||
      request.getDatosAsistentes().size() != totalEntradasSolicitadas) {
      throw new OperationNotAllowed(
        String.format("Debe proporcionar datos para los %d asistentes", totalEntradasSolicitadas)
      );
    }

    // Obtener usuario actual
    User comprador = getCurrentUser();

    // Crear reserva
    Reserva reserva = new Reserva();
    reserva.setUser(comprador);
    reserva.setSesion(sesion);
    reserva.setNombrePila(request.getNombrePila());
    reserva.setApellido1(request.getApellido1());
    reserva.setApellido2(request.getApellido2());
    reserva.setEmail(request.getEmail());
    reserva.setTelefono(request.getTelefono());
    reserva.setPais(request.getPais());
    reserva.setFechaReserva(LocalDateTime.now());
    reserva.setEstadoReserva(EstadoReserva. CONFIRMADA);
    reservaDao.create(reserva);

    // Crear entradas
    List<Entrada> entradas = new ArrayList<>();
    int indexAsistente = 0;

    for (Map.Entry<Long, Integer> entry : request.getEntradasPorTipo().entrySet()) {
      Long idTipoEntrada = entry.getKey();
      Integer cantidad = entry.getValue();

      TipoEntrada tipoEntrada = tipoEntradaDao. findById(idTipoEntrada);
      if (tipoEntrada == null) {
        throw new NotFoundException(idTipoEntrada.toString(), TipoEntrada.class);
      }

      for (int i = 0; i < cantidad; i++) {
        ReservarEntradasDTO.DatosAsistente datosAsistente =
          request.getDatosAsistentes().get(indexAsistente);

        Entrada entrada = new Entrada();
        entrada.setReserva(reserva);
        entrada.setTipoEntrada(tipoEntrada);
        entrada.setDni(datosAsistente.getDni());
        entrada.setNombrePila(datosAsistente.getNombrePila());
        entrada.setApellido1(datosAsistente.getApellido1());
        entrada.setApellido2(datosAsistente.getApellido2());
        entrada.setPrecio(tipoEntrada.getPrecio()); // Trazabilidad de precio histórico
        entradaDao.create(entrada);

        entradas.add(entrada);
        indexAsistente++;
      }
    }

    // Actualizar estado de la sesión si se completa el aforo
    if (aforoOcupado + totalEntradasSolicitadas >= sesion.getAforo()) {
      sesion.setEstadoSesion(EstadoSesion.COMPLETA);
      sesionDao.update(sesion);
    }

    float importeTotal = entradas.stream()
      .map(Entrada::getPrecio)
      .reduce(0f, Float::sum);

    logger.info("Reserva {} creada:  {} entradas para sesión {} (Total: {}€)",
      reserva.getIdReserva(), totalEntradasSolicitadas, sesion.getIdSesion(), importeTotal);

    return new ReservaDetalleDTO(reserva, true);
  }


  /**
   * HU52 - Listado de reservas (visitante autenticado)
   */
  @PreAuthorize("hasAuthority('VISITANTE')")
  public Collection<ReservaDTO> findMisReservas(EstadoReserva filtroEstado) {
    User currentUser = getCurrentUser();

    return reservaDao.findByUser(currentUser.getIdUser()).stream()
      .filter(r -> filtroEstado == null || r. getEstadoReserva() == filtroEstado)
      .sorted((r1, r2) -> r2.getFechaReserva().compareTo(r1.getFechaReserva())) // Más recientes primero
      .map(ReservaDTO::new)
      .collect(Collectors.toList());
  }

  /**
   * HU53 - Ver detalle de mi reserva
   * Sólo el propietario puede ver su reserva
   */
  @PreAuthorize("hasAuthority('VISITANTE')")
  public ReservaDetalleDTO findMiReservaDetalle(Long idReserva)
    throws NotFoundException, OperationNotAllowed {

    Reserva reserva = reservaDao.findById(idReserva);
    if (reserva == null) {
      throw new NotFoundException(idReserva. toString(), Reserva.class);
    }

    User currentUser = getCurrentUser();
    if (! reserva.getUser().getIdUser().equals(currentUser.getIdUser())) {
      throw new OperationNotAllowed("No tiene permisos para ver esta reserva");
    }

    return new ReservaDetalleDTO(reserva, true);
  }


  /**
   * HU54 - Cancelar reserva
   * Validaciones: margen mínimo 24h, sesión no cancelada/finalizada
   */
  @PreAuthorize("hasAuthority('VISITANTE')")
  @Transactional(readOnly = false)
  public ReservaDTO cancelarMiReserva(Long idReserva)
    throws NotFoundException, OperationNotAllowed {

    Reserva reserva = reservaDao.findById(idReserva);
    if (reserva == null) {
      throw new NotFoundException(idReserva.toString(), Reserva.class);
    }

    User currentUser = getCurrentUser();
    if (!reserva.getUser().getIdUser().equals(currentUser.getIdUser())) {
      throw new OperationNotAllowed("No tiene permisos para cancelar esta reserva");
    }

    // Validación 1: La reserva ya debe estar CONFIRMADA
    if (reserva. getEstadoReserva() != EstadoReserva. CONFIRMADA) {
      throw new OperationNotAllowed("Solo se pueden cancelar reservas confirmadas");
    }

    Sesion sesion = reserva.getSesion();

    // Validación 2: Sesión no puede estar CANCELADA ni FINALIZADA
    if (sesion.getEstadoSesion() == EstadoSesion.CANCELADA) {
      throw new OperationNotAllowed("La sesión ya está cancelada");
    }
    if (sesion.getEstadoSesion() == EstadoSesion.FINALIZADA) {
      throw new OperationNotAllowed("La sesión ya ha finalizado");
    }

    // Validación 3: Margen mínimo (24 horas antes)
    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime limiteCancel = sesion.getHoraInicio().minusHours(HORAS_MINIMAS_CANCELACION);

    if (ahora.isAfter(limiteCancel)) {
      throw new OperationNotAllowed(
        String.format("Solo se puede cancelar con al menos %d horas de antelación",
          HORAS_MINIMAS_CANCELACION)
      );
    }

    // Cancelar reserva
    reserva.setEstadoReserva(EstadoReserva. CANCELADA);
    reservaDao.update(reserva);

    // Liberar aforo (actualizar estado sesión si estaba completa)
    if (sesion.getEstadoSesion() == EstadoSesion.COMPLETA) {
      int aforoOcupado = reservaDao. findBySesion(sesion. getIdSesion()).stream()
        .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
        .mapToInt(r -> r. getEntradas() != null ? r.getEntradas().size() : 0)
        .sum();

      if (aforoOcupado < sesion.getAforo()) {
        sesion.setEstadoSesion(EstadoSesion.DISPONIBLE);
        sesionDao.update(sesion);
      }
    }

    logger.info("Reserva {} cancelada por el usuario {}.  {} entradas liberadas",
      idReserva, currentUser.getLogin(), reserva.getEntradas().size());

    return new ReservaDTO(reserva);
  }


  /**
   * HU57 - Listar reservas de una edición (ADMIN/GESTOR)
   * Con filtros por sesión, estado, fecha
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<ReservaDTO> findReservasByEdicion(Long idEdicion, Long idSesionFiltro,
                                                      EstadoReserva estadoFiltro, LocalDateTime fechaDesde,
                                                      LocalDateTime fechaHasta)
    throws NotFoundException, InvalidPermissionException {

    Edicion edicion = edicionDao.findById(idEdicion);

    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), es.museum.asi.model.domain.Edicion.class);
    }

    // Verificar permisos
    verificarPermisoSobreExposicion(
      edicion.getExposicion().getIdExposicion(),
      "ver reservas"
    );

    // Obtener todas las sesiones de la edición
    Collection<Sesion> sesiones = sesionDao.findByEdicion(idEdicion);

    // Obtener reservas y aplicar filtros
    return sesiones.stream()
      .filter(s -> idSesionFiltro == null || s.getIdSesion().equals(idSesionFiltro))
      .flatMap(s -> reservaDao.findBySesion(s.getIdSesion()).stream())
      .filter(r -> estadoFiltro == null || r.getEstadoReserva() == estadoFiltro)
      .filter(r -> fechaDesde == null || ! r.getFechaReserva().isBefore(fechaDesde))
      .filter(r -> fechaHasta == null || !r.getFechaReserva().isAfter(fechaHasta))
      .sorted((r1, r2) -> r2.getFechaReserva().compareTo(r1.getFechaReserva()))
      .map(ReservaDTO::new)
      .collect(Collectors.toList());
  }

  /**
   * HU58 - Ver detalle de reserva (ADMIN/GESTOR)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public ReservaDetalleDTO findReservaDetalleAdmin(Long idReserva)
    throws NotFoundException, InvalidPermissionException {

    Reserva reserva = reservaDao. findById(idReserva);
    if (reserva == null) {
      throw new NotFoundException(idReserva.toString(), Reserva.class);
    }

    verificarPermisoSobreExposicion(
      reserva. getSesion().getEdicion().getExposicion().getIdExposicion(),
      "ver detalles de reservas"
    );

    return new ReservaDetalleDTO(reserva, true);
  }



  // ==================== MÉTODOS AUXILIARES ====================

  private User getCurrentUser() {
    String login = SecurityUtils.getCurrentUserLogin();
    return userDao.findByLogin(login);
  }

  private void verificarPermisoSobreExposicion(Long idExposicion, String operacion)
    throws InvalidPermissionException {
    TipoPermiso permiso = getPermisoSobreExposicion(idExposicion);
    if (permiso == null) {
      throw new InvalidPermissionException(operacion, "No tiene permisos sobre esta exposición");
    }
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

}
