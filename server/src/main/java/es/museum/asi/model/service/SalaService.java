package es.museum.asi.model.service;

import es.museum.asi.model.domain.Sala;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.dto.SalaDTO;
import es.museum.asi.repository.OrdenSalaSesionDao;
import es.museum.asi.repository.PiezaExpuestaDao;
import es.museum.asi.repository.SalaDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class SalaService {

  @Autowired
  private SalaDao  salaDao;

  @Autowired
  private OrdenSalaSesionDao ordenSalaSesionDao;

  @Autowired
  private PiezaExpuestaDao  piezaExpuestaDao;

  /**
   * HU38 - Listar salas (Solo ADMIN)
   * Ordenadas por planta y nombre
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public Collection<SalaDTO> findAll() {
    return salaDao.findAll().stream()
      .map(SalaDTO::new)
      . collect(Collectors.toList());
  }

  /**
   * Obtener una sala por ID
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public SalaDTO findById(Long idSala) throws NotFoundException {
    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala. toString(), Sala.class);
    }
    return new SalaDTO(sala);
  }

  /**
   * Buscar salas por planta
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public Collection<SalaDTO> findByPlanta(Integer planta) {
    return salaDao.findByPlanta(planta).stream()
      .map(SalaDTO::new)
      .collect(Collectors.toList());
  }

  /**
   * HU37 - Crear sala (Solo ADMIN)
   * Validación: nombre único
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public SalaDTO create(String nombre, Integer planta) throws OperationNotAllowed {
    // Validar nombre único
    Sala existente = salaDao.findByNombre(nombre);
    if (existente != null) {
      throw new OperationNotAllowed("Ya existe una sala con el nombre: " + nombre);
    }

    // Crear sala
    Sala sala = new Sala(nombre, planta);
    salaDao.create(sala);

    return new SalaDTO(sala);
  }

  /**
   * HU39 - Editar sala (Solo ADMIN)
   * Permite modificar nombre y planta
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public SalaDTO update(Long idSala, String nombre, Integer planta)
    throws NotFoundException, OperationNotAllowed {
    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala.toString(), Sala.class);
    }

    // Validar nombre único si se cambia
    if (nombre != null && !nombre.equals(sala.getNombre())) {
      Sala existente = salaDao.findByNombre(nombre);
      if (existente != null) {
        throw new OperationNotAllowed("Ya existe una sala con el nombre: " + nombre);
      }
      sala.setNombre(nombre);
    }

    // Actualizar planta
    if (planta != null) {
      sala.setPlanta(planta);
    }

    salaDao.update(sala);
    return new SalaDTO(sala);
  }

  /**
   * HU40 - Eliminar sala (Solo ADMIN)
   * Validaciones:
   * - No se puede eliminar si tiene sesiones programadas (a través de OrdenSalaSesion)
   * - No se puede eliminar si tiene piezas expuestas
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = false)
  public void deleteById(Long idSala) throws NotFoundException, OperationNotAllowed {
    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala.toString(), Sala.class);
    }

    // Validación 1: No eliminar si tiene sesiones programadas
    if (!ordenSalaSesionDao.findBySala(idSala).isEmpty()) {
      throw new OperationNotAllowed(
        "No se puede eliminar la sala '" + sala.getNombre() +
          "' porque tiene sesiones programadas.  Debe desasignarla de las sesiones primero."
      );
    }

    // Validación 2: No eliminar si tiene piezas expuestas
    if (!piezaExpuestaDao.findBySala(idSala).isEmpty()) {
      throw new OperationNotAllowed(
        "No se puede eliminar la sala '" + sala.getNombre() +
          "' porque tiene piezas expuestas. Debe reasignar las piezas a otra sala primero."
      );
    }

    String nombreSala = sala.getNombre();
    salaDao.delete(sala);
  }

  /**
   * Verificar si una sala está disponible (sin sesiones ni piezas)
   * Útil para el frontend
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public boolean isDisponibleParaEliminar(Long idSala) throws NotFoundException {
    Sala sala = salaDao.findById(idSala);
    if (sala == null) {
      throw new NotFoundException(idSala.toString(), Sala.class);
    }

    return ordenSalaSesionDao.findBySala(idSala).isEmpty() &&
      piezaExpuestaDao.findBySala(idSala).isEmpty();
  }
}
