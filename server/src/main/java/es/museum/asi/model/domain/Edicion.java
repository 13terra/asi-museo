package es.museum.asi.model.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "edicion")
public class Edicion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edidicon_generator")
  @SequenceGenerator(name = "edicion_generator", sequenceName = "edicion_seq")
  private Long idEdicion;


}
