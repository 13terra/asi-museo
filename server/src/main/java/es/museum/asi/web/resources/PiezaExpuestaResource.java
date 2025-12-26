package es.museum.asi.web.resources;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.PiezaExpuestaService;
import es.museum.asi.model.service.dto.PiezaExpuestaDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de piezas expuestas (HU27-HU30).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PiezaExpuestaResource {

  @Autowired
  private PiezaExpuestaService piezaExpuestaService;

  @PostMapping("/ediciones/{idEdicion}/piezas-expuestas")
  public ResponseEntity<?> create(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) Long idObra,
      @RequestParam(required = false) Long idSala,
      @RequestParam(required = false) Integer orden,
      @RequestParam(required = false) String textoCuratorial,
      @RequestParam(required = false) Boolean portada,
      @RequestBody(required = false) PiezaExpuestaCreateRequest body) {
    try {
      Long finalIdObra = coalesce(idObra, body != null ? body.getIdObra() : null);
      Long finalIdSala = coalesce(idSala, body != null ? body.getIdSala() : null);
      Integer finalOrden = coalesce(orden, body != null ? body.getOrden() : null);
      String finalTexto = coalesce(textoCuratorial, body != null ? body.getTextoCuratorial() : null);
      Boolean finalPortada = coalesce(portada, body != null ? body.getPortada() : Boolean.FALSE);

      if (finalIdObra == null || finalIdSala == null) {
        return ResponseEntity.badRequest().body(new ErrorDTO("Debe indicar 'idObra' e 'idSala'."));
      }

      PiezaExpuestaDTO dto = piezaExpuestaService.create(finalIdObra, idEdicion, finalIdSala, finalOrden, finalTexto, finalPortada);
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/ediciones/{idEdicion}/piezas-expuestas")
  public ResponseEntity<?> listByEdicion(@PathVariable Long idEdicion) {
    try {
      Collection<PiezaExpuestaDTO> list = piezaExpuestaService.findByEdicion(idEdicion);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/salas/{idSala}/ediciones/{idEdicion}/piezas-expuestas")
  public ResponseEntity<?> listBySala(@PathVariable Long idSala, @PathVariable Long idEdicion) {
    try {
      Collection<PiezaExpuestaDTO> list = piezaExpuestaService.findBySala(idSala, idEdicion);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/piezas-expuestas/{idPiezaExpuesta}")
  public ResponseEntity<?> update(
      @PathVariable Long idPiezaExpuesta,
      @RequestParam(required = false) Long idSala,
      @RequestParam(required = false) Integer orden,
      @RequestParam(required = false) String textoCuratorial,
      @RequestParam(required = false) Boolean portada,
      @RequestBody(required = false) PiezaExpuestaUpdateRequest body) {
    try {
      Long finalIdSala = coalesce(idSala, body != null ? body.getIdSala() : null);
      Integer finalOrden = coalesce(orden, body != null ? body.getOrden() : null);
      String finalTexto = coalesce(textoCuratorial, body != null ? body.getTextoCuratorial() : null);
      Boolean finalPortada = coalesce(portada, body != null ? body.getPortada() : null);

      PiezaExpuestaDTO dto = piezaExpuestaService.update(idPiezaExpuesta, finalIdSala, finalOrden, finalTexto, finalPortada);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @DeleteMapping("/piezas-expuestas/{idPiezaExpuesta}")
  public ResponseEntity<?> delete(@PathVariable Long idPiezaExpuesta) {
    try {
      piezaExpuestaService.delete(idPiezaExpuesta);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/piezas-expuestas/{idPiezaExpuesta}/portada")
  public ResponseEntity<?> setPortada(@PathVariable Long idPiezaExpuesta) {
    try {
      piezaExpuestaService.setPortada(idPiezaExpuesta);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return handle(e);
    }
  }

  private ResponseEntity<ErrorDTO> handle(Exception e) {
    if (e instanceof NotFoundException) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    }
    if (e instanceof InvalidPermissionException) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDTO(e.getMessage()));
    }
    if (e instanceof OperationNotAllowed) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
  }

  private <T> T coalesce(T first, T second) {
    return first != null ? first : second;
  }

  public static class PiezaExpuestaCreateRequest {
    private Long idObra;
    private Long idSala;
    private Integer orden;
    private String textoCuratorial;
    private Boolean portada;

    public Long getIdObra() {
      return idObra;
    }

    public void setIdObra(Long idObra) {
      this.idObra = idObra;
    }

    public Long getIdSala() {
      return idSala;
    }

    public void setIdSala(Long idSala) {
      this.idSala = idSala;
    }

    public Integer getOrden() {
      return orden;
    }

    public void setOrden(Integer orden) {
      this.orden = orden;
    }

    public String getTextoCuratorial() {
      return textoCuratorial;
    }

    public void setTextoCuratorial(String textoCuratorial) {
      this.textoCuratorial = textoCuratorial;
    }

    public Boolean getPortada() {
      return portada;
    }

    public void setPortada(Boolean portada) {
      this.portada = portada;
    }
  }

  public static class PiezaExpuestaUpdateRequest {
    private Long idSala;
    private Integer orden;
    private String textoCuratorial;
    private Boolean portada;

    public Long getIdSala() {
      return idSala;
    }

    public void setIdSala(Long idSala) {
      this.idSala = idSala;
    }

    public Integer getOrden() {
      return orden;
    }

    public void setOrden(Integer orden) {
      this.orden = orden;
    }

    public String getTextoCuratorial() {
      return textoCuratorial;
    }

    public void setTextoCuratorial(String textoCuratorial) {
      this.textoCuratorial = textoCuratorial;
    }

    public Boolean getPortada() {
      return portada;
    }

    public void setPortada(Boolean portada) {
      this.portada = portada;
    }
  }
}
