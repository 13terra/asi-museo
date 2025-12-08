package es.museum.asi.security;

import java.util.Collections;

import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.repository.UserDao;
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
    // HAY QUE IMPLEMENTARLO
    User user = userDAO.findByLogin(login);
    if (user == null) {
      throw new UsernameNotFoundException("User " + login + " not found");
    }
    if (user.getEstado() != EstadoUser.ACTIVO) {
      throw new UsernameNotFoundException("User " + login + " not active");
    }

    logger.info("Loaded user {} with authority {}", login, user.getAutoridad().name());

    GrantedAuthority authority = new SimpleGrantedAuthority(user.getAutoridad().name());
    return new org.springframework.security.core.userdetails.User(login, user.getContrasena(),
        Collections.singleton(authority));
  }
}
