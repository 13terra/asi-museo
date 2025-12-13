package es.museum.asi.model.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * DTO para solicitud de reserva de entradas (HU51)
 */
public class ReservarEntradasDTO {
  @NotNull
  private Long idSesion;

  // Datos del comprador
  @NotBlank
  private String nombrePila;

  @NotBlank
  private String apellido1;

  private String apellido2;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String telefono;

  @NotBlank
  private String pais;

  //Entrada por tipo
  @NotNull
  @Size(min = 1)
  private Map<Long, Integer> entradasPorTipo;

  //Datos asistentes
  @NotNull
  @Size(min = 1)
  private List<DatosAsistente> datosAsistentes;

  public Long getIdSesion() {
    return idSesion;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public String getNombrePila() {
    return nombrePila;
  }

  public void setNombrePila(String nombrePila) {
    this.nombrePila = nombrePila;
  }

  public String getApellido1() {
    return apellido1;
  }

  public void setApellido1(String apellido1) {
    this.apellido1 = apellido1;
  }

  public String getApellido2() {
    return apellido2;
  }

  public void setApellido2(String apellido2) {
    this.apellido2 = apellido2;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
  }

  public Map<Long, Integer> getEntradasPorTipo() {
    return entradasPorTipo;
  }

  public void setEntradasPorTipo(Map<Long, Integer> entradasPorTipo) {
    this.entradasPorTipo = entradasPorTipo;
  }

  public List<DatosAsistente> getDatosAsistentes() {
    return datosAsistentes;
  }

  public void setDatosAsistentes(List<DatosAsistente> datosAsistentes) {
    this.datosAsistentes = datosAsistentes;
  }


  // Clase interna para datos de asistentes
  public static class DatosAsistente {
    @NotBlank
    private String nombrePila;

    @NotBlank
    private String apellido1;

    private String apellido2;

    @NotBlank
    private String dni;


    public String getNombrePila() {
      return nombrePila;
    }

    public void setNombrePila(String nombrePila) {
      this.nombrePila = nombrePila;
    }

    public String getApellido1() {
      return apellido1;
    }

    public void setApellido1(String apellido1) {
      this.apellido1 = apellido1;
    }

    public String getApellido2() {
      return apellido2;
    }

    public void setApellido2(String apellido2) {
      this.apellido2 = apellido2;
    }

    public String getDni() {
      return dni;
    }

    public void setDni(String dni) {
      this.dni = dni;
    }
  }

}
