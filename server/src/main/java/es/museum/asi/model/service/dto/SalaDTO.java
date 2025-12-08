package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Sala;
import jakarta.validation.constraints.NotEmpty;

public class SalaDTO {

  private Long idSala;
  @NotEmpty
  private String nombre;
  @NotEmpty
  private Integer planta;

  private int numPiezaExpuestas;
  private int numSesionesAsignadas;


  public SalaDTO() {
  }

  public SalaDTO(Sala sala) {
    this.idSala = sala.getIdSala();
    this.nombre = sala.getNombre();
    this.planta = sala.getPlanta();
    this.numPiezaExpuestas = sala.getPiezasExpuestas() != null ? sala.getPiezasExpuestas().size() : 0 ;
    this.numSesionesAsignadas = sala.getOrdenes() != null ? sala.getOrdenes().size() : 0 ;
  }


  public Long getIdSala() {
    return idSala;
  }

  public void setIdSala(Long idSala) {
    this.idSala = idSala;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Integer getPlanta() {
    return planta;
  }

  public void setPlanta(Integer planta) {
    this.planta = planta;
  }

  public int getNumPiezaExpuestas() {
    return numPiezaExpuestas;
  }

  public void setNumPiezaExpuestas(int numPiezaExpuestas) {
    this.numPiezaExpuestas = numPiezaExpuestas;
  }

  public int getNumSesionesAsignadas() {
    return numSesionesAsignadas;
  }

  public void setNumSesionesAsignadas(int numSesionesAsignadas) {
    this.numSesionesAsignadas = numSesionesAsignadas;
  }
}
