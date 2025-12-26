package es.museum.asi.web.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import es.museum.asi.model.exception.UserLoginExistsException;
import es.museum.asi.model.service.UserService;
import es.museum.asi.model.service.dto.UserDTOPrivate;
import es.museum.asi.model.service.dto.UserDTOPublic;
import es.museum.asi.security.JWTToken;
import es.museum.asi.security.TokenProvider;
import es.museum.asi.web.util.dto.RegisterRequest;
import es.museum.asi.web.util.dto.LoginDTO;
import es.museum.asi.web.util.ErrorDTO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

/**
 * Resource de autenticación (HU1-HU3)
 * Basado en el estilo del notebook del profesor.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthResource {

  private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private TokenProvider tokenProvider;

  /**
   * HU1 - Registro de usuario VISITANTE.
   * POST /api/auth/register
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    logger.info("Intento de registro: login={} ", request.getLogin());

    // Validación de contraseñas
    if (request.getPassword() == null || request.getPasswordConfirm() == null) {
      return ResponseEntity.badRequest().body(new ErrorDTO("La contraseña y su confirmación son obligatorias"));
    }
    if (!request.getPassword().equals(request.getPasswordConfirm())) {
      return ResponseEntity.badRequest().body(new ErrorDTO("Las contraseñas no coinciden"));
    }

    try {
      UserDTOPrivate created = userService.registerUser(request.getLogin(), request.getPassword());
      UserDTOPublic response = new UserDTOPublic();
      response.setId(created.getId());
      response.setLogin(created.getLogin());
      response.setAutoridad(created.getAuthority());
      response.setEstado(created.getEstadoUser());
      response.setPermisoGestor(created.getPermisoGestor());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (UserLoginExistsException e) {
      logger.warn("Login ya existe: {}", request.getLogin());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("El login ya existe"));
    } catch (Exception e) {
      logger.error("Error inesperado en registro", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("Error en el registro"));
    }
  }

  /**
   * HU2 - Login (gestionado por Spring Security). Endpoint informativo.
   */
/**
   * HU2 - Login (gestionado por Spring Security).
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
    try {
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDTO.getLogin(), loginDTO.getPassword());

      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = tokenProvider.createToken(authentication);
      return ResponseEntity.ok(new JWTToken(jwt));

    } catch (DisabledException e) {
      // CASO 1: El usuario existe y la contraseña está bien, pero está INACTIVO
      logger.warn("Intento de login de usuario desactivado: {}", loginDTO.getLogin());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorDTO("Tu cuenta ha sido desactivada. Contacta con el administrador."));

    } catch (BadCredentialsException e) {
      //  CASO 2: Usuario no existe o contraseña mal
      logger.warn("Credenciales incorrectas para: {}", loginDTO.getLogin());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorDTO("Credenciales incorrectas"));

    } catch (Exception e) {
      // OTROS ERRORES
      logger.error("Error inesperado en login", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorDTO("Error en el login"));
    }
  }

  /**
   * HU3 - Logout
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout() {
    logger.info("Cierre de sesión solicitado");
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok("Sesión cerrada");
  }

  /**
   * Obtener el usuario autenticado.
   */
  @GetMapping("/me")
  public ResponseEntity<?> me() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO("No autenticado"));
    }

    UserDTOPrivate current = userService.getCurrentUserWithAuthority();
    if (current == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO("No autenticado"));
    }

    UserDTOPublic response = new UserDTOPublic();
    response.setId(current.getId());
    response.setLogin(current.getLogin());
    response.setAutoridad(current.getAuthority());
    response.setEstado(current.getEstadoUser());
    response.setPermisoGestor(current.getPermisoGestor());
    return ResponseEntity.ok(response);
  }
}
