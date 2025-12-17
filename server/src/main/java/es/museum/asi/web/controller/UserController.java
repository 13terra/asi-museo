package es.museum.asi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.exception.UserLoginExistsException;
import es.museum.asi.model.service.UserService;
import es.museum.asi.model.service.dto.UserDTOCompleto;
import es.museum.asi.model.service.dto.UserDTOPrivate;
import es.museum.asi.model.service.dto.UserDTOPublic;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de usuarios
 * Implementa HU4-HU8
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  /**
   * HU4 - GET /api/users - Listar usuarios (ADMIN)
   * Soporta filtros opcionales por autoridad y estado
   */
  @GetMapping
  public List<UserDTOPublic> findAll(
      @RequestParam(required = false) UserAuthority autoridad,
      @RequestParam(required = false) EstadoUser estado) {

    if (autoridad != null) {
      return userService.findByAutoridad(autoridad);
    } else if (estado != null) {
      return userService.findByEstado(estado);
    } else {
      return userService.findAll();
    }
  }

  /**
   * HU4 - GET /api/users/{id} - Obtener detalle de usuario (ADMIN)
   */
  @GetMapping("/{id}")
  public UserDTOCompleto findOne(@PathVariable Long id) throws NotFoundException {
    return userService.findOne(id);
  }

  /**
   * HU5 - POST /api/users - Crear usuario (ADMIN)
   */
  @PostMapping
  public UserDTOPrivate create(@RequestBody @Valid UserDTOPrivate userDTO, Errors errors)
      throws RequestBodyNotValidException, UserLoginExistsException {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    return userService.createUser(
        userDTO.getLogin(),
        userDTO.getPassword(),
        userDTO.getAuthority()
    );
  }

  /**
   * HU6 - PUT /api/users/{id} - Actualizar usuario (ADMIN)
   */
  @PutMapping("/{id}")
  public UserDTOPublic update(
      @PathVariable Long id,
      @RequestBody @Valid UserDTOPublic userDTO,
      Errors errors)
      throws RequestBodyNotValidException, NotFoundException, OperationNotAllowed {

    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    return userService.updateUser(
        id,
        userDTO.getLogin(),
        null,  // contraseña se actualiza por separado
        userDTO.getAutoridad(),
        userDTO.getEstado()
    );
  }

  /**
   * HU8 - PUT /api/users/{id}/activate - Activar usuario (ADMIN)
   */
  @PutMapping("/{id}/activate")
  public UserDTOPublic activate(@PathVariable Long id)
      throws NotFoundException, OperationNotAllowed {
    return userService.activateUser(id);
  }

  /**
   * HU8 - PUT /api/users/{id}/deactivate - Desactivar usuario (ADMIN)
   */
  @PutMapping("/{id}/deactivate")
  public UserDTOPublic deactivate(@PathVariable Long id)
      throws NotFoundException, OperationNotAllowed {
    return userService.deactivateUser(id);
  }

  /**
   * HU7 - DELETE /api/users/{id} - Eliminar usuario (ADMIN)
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id)
      throws NotFoundException, OperationNotAllowed {
    userService.deleteById(id);
  }
}
