package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Edicion;
import es.museum.asi.model.enums.EstadoEdicion;
import es.museum.asi.model.service.dto.ResumenPiezaExpuestaDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO detallado para HU21
 */

public class EdicionDetalleDTO {
  private Long idEdicion;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private EstadoEdicion estadoEdicion;

  //Info expo padre
  private Long idExpo;
  private String tituloExpo;

  //Resumen ejecutivo sólo para admin/gestor
  private Integer numPiezas;
  private Integer numSesiones;
  private Integer numSalasAsignadas;
  private Integer numReservas;
  private Integer numEntradas;

  //Listados resumidos sólo para admin/gestor
  private List<ResumenSesionDTO> sesiones;
  private List<ResumenPiezaExpuestaDTO> piezasExpuestas;

  public EdicionDetalleDTO() {}

  public EdicionDetalleDTO(Edicion edicion, boolean incluirInfoGestion) {
    this.idEdicion = edicion.getIdEdicion();
    this.fechaInicio = edicion.getFechaInicio();
    this.fechaFin = edicion.getFechaFin();
    this.estadoEdicion = edicion.getEstado();
    this.idExpo = edicion.getExposicion().getIdExposicion();
    this.tituloExpo = edicion.getExposicion().getTitulo();

    if (incluirInfoGestion) {
      this.numPiezas = edicion.getPiezasExpuestas() != null ? edicion.getPiezasExpuestas().size() : 0;
      this.numSesiones = edicion.getSesiones() != null ? edicion.getSesiones().size() : 0;

      //ojo aquí -> contar salas únicas a través de sesiones
      this.numSalasAsignadas = (int) edicion.getSesiones().stream()
        .flatMap(sesion -> sesion.getOrdenes().stream())
        .map(orden -> orden.getSala().getIdSala())
        .distinct()
        .count();

      this.numReservas = edicion.getSesiones().stream()
        .mapToInt(sesion -> sesion.getReservas() != null ?
          sesion.getReservas().size() : 0)
        .sum();

      this.numEntradas = edicion.getSesiones().stream()
        .flatMap(sesion -> sesion.getReservas().stream())
        .mapToInt(reserva -> reserva.getEntradas() != null ?
          reserva.getEntradas().size() : 0)
        .sum();

      //Mapeamos listas resumidas
      this.sesiones = edicion.getSesiones().stream()
        .map(ResumenSesionDTO::new)
        .collect(Collectors.toList());
    }
    this.piezasExpuestas = edicion.getPiezasExpuestas().stream()
      .map(ResumenPiezaExpuestaDTO::new)
      .collect(Collectors.toList());
    }
}






