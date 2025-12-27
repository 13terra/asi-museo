package es.museum.asi.model.service;


import es.museum.asi.model.domain.*;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.PiezaExpuestaDTO;
import es.museum.asi.repository.*;
import es.museum.asi.security.SecurityUtils;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Serivicio para gestión de piezas expuestas
 */
@Service
@Transactional(readOnly=true, rollbackFor =  Exception.class)
public class PiezaExpuestaService {

  private final Logger logger = LoggerFactory.getLogger(PiezaExpuestaService.class);

  @Autowired
  private PiezaExpuestaDao piezaExpuestaDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private GestionDao  gestionDao;

  @Autowired
  private EdicionDao edicionDao;

  @Autowired
  private ObraDao obraDao;

  @Autowired
  private SalaDao salaDao;


  /**
   * HU27 - Añadir pieza expuesta
   * Añadir una obra del catálogo a una edición: Se indica la obra, sala, orden y texto curatorial
   * Combinación obra+edición única
   * Orden debe ser único dentro de edición+sala
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public PiezaExpuestaDTO create(Long idObra, Long idEdicion, Long idSala, Integer orden,
                                 String textoCuratorial, Boolean esPortada)
    throws NotFoundException, OperationNotAllowed, InvalidPermissionException {

    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    Obra obra = obraDao.findById(idObra);
    if (obra == null) {
      throw new NotFoundException(idObra.toString(), Obra.class);
    }

    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala.toString(), Sala.class);
    }

    Collection<PiezaExpuesta> piezasDeLaEdicion = piezaExpuestaDao.findByEdicion(idEdicion);

    //Verificamos permisos
    verificarPermisoSobreExposicion(edicion.getExposicion().getIdExposicion(), "Añadir pieza expuesta");

    //Validación: Obra + Edición única
    boolean yaExiste = piezasDeLaEdicion.stream()
      .anyMatch(pe -> pe.getObra().getIdObra().equals(idObra));

    if (yaExiste) {
      throw new OperationNotAllowed("La obra ya está asignada a esa edición.");
    }

  // Variable para determinar el orden definitivo sin modificar el parámetro 'orden'
    Integer ordenFinal = null;

    // Validación: Orden debe ser único dentro de Edición + Sala
    if (orden != null) {
      boolean ordenDuplicado = piezasDeLaEdicion.stream()
        .filter(pe -> pe.getSala().getIdSala().equals(idSala))
        .anyMatch(pe -> pe.getOrden().equals(orden));

      if (ordenDuplicado) {
        throw new OperationNotAllowed(
          String.format("Ya existe una pieza con orden %d en la sala '%s'", orden, sala.getNombre())
        );
      }
      // Si no está duplicado, el orden final es el que vino por parámetro
      ordenFinal = orden;

    } else {
      // Calculamos el nuevo orden y lo asignamos a la NUEVA variable
      ordenFinal = piezasDeLaEdicion.stream()
        .filter(pe -> pe.getSala().getIdSala().equals(idSala))
        .mapToInt(PiezaExpuesta::getOrden).max().orElse(0) + 1;
    }

    // Validación 3: Si es portada, desmarcar la anterior
    if (Boolean.TRUE.equals(esPortada)) {
      PiezaExpuesta portadaAnterior = piezaExpuestaDao.findPortadaByEdicion(idEdicion);
      if (portadaAnterior != null) {
        portadaAnterior.setPortada(false);
        piezaExpuestaDao.update(portadaAnterior);
      }
    }

    // Crear pieza expuesta
    PiezaExpuesta piezaExpuesta = new PiezaExpuesta();
    piezaExpuesta.setEdicion(edicion);
    piezaExpuesta.setObra(obra);
    piezaExpuesta. setSala(sala);
    piezaExpuesta.setOrden(ordenFinal);
    piezaExpuesta.setTextoCuratorial(textoCuratorial);
    piezaExpuesta.setPortada(Boolean.TRUE.equals(esPortada));
    piezaExpuestaDao.create(piezaExpuesta);

    logger.info("Pieza expuesta creada:  Obra '{}' en sala '{}' (orden {}) para edición {}",
      obra.getTitulo(), sala.getNombre(), ordenFinal, idEdicion);

    return new PiezaExpuestaDTO(piezaExpuesta);

  }


  /**
   * HU28 - Listar piezas expuestas
   * Por edición
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<PiezaExpuestaDTO> findByEdicion(Long idEdicion)
    throws NotFoundException, InvalidPermissionException {
    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    verificarPermisoSobreExposicion(edicion.getExposicion().getIdExposicion(), "Ver piezas expuestas");

    return piezaExpuestaDao.findByEdicion(idEdicion).stream()
      .sorted((p1, p2) -> {
        //Primero x sala, luego por orden
        int compareSala = p1.getSala().getNombre().compareTo(p2.getSala().getNombre());
        return compareSala != 0 ? compareSala : p1.getOrden().compareTo(p2.getOrden());
      })
      .map(PiezaExpuestaDTO::new)
      .collect(Collectors.toList());
  }

  /**
   * Listar piezas expuestas (PÚBLICO)
   * Solo si la edición está PUBLICADA
   */
  public Collection<PiezaExpuestaDTO> findByEdicionPublic(Long idEdicion)
    throws NotFoundException, OperationNotAllowed {
    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    // Solo permitir si está publicada (o finalizada/cancelada si se desea histórico, pero HU dice publicada)
    // Asumimos que el público puede ver lo publicado.
    // Si se requiere que la expo esté ACTIVA, añadir check.
    // HU21 dice: "Los visitantes pueden consultar el detalle de una edición publicada"
    if (edicion.getEstado() == es.museum.asi.model.enums.EstadoEdicion.BORRADOR) {
       throw new OperationNotAllowed("Esta edición no es pública");
    }

    return piezaExpuestaDao.findByEdicion(idEdicion).stream()
      .sorted((p1, p2) -> {
        int compareSala = p1.getSala().getNombre().compareTo(p2.getSala().getNombre());
        return compareSala != 0 ? compareSala : p1.getOrden().compareTo(p2.getOrden());
      })
      .map(PiezaExpuestaDTO::new)
      .collect(Collectors.toList());
  }


