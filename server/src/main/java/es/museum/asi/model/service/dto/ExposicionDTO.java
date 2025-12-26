package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Exposicion;
import es.museum.asi.model.domain.Edicion;
import es.museum.asi.model.enums.EstadoEdicion;
import es.museum.asi.model.enums.EstadoExpo;
import es.museum.asi.model.enums.TipoPermiso;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para listados de exposiciones
 */

public class ExposicionDTO {
  private Long idExposicion;
  @NotEmpty
  private String titulo;

  private String descripcion;

  private String portadaUrl;
  private LocalDate fechaInicioProximaEdicion;

  private EstadoExpo estadoExpo;

  private int numEdiciones;

  // Solo para vistas de gestión (ADMIN/GESTOR)
  private List<GestorPermisoDTO> gestores;
  private TipoPermiso miPermiso; //permiso user actual

  public ExposicionDTO() {
  }

  // Constructor para vista pública
  public ExposicionDTO(Exposicion exposicion, boolean incluirPermisos) {
    this.idExposicion = exposicion.getIdExposicion();
    this.titulo = exposicion.getTitulo();
    this.descripcion = exposicion.getDescripcion();
    this.portadaUrl = exposicion.getPortadaUrl();
    this.estadoExpo = exposicion.getEstadoExpo();
    this.numEdiciones = exposicion.getEdiciones() != null ? exposicion.getEdiciones().size() : 0;

    if (exposicion.getEdiciones() != null) {
      this.fechaInicioProximaEdicion = exposicion.getEdiciones().stream()
        .filter(e -> e.getEstado() == EstadoEdicion.PUBLICADA && (e.getFechaFin().isAfter(LocalDate.now()) || e.getFechaFin().isEqual(LocalDate.now())))
        .map(Edicion::getFechaInicio)
        .min(Comparator.naturalOrder())
        .orElse(null);
    }

    if (incluirPermisos) {
      this.gestores = exposicion.getGestiones().stream()
        .map(GestorPermisoDTO::new).collect(Collectors.toList());
    }
  }

  // Constructor para vista de Gestor (con su permiso)
  public ExposicionDTO(Exposicion exposicion, TipoPermiso miPermiso) {
    this(exposicion, true);
    this.miPermiso = miPermiso;
  }


  public Long getIdExposicion() {
    return idExposicion;
  }

  public void setIdExposicion(Long idExposicion) {
    this.idExposicion = idExposicion;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getPortadaUrl() {
    return portadaUrl;
  }

  public LocalDate getFechaInicioProximaEdicion() {
    return fechaInicioProximaEdicion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public EstadoExpo getEstadoExpo() {
    return estadoExpo;
  }

  public void setEstadoExpo(EstadoExpo estadoExpo) {
    this.estadoExpo = estadoExpo;
  }

  public int getNumEdiciones() {
    return numEdiciones;
  }

  public void setNumEdiciones(int numEdiciones) {
    this.numEdiciones = numEdiciones;
  }

  public List<GestorPermisoDTO> getGestores() {
    return gestores;
  }

  public void setGestores(List<GestorPermisoDTO> gestores) {
    this.gestores = gestores;
  }

  public TipoPermiso getMiPermiso() {
    return miPermiso;
  }

  public void setMiPermiso(TipoPermiso miPermiso) {
    this.miPermiso = miPermiso;
  }
}
