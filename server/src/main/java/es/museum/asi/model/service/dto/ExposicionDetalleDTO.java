package es.museum.asi.model.service.dto;


import es.museum.asi.model.enums.EstadoExpo;

import java.util.List;

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

}
