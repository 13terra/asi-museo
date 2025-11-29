package es.udc.asi.museo_rest.model;

import java.util.ArrayList;
import java.util.List;

import es.udc.asi.museo_rest.model.enums.UserAuthority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "theUser")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
  @SequenceGenerator(name = "user_generator", sequenceName = "user_seq")
  private Long id;

  @Column(unique = true)
  private String login;

  private String password;

  @Enumerated(EnumType.STRING)
  private UserAuthority authority;  // El tipo de autoridad es una de los atributos del usuario

  @OneToMany(mappedBy = "owner")
  private List<Note> notes = new ArrayList<>();

  private boolean active = true;

  public User() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserAuthority getAuthority() {
    return authority;
  }

  public void setAuthority(UserAuthority authority) {
    this.authority = authority;
  }

  public List<Note> getNotes() {
    return notes;
  }

  public void setNotes(List<Note> notes) {
    this.notes = notes;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
