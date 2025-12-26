package es.museum.asi.config;

import es.museum.asi.model.domain.TipoEntrada;
import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.repository.TipoEntradaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import es.museum.asi.model.exception.UserLoginExistsException;
import es.museum.asi.model.service.UserService;

import java.util.Collections;

@Configuration
public class DatabaseLoader {

  private final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

  @Autowired
  private TipoEntradaDao tipoEntradaDao;

  @Autowired
  private UserService userService;


  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void loadData() throws UserLoginExistsException, InterruptedException {

    /**
     * HACER LO MISMO PERO INICIALIZANDO LOS TIPOS DE ENTRADAS QUE NOS DE LA GANA Y EL ADMIN SEMILLA
     */

    logger.info("Iniciando carga datos semilla...");

    // Fijamos autenticación ADMIN en el contexto para pasar las @PreAuthorize
    Authentication seedAuth = new UsernamePasswordAuthenticationToken(
      "seed-loader",
      "N/A",
      Collections.singletonList(new SimpleGrantedAuthority(UserAuthority.ADMIN.name()))
    );

    SecurityContextHolder.getContext().setAuthentication(seedAuth);

    try {
      // ADMIN SEMILLA
      userService.createUser("admin", "admin", UserAuthority.ADMIN, null);
      logger.info("Admin semilla creado");

      // ADMIN ADICIONAL
      userService.createUser("pepemin", "pepemin",  UserAuthority.ADMIN, null);
      logger.info("Admin adicional creado");

      // Gestores de expos
      userService.createUser("gestor1", "gestor1", UserAuthority.GESTOR, TipoPermiso.CREADOR);
      userService.createUser("gestor2", "gestor2", UserAuthority.GESTOR, TipoPermiso.EDITOR);
      logger.info("Gestores creados");

      // Visitantes de prueba
      userService.registerUser("josebu", "josebu");
      userService.registerUser("terra", "terra");
      logger.info("Visitantes creados");


      // TIPOS DE ENTRADAS
      logger.info("Creando tipos de entrada...");

      TipoEntrada general = new TipoEntrada();
      general.setNombre("General");
      general.setPrecio(15.0f);
      general.setDescripcion("Entrada general para adultos");
      tipoEntradaDao.create(general);

      TipoEntrada reducida = new TipoEntrada();
      reducida.setNombre("Reducida");
      reducida.setPrecio(7.5f);
      reducida. setDescripcion("Entrada reducida para mayores de 65 años y grupos");
      tipoEntradaDao.create(reducida);

      TipoEntrada estudiante = new TipoEntrada();
      estudiante.setNombre("Estudiante");
      estudiante.setPrecio(3.75f);
      estudiante.setDescripcion("Entrada para estudiantes con carnet universitario");
      tipoEntradaDao.create(estudiante);

      logger.info("Tipos de entrada creados");
      logger.info("Datos semilla cargados correctamente");
    } finally {
      // Limpiamos contexto para no contaminar otras operaciones
      SecurityContextHolder.clearContext();
    }
  }

}
