package es.museum.asi.model.service;


import es.museum.asi.model.domain.Entrada;
import es.museum.asi.model.domain.User;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.EntradaDTO;
import es.museum.asi.model.service.dto.EntradaDetalleDTO;
import es.museum.asi.repository.EntradaDao;
import es.museum.asi.repository.ReservaDao;
import es.museum.asi.repository.UserDao;
import es.museum.asi.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Servicio para consulta de entradas.
 * Implementa HU55-HU56
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class EntradaService {

  @Autowired
  private EntradaDao entradaDao;

  @Autowired
  private ReservaDao reservaDao;

  @Autowired
  private UserDao userDao;

  /**
   * HU55 - Listar mis entradas
   * Solo VISITANTE, muestra todas las entradas de todas sus reservas
   */
  @PreAuthorize("hasAuthority('VISITANTE')")
  public Collection<EntradaDTO> findMisEntradas() {
    User currentUser = getCurrentUser();

    // Obtener todas las reservas del usuario
    return reservaDao.findByUser(currentUser.getIdUser()).stream()
      .flatMap(reserva -> reserva.getEntradas().stream())
      .sorted((e1, e2) -> {
        // Ordenar por fecha de sesión (más recientes primero)
        return e2.getReserva().getSesion().getHoraInicio()
          .compareTo(e1.getReserva().getSesion().getHoraInicio());
      })
      .map(EntradaDTO::new)
      .collect(Collectors. toList());
  }

  /**
   * HU56 - Ver detalle de entrada
   * Solo el propietario puede ver su entrada
   */
  @PreAuthorize("hasAuthority('VISITANTE')")
  public EntradaDetalleDTO findMiEntradaDetalle(Long idEntrada)
    throws NotFoundException, OperationNotAllowed {

    Entrada entrada = entradaDao.findById(idEntrada);
    if (entrada == null) {
      throw new NotFoundException(idEntrada.toString(), Entrada.class);
    }

    // Verificar que la entrada pertenece al usuario actual
    User currentUser = getCurrentUser();
    if (! entrada.getReserva().getUser().getIdUser().equals(currentUser.getIdUser())) {
      throw new OperationNotAllowed("No tiene permisos para ver esta entrada");
    }

    return new EntradaDetalleDTO(entrada);
  }

  // ========= MÉTODOS AUXILIARES =========

  private User getCurrentUser() {
    String login = SecurityUtils.getCurrentUserLogin();
    return userDao.findByLogin(login);
  }

}
