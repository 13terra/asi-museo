package es.museum.asi.security;

import java.util.Collections;

import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.repository.UserDao;
import es.museum.asi.web.exceptions.UserNotActiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.museum.asi.model.domain.User;

@Component
public class MyUserDetailsService implements UserDetailsService {
  private final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

  @Autowired
  private UserDao userDAO;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

    User user = userDAO.findByLogin(login);
    // usuario no existe
    if (user == null) {
      logger.warn("Intento de login fallido: usuario '{}' no encontrado", login);
      throw new UsernameNotFoundException("Usuario o contraseña incorrectos");
    }
    // usuario inactivo
    if (user.getEstado() != EstadoUser.ACTIVO) {
      logger.warn("Intento de login con usuario inactivo: '{}'", login);
      throw new UserNotActiveException( "El usuario está inactivo.  Contacte con el administrador.");
    }

    logger.info("Usuario '{}' autenticado correctamente con autoridad {}", login, user.getAutoridad(). name());

    // Crear GrantedAuthority con el enum convertido a String
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getAutoridad().name());

    return new org.springframework.security.core.userdetails.User(
      login,
      user.getContrasena(),
      Collections.singleton(authority));
  }
}