  /**
   * HU28 - Listar piezas expuestas
   * Por sala
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<PiezaExpuestaDTO> findBySala(Long idSala, Long idEdicion)
    throws NotFoundException, InvalidPermissionException {

    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala.toString(), Sala.class);
    }

    Edicion edicion = edicionDao.findById(idEdicion);
    if (edicion == null) {
      throw new NotFoundException(idEdicion.toString(), Edicion.class);
    }

    verificarPermisoSobreExposicion(
      edicion.getExposicion().getIdExposicion(),
      "ver piezas expuestas"
    );

    return piezaExpuestaDao.findBySala(idSala).stream()
      .filter(pe -> pe.getEdicion().getIdEdicion().equals(idEdicion))
      .sorted((p1, p2) -> p1.getOrden().compareTo(p2.getOrden()))
      .map(PiezaExpuestaDTO::new)
      .collect(Collectors.toList());
  }


  // FALTAN EDITAR PE (HU29) + ELIMINAR PE(HU30)


  // ==================== HU29: EDITAR PIEZA EXPUESTA ====================

  /**
   * HU29 - Editar pieza expuesta
   * Permite modificar:  sala, orden, texto curatorial, portada
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public PiezaExpuestaDTO update(Long idPiezaExpuesta, Long nuevaIdSala, Integer nuevoOrden,
                                 String nuevoTextoCuratorial, Boolean esPortada)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    PiezaExpuesta piezaExpuesta = piezaExpuestaDao.findById(idPiezaExpuesta);
    if (piezaExpuesta == null) {
      throw new NotFoundException(idPiezaExpuesta.toString(), PiezaExpuesta.class);
    }

    verificarPermisoSobreExposicion(
      piezaExpuesta.getEdicion().getExposicion().getIdExposicion(),
      "editar piezas expuestas"
    );

    // Actualizar sala
    if (nuevaIdSala != null && !nuevaIdSala.equals(piezaExpuesta.getSala().getIdSala())) {
      Sala nuevaSala = salaDao.findById(nuevaIdSala);
      if (nuevaSala == null) {
        throw new NotFoundException(nuevaIdSala. toString(), Sala.class);
      }
      piezaExpuesta.setSala(nuevaSala);

      // Si cambia de sala, auto-asignar orden al final de la nueva sala
      if (nuevoOrden == null) {
        nuevoOrden = piezaExpuestaDao.findByEdicion(piezaExpuesta.getEdicion().getIdEdicion()).stream()
          .filter(pe -> pe.getSala().getIdSala().equals(nuevaIdSala))
          .filter(pe -> ! pe.getIdPiezaExpuesta().equals(idPiezaExpuesta))
          .mapToInt(PiezaExpuesta::getOrden)
          .max()
          .orElse(0) + 1;
      }
    }

    // Actualizar orden (validar unicidad en sala)
    if (nuevoOrden != null && !nuevoOrden.equals(piezaExpuesta.getOrden())) {
      Long idSalaFinal = nuevaIdSala != null ? nuevaIdSala : piezaExpuesta.getSala().getIdSala();

      // Se utiliza para validar si hay alguna pieza con el orden que se le pasa, sino protestaba
      final Integer ordenParaValidar = nuevoOrden;

      boolean ordenDuplicado = piezaExpuestaDao.findByEdicion(piezaExpuesta.getEdicion().getIdEdicion()).stream()
        .filter(pe -> pe.getSala().getIdSala().equals(idSalaFinal))
        .filter(pe -> !pe.getIdPiezaExpuesta().equals(idPiezaExpuesta))
        .anyMatch(pe -> pe.getOrden().equals(ordenParaValidar));

      if (ordenDuplicado) {
        throw new OperationNotAllowed(
          String.format("Ya existe una pieza con orden %d en esta sala", nuevoOrden)
        );
      }
      piezaExpuesta.setOrden(nuevoOrden);
    }

    // Actualizar texto curatorial
    if (nuevoTextoCuratorial != null) {
      piezaExpuesta.setTextoCuratorial(nuevoTextoCuratorial);
    }

    // Actualizar portada
    if (Boolean.TRUE.equals(esPortada) && ! piezaExpuesta.getPortada()) {
      // Desmarcar portada anterior
      PiezaExpuesta portadaAnterior = piezaExpuestaDao. findPortadaByEdicion(
        piezaExpuesta.getEdicion().getIdEdicion()
      );
      if (portadaAnterior != null && ! portadaAnterior.getIdPiezaExpuesta().equals(idPiezaExpuesta)) {
        portadaAnterior.setPortada(false);
        piezaExpuestaDao.update(portadaAnterior);
      }
      piezaExpuesta.setPortada(true);
    } else if (Boolean.FALSE.equals(esPortada)) {
      piezaExpuesta.setPortada(false);
    }

    piezaExpuestaDao. update(piezaExpuesta);
    logger.info("Pieza expuesta {} actualizada", idPiezaExpuesta);

    return new PiezaExpuestaDTO(piezaExpuesta);
  }

  // ==================== HU30: ELIMINAR PIEZA EXPUESTA ====================

  /**
   * HU30 - Eliminar pieza expuesta
   * La obra permanece en el catálogo general
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void delete(Long idPiezaExpuesta)
    throws NotFoundException, InvalidPermissionException, OperationNotAllowed {

    PiezaExpuesta piezaExpuesta = piezaExpuestaDao.findById(idPiezaExpuesta);
    if (piezaExpuesta == null) {
      throw new NotFoundException(idPiezaExpuesta.toString(), PiezaExpuesta.class);
    }

    verificarPermisoSobreExposicion(
      piezaExpuesta.getEdicion().getExposicion().getIdExposicion(),
      "eliminar piezas expuestas"
    );

    // Advertencia si es la portada
    if (piezaExpuesta.getPortada()) {
      logger.warn("Se está eliminando la pieza portada de la edición {}",
        piezaExpuesta. getEdicion().getIdEdicion());
    }

    String tituloObra = piezaExpuesta. getObra().getTitulo();
    piezaExpuestaDao.delete(piezaExpuesta);

    logger.info("Pieza expuesta {} ('{}') eliminada. La obra permanece en el catálogo",
      idPiezaExpuesta, tituloObra);
  }

  /**
   * Marcar/desmarcar pieza como portada
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  @Transactional(readOnly = false)
  public void setPortada(Long idPiezaExpuesta) throws NotFoundException, InvalidPermissionException {

    PiezaExpuesta piezaExpuesta = piezaExpuestaDao.findById(idPiezaExpuesta);
    if (piezaExpuesta == null) {
      throw new NotFoundException(idPiezaExpuesta.toString(), PiezaExpuesta.class);
    }

    verificarPermisoSobreExposicion(
      piezaExpuesta.getEdicion().getExposicion().getIdExposicion(),
      "gestionar portadas"
    );

    // Desmarcar portada anterior
    PiezaExpuesta portadaAnterior = piezaExpuestaDao.findPortadaByEdicion(
      piezaExpuesta.getEdicion().getIdEdicion()
    );
    if (portadaAnterior != null) {
      portadaAnterior.setPortada(false);
      piezaExpuestaDao.update(portadaAnterior);
    }

    // Marcar nueva portada
    piezaExpuesta.setPortada(true);
    piezaExpuestaDao.update(piezaExpuesta);

    logger.info("Pieza expuesta {} marcada como portada de la edición {}",
      idPiezaExpuesta, piezaExpuesta.getEdicion().getIdEdicion());
  }





  // ==================== MÉTODOS AUXILIARES ====================

  private User getCurrentUser() {
    String login = SecurityUtils.getCurrentUserLogin();
    return userDao.findByLogin(login);
  }

  private TipoPermiso getPermisoSobreExposicion(Long idExposicion) {
    User currentUser = getCurrentUser();

    if (currentUser.getAutoridad() == UserAuthority.ADMIN) {
      return TipoPermiso.CREADOR;
    }

    Gestion gestion = gestionDao.findByUserAndExpo(currentUser.getIdUser(), idExposicion);

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
