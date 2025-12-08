package es.museum.asi.model.service;


import es.museum.asi.model.domain.User;
import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.exception.UserLoginExistsException;
import es.museum.asi.model.service.dto.UserDTOCompleto;
import es.museum.asi.model.service.dto.UserDTOPrivate;
import es.museum.asi.model.service.dto.UserDTOPublic;
import es.museum.asi.repository.EntradaDao;
import es.museum.asi.repository.GestionDao;
import es.museum.asi.repository.ReservaDao;
import es.museum.asi.repository.UserDao;
import es.museum.asi.security.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class UserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private ReservaDao reservaDao;

  @Autowired
  private GestionDao gestionDao;

  @Autowired
  private EntradaDao entradaDao;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * HU 4 - Listado de usuarios (sólo para ADMIN)
   * Filtra por rol o estado según parámetros
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<UserDTOPublic> findAll() {
    return userDao.findAll().stream().map(UserDTOPublic::new).collect(Collectors.toList());
  }

  /**
   * HU 4 - Listado de usuarios por autoridad
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<UserDTOPublic> findByAutoridad(UserAuthority autoridad) {
    return userDao.findAll().stream().filter(user -> user.getAutoridad().equals(autoridad)).map(UserDTOPublic::new).collect(Collectors.toList());
  }

  /**
   * HU 4 - Listado de usuarios por estado
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<UserDTOPublic> findByEstado(EstadoUser estado) {
    return userDao.findAll().stream().filter(user -> user.getEstado().equals(estado)).map(UserDTOPublic::new).collect(Collectors.toList());
  }

  /**
   * Obtener usuario por ID con toda su información
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public UserDTOCompleto findOne(Long idUser) throws NotFoundException {
    User user = userDao.findById(idUser);
    if(user == null) {
      throw new NotFoundException(idUser.toString(), User.class);
    }
    return new UserDTOCompleto(user);
  }

  /**
   * HU1 - Registro de visitante (VISITANTE por defecto)
   * Accesible sin autenticación
   */
  @Transactional(readOnly = false)
  public UserDTOPrivate registerUser(String login, String contrasena) throws UserLoginExistsException {
    return registerUser(login, contrasena, UserAuthority.VISITANTE);
  }

  /**
   * HU5 - Crear usuarios con autoridad específica, sólo ADMIN puede hacerlo
   * (ADMIN, GESTOR o VISITANTE)
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public UserDTOPrivate createUser(String login, String contrasena, UserAuthority autoridad) throws UserLoginExistsException {
    return registerUser(login, contrasena, autoridad);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public UserDTOPrivate registerUser(String login, String contrasena, UserAuthority autoridad)
    throws UserLoginExistsException  {
    if(userDao.findByLogin(login) != null) {
      throw new UserLoginExistsException(login);
    }

    User user = new User();
    String encryptedPassword = passwordEncoder.encode(contrasena);

    user.setLogin(login);
    user.setContrasena(encryptedPassword);
    user.setAutoridad(autoridad);
    user.setEstado(EstadoUser.ACTIVO);

    userDao.create(user);
    return new UserDTOPrivate(user);
  }

  /**
   * HU6 - Editar usuario (solo ADMIN)
   * Permite modificar login, contrasena, autoridad y estado
   * */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public UserDTOPublic updateUser(Long idUser,String login, String contrasena, UserAuthority autoridad, EstadoUser estado)
    throws NotFoundException, OperationNotAllowed {
    User user = userDao.findById(idUser);
    if(user == null) {
      throw new NotFoundException(idUser.toString(), User.class);
    }

    // Validar restricción: no cambiar GESTOR/ADMIN a VISITANTE si tiene exposiciones
    if ((user.getAutoridad() == UserAuthority.GESTOR || user.getAutoridad() == UserAuthority.ADMIN)
    && autoridad == UserAuthority.VISITANTE) {
      if(!gestionDao.findByUser(idUser).isEmpty()) {
        throw new OperationNotAllowed("No se puede cambiar la autoridad de un GESTOR o ADMIN a VISITANTE si tiene exposiciones asignadas.");
      }
    }

    // Actualización de campos
    if (login != null && !login.equals(user.getLogin())) {
      // Validamos que el nuevo login no existe
      if (userDao.findByLogin(login) != null) {
        throw new OperationNotAllowed("El login " + login + " ya existe en el sistema.");
      }
      user.setLogin(login);
    }

    if(contrasena != null && !contrasena.isEmpty()) {
      user.setContrasena(passwordEncoder.encode(contrasena));
    }

    if(autoridad != null) {
      user.setAutoridad(autoridad);
    }

    if(estado != null) {
      user.setEstado(estado);
    }
    userDao.update(user);
    return new UserDTOPublic(user);
  }

  /**
   * HU8 - Desactivar usuario (sólo ADMIN)
   * Suspensión temporal del acceso
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public UserDTOPublic deactivateUser(Long idUser)
    throws NotFoundException, OperationNotAllowed {
    return updateUserEstado(idUser, EstadoUser.INACTIVO);
  }

  /**
   * HU8 - Activar usuario (sólo ADMIN)
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public UserDTOPublic activateUser(Long idUser)
    throws NotFoundException, OperationNotAllowed {
    return updateUserEstado(idUser, EstadoUser.ACTIVO);
  }

  /**
   * Método privado para cambiar estado usuario
   */

  private UserDTOPublic updateUserEstado(Long idUser, EstadoUser estado)
    throws NotFoundException, OperationNotAllowed {
    User user = userDao.findById(idUser);
    if(user == null) {
      throw new NotFoundException(idUser.toString(), User.class);
    }
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser.getId().equals(user.getIdUser())) {
      throw new OperationNotAllowed("El usuario no puede activar/desactivar su propia cuenta.");
    }

    user.setEstado(estado);
    userDao.update(user);
    return new UserDTOPublic(user);
  }

  /**
   * HU7 - Eliminar usuario (sólo ADMIN)
   * Validar que no sea el admin semilla ni el último admin activ
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public void deleteById(Long idUser) throws NotFoundException, OperationNotAllowed {
    User theUser =  userDao.findById(idUser);
    if(theUser == null) {
      throw new NotFoundException(idUser.toString(), User.class);
    }

    // No eliminar su propia cuenta
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser.getId().equals(theUser.getIdUser())) {
      throw new OperationNotAllowed("El usuario no puede eliminarse a sí mismo.");
    }

    // No eliminar admin semilla
    if ("admin".equals(theUser.getLogin())) {
      throw new OperationNotAllowed("No se puede eliminar al admin semilla");
    }

    // Debe existir al menos 1 admin activo
    if (theUser.getAutoridad() == UserAuthority.ADMIN) {
      long activeAdmins = userDao.findAll().stream().filter(u -> u.getAutoridad() == UserAuthority.ADMIN
      && u.getEstado() == EstadoUser.ACTIVO).count(); //long porque .count devuelve un long

      if (activeAdmins <= 1) {
        throw new OperationNotAllowed("Debe existir al menos un administrador activo en el sistema");
      }
    }

    //Eliminar gestiones asociadas
    gestionDao.findByUser(idUser).forEach(gestion -> gestionDao.delete(gestion));

    //Eliminar reservas y entradas asociadas
    reservaDao.findByUser(idUser).forEach(reserva -> {
      entradaDao.findByReserva(reserva.getIdReserva()).forEach(entrada -> {
        entradaDao.delete(entrada);
      });
      reservaDao.delete(reserva);
    });

    userDao.delete(theUser);
  }

  // Obtener usuario actual autenticado con autoridad
  public UserDTOPrivate getCurrentUserWithAuthority() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();
    if (currentUserLogin != null) {
      User user = userDao.findByLogin(currentUserLogin);
      if(user != null) {
        return new UserDTOPrivate(user);
      }
    }
    return null;
  }


  // Buscar usuario por login
  public User findByLogin(String login) {
    return userDao.findByLogin(login);
  }
}
