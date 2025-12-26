package es.museum.asi.model.service.dto;


import es.museum.asi.model.domain.Entrada;
import es.museum.asi.model.domain.Sesion;
import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.model.enums.EstadoSesion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HU33 - DTO detalla con estadísticas
 */
public class SesionDetalleDTO {
  private Long idSesion;
  private LocalDateTime horaInicio;
  private LocalDateTime horaFin;
  private int aforo;
  private int aforoOcupado;
  private EstadoSesion estado;

  // Contexto
  private Long idEdicion;
  private String nombreExposicion;

  // Salas asignadas (ordenadas)
  private List<SalaOrdenDTO> salas;

  //Estadísticas --> sólo admin y gestor
  private Integer numReservas;
  private Map<String, Integer> entradasPorTipo;
  private Float porcentajeOcupacion;
  private Float ingresosEstimados;

  //Listado resevas --> sólo admin y gestor
  private List<ResumenReservaDTO> reservas;

  public SesionDetalleDTO() {}

  public SesionDetalleDTO(Sesion sesion, boolean incluirInfoGestion) {
    this.idSesion = sesion.getIdSesion();
    this.horaInicio = sesion.getHoraInicio();
    this.horaFin = sesion.getHoraFin();
    this.aforo = sesion.getAforo();
    this.idEdicion = sesion.getEdicion().getIdEdicion();
    this.nombreExposicion = sesion.getEdicion().getExposicion().getTitulo();
    this.estado = sesion.getEstadoSesion();
    //Aforo ocupado --> siempre visible
    this.aforoOcupado = sesion.getReservas().stream()
      .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
      .mapToInt(r -> r.getEntradas() != null ? r.getEntradas().size() : 0)
      .sum();
    //Salas ordenadas
    this.salas = sesion.getOrdenes().stream()
      .sorted((o1,o2) -> o1.getOrden().compareTo(o2.getOrden()))
      .map(SalaOrdenDTO::new)
      .collect(Collectors.toList())
    ;

    if (incluirInfoGestion) {
      //castear a int sino protesta
      this.numReservas = (int) sesion.getReservas().stream()
        .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
        .count();

      this.porcentajeOcupacion = sesion.getAforo() > 0 ? (float) this.aforoOcupado / sesion.getAforo() * 100 : 0f;

      this.entradasPorTipo = sesion.getReservas().stream()
        .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
        //lo que sale del flatMap(...) es un STREAM de ENTRADAS
        .flatMap(r -> r.getEntradas().stream())
        .collect(Collectors.groupingBy(
          entrada -> entrada.getTipoEntrada().getNombre(),
          Collectors.summingInt(e -> 1)
        ));

      this.ingresosEstimados = sesion.getReservas().stream()
        .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
        .flatMap(r -> r.getEntradas().stream())
        .map(Entrada::getPrecio)
        .reduce(0f, Float::sum);
    }

    this.reservas = sesion.getReservas().stream()
      .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
      .map(ResumenReservaDTO::new)
      .collect(Collectors.toList());
  }


  public Long getIdSesion() {
    return idSesion;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public LocalDateTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalDateTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalDateTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalDateTime horaFin) {
    this.horaFin = horaFin;
  }

  public int getAforo() {
    return aforo;
  }

  public void setAforo(int aforo) {
    this.aforo = aforo;
  }

  public int getAforoOcupado() {
    return aforoOcupado;
  }

  public void setAforoOcupado(int aforoOcupado) {
    this.aforoOcupado = aforoOcupado;
  }

  public EstadoSesion getEstado() {
    return estado;
  }

  public void setEstado(EstadoSesion estado) {
    this.estado = estado;
  }

  public Long getIdEdicion() {
    return idEdicion;
  }

  public void setIdEdicion(Long idEdicion) {
    this.idEdicion = idEdicion;
  }

  public String getNombreExposicion() {
    return nombreExposicion;
  }

  public void setNombreExposicion(String nombreExposicion) {
    this.nombreExposicion = nombreExposicion;
  }

  public List<SalaOrdenDTO> getSalas() {
    return salas;
  }

  public void setSalas(List<SalaOrdenDTO> salas) {
    this.salas = salas;
  }

  public Integer getNumReservas() {
    return numReservas;
  }

  public void setNumReservas(Integer numReservas) {
    this.numReservas = numReservas;
  }

  public Map<String, Integer> getEntradasPorTipo() {
    return entradasPorTipo;
  }

  public void setEntradasPorTipo(Map<String, Integer> entradasPorTipo) {
    this.entradasPorTipo = entradasPorTipo;
  }

  public Float getPorcentajeOcupacion() {
    return porcentajeOcupacion;
  }

  public void setPorcentajeOcupacion(Float porcentajeOcupacion) {
    this.porcentajeOcupacion = porcentajeOcupacion;
  }

  public Float getIngresosEstimados() {
    return ingresosEstimados;
  }

  public void setIngresosEstimados(Float ingresosEstimados) {
    this.ingresosEstimados = ingresosEstimados;
  }

  public List<ResumenReservaDTO> getReservas() {
    return reservas;
  }

  public void setReservas(List<ResumenReservaDTO> reservas) {
    this.reservas = reservas;
  }
}
