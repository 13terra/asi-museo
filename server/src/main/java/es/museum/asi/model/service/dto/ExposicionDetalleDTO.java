package es.museum.asi.model.service.dto;


import es.museum.asi.model.domain.Exposicion;
import es.museum.asi.model.enums.EstadoExpo;
import es.museum.asi.model.enums.TipoPermiso;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO detallado de exposición (HU13)
 */

public class ExposicionDetalleDTO {
  private Long idExposicion;
  private String titulo;
  private String descripcion;
  private EstadoExpo estadoExpo;

  //Ediciones asociadas (resumen)
  private List<ResumenEdicionDTO> ediciones;

  //Info de gestión (sólo admin/gestor)
  private GestorPermisoDTO creador;
  private List<GestorPermisoDTO> gestores;

  public ExposicionDetalleDTO() {}

  public ExposicionDetalleDTO(Exposicion exposicion, boolean incluirInfoGestion) {
    this.idExposicion = exposicion.getIdExposicion();
    this.titulo = exposicion.getTitulo();
    this.descripcion = exposicion.getDescripcion();
    this.estadoExpo = exposicion.getEstadoExpo();
    // Mapeamos ediciones a DTOs resumidos
    this.ediciones = exposicion.getEdiciones().stream()
      .map(ResumenEdicionDTO::new).collect(Collectors.toList());

    if(incluirInfoGestion){
      // Identificar Creador
      this.creador = exposicion.getGestiones().stream()
        .filter(g -> g.getPermiso() == TipoPermiso.CREADOR)
        .findFirst()
        .map(GestorPermisoDTO::new)
        .orElse(null);
      //Listar todos gestores
      this.gestores = exposicion.getGestiones().stream()
        .map(GestorPermisoDTO::new)
        .collect(Collectors.toList());
    }
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

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public EstadoExpo getEstadoExpo() {
    return estadoExpo;
  }

  public void setEstadoExpo(EstadoExpo estadoExpo) {
    this.estadoExpo = estadoExpo;
  }

  public List<ResumenEdicionDTO> getEdiciones() {
    return ediciones;
  }

  public void setEdiciones(List<ResumenEdicionDTO> ediciones) {
    this.ediciones = ediciones;
  }

  public GestorPermisoDTO getCreador() {
    return creador;
  }

  public void setCreador(GestorPermisoDTO creador) {
    this.creador = creador;
  }

  public List<GestorPermisoDTO> getGestores() {
    return gestores;
  }

  public void setGestores(List<GestorPermisoDTO> gestores) {
    this.gestores = gestores;
  }
}
