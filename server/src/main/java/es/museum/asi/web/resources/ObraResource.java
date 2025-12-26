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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.museum.asi.model.enums.EstadoObra;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.ObraService;
import es.museum.asi.model.service.dto.ObraDTO;
import es.museum.asi.web.util.ErrorDTO;
import es.museum.asi.web.resources.dto.ObraCreateRequest;
import es.museum.asi.web.resources.dto.ObraUpdateRequest;

/**
 * Resource de obras (HU43-HU47) + búsqueda.
 */
@RestController
@RequestMapping("/api/obras")
@CrossOrigin(origins = "*")
public class ObraResource {

  @Autowired
  private ObraService obraService;

  @GetMapping
  public ResponseEntity<Collection<ObraDTO>> findAll(
      @RequestParam(required = false) String autor,
      @RequestParam(required = false) Integer ano,
      @RequestParam(required = false) EstadoObra estado,
      @RequestParam(required = false) String tecnica) {
    return ResponseEntity.ok(obraService.findAll(autor, ano, estado, tecnica));
  }

  @GetMapping("/search")
  public ResponseEntity<Collection<ObraDTO>> search(@RequestParam String query) {
    return ResponseEntity.ok(obraService.search(query));
  }

  @GetMapping("/{idObra}")
  public ResponseEntity<?> findById(@PathVariable Long idObra) {
    try {
      return ResponseEntity.ok(obraService.findById(idObra));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Obra no encontrada"));
    }
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<?> create(
      @RequestParam String titulo,
      @RequestParam(required = false) String autor,
      @RequestParam(required = false) Integer anoCreacion,
      @RequestParam(required = false) String tecnica,
      @RequestParam(required = false) String dimensiones,
      @RequestParam(required = false) String imagenUrlMET,
      @RequestParam(required = false) MultipartFile imagenFile,
      @RequestParam(required = false) Long idExterno,
      @RequestParam(required = false) EstadoObra estado) {
    try {
      ObraDTO dto = obraService.create(titulo, autor, anoCreacion, tecnica, dimensiones, imagenUrlMET, imagenFile, idExterno, estado);
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @PostMapping(consumes = {"application/json"})
  public ResponseEntity<?> createJson(@org.springframework.web.bind.annotation.RequestBody ObraCreateRequest request) {
    try {
      ObraDTO dto = obraService.create(
        request.getTitulo(),
        request.getAutor(),
        request.getAnioCreacion(),
        request.getTecnica(),
        request.getDimensiones(),
        request.getImagen(),
        null,
        request.getIdExterno(),
        request.getEstado()
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @PutMapping(value = "/{idObra}", consumes = {"multipart/form-data"})
  public ResponseEntity<?> update(
    @PathVariable Long idObra,
    @RequestParam(required = false) String titulo,
    @RequestParam(required = false) String autor,
    @RequestParam(required = false) Integer anoCreacion,
    @RequestParam(required = false) String tecnica,
    @RequestParam(required = false) String dimensiones,

    @RequestParam(required = false) String imagenUrlMET,

    @RequestParam(required = false) MultipartFile imagenFile,
    @RequestParam(required = false) EstadoObra estado) {
    try {
      // Asegúrate de pasar 'imagenUrlMET' a tu servicio también
      return ResponseEntity.ok(
        obraService.update(idObra, titulo, autor, anoCreacion, tecnica, dimensiones, imagenUrlMET, imagenFile, estado)
      );
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Obra no encontrada"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @DeleteMapping("/{idObra}")
  public ResponseEntity<?> delete(@PathVariable Long idObra) {
    try {
      obraService.delete(idObra);
      return ResponseEntity.noContent().build();
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Obra no encontrada"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }
}
