package es.museum.asi.web.resources;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.exception.UserLoginExistsException;
import es.museum.asi.model.service.UserService;
import es.museum.asi.model.service.dto.UserDTOPrivate;
import es.museum.asi.model.service.dto.UserDTOPublic;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import es.museum.asi.web.util.ErrorDTO;
import es.museum.asi.web.util.dto.CreateUserRequest;
import es.museum.asi.web.util.dto.UpdateUserRequest;

/**
 * Resource de gestión de usuarios (HU4-HU8).
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('ADMIN')")
@CrossOrigin(origins = "*")
public class UserResource {

  private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

  @Autowired
  private UserService userService;

  /**
   * HU4 - Listar usuarios con filtros opcionales.
   */
  @GetMapping
  public ResponseEntity<Collection<UserDTOPublic>> listUsers(
      @RequestParam(required = false) UserAuthority autoridad,
      @RequestParam(required = false) EstadoUser estado) {

    logger.info("Listando usuarios (autoridad={}, estado={})", autoridad, estado);

    if (autoridad != null) {
      return ResponseEntity.ok(userService.findByAutoridad(autoridad));
    }
    if (estado != null) {
      return ResponseEntity.ok(userService.findByEstado(estado));
    }
    return ResponseEntity.ok(userService.findAll());
  }

  /**
   * HU5 - Crear usuario (ADMIN).
   */
  @PostMapping
  public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request)
      throws UserLoginExistsException {

    logger.info("Creando usuario: login={}, autoridad={}", request.getLogin(), request.getAutoridad());

    try {
      UserDTOPrivate created = userService.createUser(request.getLogin(), request.getPassword(), request.getAutoridad(), request.getPermisoGestor());
      return ResponseEntity.status(HttpStatus.CREATED).body(toPublic(created));
    } catch (UserLoginExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("El login ya existe"));
    }
  }

  /**
   * HU6 - Editar usuario (login/autoridad/estado/contraseña).
   */
  @PutMapping("/{idUser}")
  public ResponseEntity<?> updateUser(
      @PathVariable Long idUser,
      @RequestBody UpdateUserRequest request,
      Errors errors) throws RequestBodyNotValidException {

    if (errors != null && errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    try {
      UserDTOPublic updated = userService.updateUser(
        idUser,
        request.getLogin(),
        request.getPassword(),
        request.getAutoridad(),
        request.getEstado(),
        request.getPermisoGestor()
      );
      return ResponseEntity.ok(updated);
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Usuario no encontrado"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  /**
   * HU8 - Activar usuario.
   */
  @PutMapping("/{idUser}/activate")
  public ResponseEntity<?> activate(@PathVariable Long idUser) {
    try {
      return ResponseEntity.ok(userService.activateUser(idUser));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Usuario no encontrado"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  /**
   * HU8 - Desactivar usuario.
   */
  @PutMapping("/{idUser}/deactivate")
  public ResponseEntity<?> deactivate(@PathVariable Long idUser) {
    try {
      return ResponseEntity.ok(userService.deactivateUser(idUser));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Usuario no encontrado"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  /**
   * HU7 - Eliminar usuario.
   */
  @DeleteMapping("/{idUser}")
  public ResponseEntity<?> delete(@PathVariable Long idUser) {
    try {
      userService.deleteById(idUser);
      return ResponseEntity.noContent().build();
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Usuario no encontrado"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  private UserDTOPublic toPublic(UserDTOPrivate dto) {
    UserDTOPublic pu = new UserDTOPublic();
    pu.setId(dto.getId());
    pu.setLogin(dto.getLogin());
    pu.setAutoridad(dto.getAuthority());
    pu.setEstado(dto.getEstadoUser());
    pu.setPermisoGestor(dto.getPermisoGestor());
    return pu;
  }
}
