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
import java.util.List;
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
