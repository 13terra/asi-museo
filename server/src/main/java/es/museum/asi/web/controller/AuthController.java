package es.museum.asi.web.controller;

import es.museum.asi.model.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private UserService userService;

  /**
   * HU1 - Registro de usuario
   * POST /api/auth/register
   */
  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
    logger.info("Intento de registro:  login={}", request.getLogin());

    try {
      // Validar que las contraseñas coinciden
      if (!request.getPassword().equals(request.getPasswordConfirm())) {
        throw new OperationNotAllowed("Las contraseñas no coinciden");
      }

      UserDTO user = userService.register(request.getLogin(), request.getPassword());
      logger.info("Usuario registrado correctamente:   {}", user.getLogin());

      return ResponseEntity.status(HttpStatus.CREATED).body(user);

    } catch (OperationNotAllowed e) {
      logger.warn("Error en registro: {}", e.getMessage());
      throw e;
    }
  }

}
