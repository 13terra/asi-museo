package es.udc.asi.notebook_rest.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.asi.notebook_rest.model.domain.Note;
import es.udc.asi.notebook_rest.model.exception.NotFoundException;
import es.udc.asi.notebook_rest.model.exception.OperationNotAllowed;
import es.udc.asi.notebook_rest.model.service.NoteService;
import es.udc.asi.notebook_rest.model.service.dto.NoteDTO;
import es.udc.asi.notebook_rest.model.service.dto.NoteOwnerDTO;
import es.udc.asi.notebook_rest.web.exceptions.IdAndBodyNotMatchingOnUpdateException;
import es.udc.asi.notebook_rest.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notes")
public class NoteResource {

  @Autowired
  private NoteService noteService;

  @GetMapping
  public Collection<NoteDTO> findAll(@RequestParam(required = false) String category) {
    return noteService.findAll(category);
  }

  @GetMapping("/{id}")
  public NoteDTO findOne(@PathVariable Long id) throws NotFoundException {
    return noteService.findById(id);
  }

  @PutMapping("/{id}/owner")
  public NoteDTO updateNoteOwner(@PathVariable Long id, @RequestBody @Valid NoteOwnerDTO newOwner)
      throws NotFoundException, OperationNotAllowed {
    return noteService.updateNoteOwner(id, newOwner);
  }

  @PostMapping
  public NoteDTO create(@RequestBody @Valid NoteDTO note, Errors errors) throws RequestBodyNotValidException {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    return noteService.create(note);
  }

  @PutMapping("/{id}")
  public NoteDTO update(@PathVariable Long id, @RequestBody @Valid NoteDTO note, Errors errors)
      throws RequestBodyNotValidException, IdAndBodyNotMatchingOnUpdateException, NotFoundException,
      OperationNotAllowed {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    if (!id.equals(note.getId())) {
      throw new IdAndBodyNotMatchingOnUpdateException(Note.class);
    }

    return noteService.update(note);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    noteService.deleteById(id);
  }

}
