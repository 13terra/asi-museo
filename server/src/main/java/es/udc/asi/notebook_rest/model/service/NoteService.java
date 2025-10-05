package es.udc.asi.notebook_rest.model.service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.asi.notebook_rest.model.domain.Note;
import es.udc.asi.notebook_rest.model.domain.User;
import es.udc.asi.notebook_rest.model.domain.UserAuthority;
import es.udc.asi.notebook_rest.model.exception.NotFoundException;
import es.udc.asi.notebook_rest.model.exception.OperationNotAllowed;
import es.udc.asi.notebook_rest.model.repository.CategoryDao;
import es.udc.asi.notebook_rest.model.repository.NoteDao;
import es.udc.asi.notebook_rest.model.repository.UserDao;
import es.udc.asi.notebook_rest.model.service.dto.NoteDTO;
import es.udc.asi.notebook_rest.model.service.dto.NoteOwnerDTO;
import es.udc.asi.notebook_rest.model.service.dto.UserDTOPrivate;
import es.udc.asi.notebook_rest.security.SecurityUtils;
import jakarta.validation.Valid;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class NoteService {

  @Autowired
  private NoteDao noteDAO;

  @Autowired
  private UserService userService;

  @Autowired
  private UserDao userDAO;

  @Autowired
  private CategoryDao categoryDAO;

  public Collection<NoteDTO> findAll(String category) {
    Stream<Note> notes;

    if (SecurityUtils.getCurrentUserIsAdmin()) {
      notes = noteDAO.findAll(category).stream();
    } else {
      notes = noteDAO.findByUser(SecurityUtils.getCurrentUserLogin(), category).stream();
    }

    return notes.map(note -> new NoteDTO(note)).collect(Collectors.toList());
  }

  public NoteDTO findById(Long id) throws NotFoundException {
    Note note = noteDAO.findById(id);
    if (note == null) {
      throw new NotFoundException(id.toString(), Note.class);
    }
    return new NoteDTO(note);
  }

  @PreAuthorize("hasAuthority('USER')")
  @Transactional(readOnly = false)
  public NoteDTO create(NoteDTO note) {
    User currentUser = userDAO.findById(userService.getCurrentUserWithAuthority().getId());
    Note bdNote = new Note(note.getTitle(), note.getContent(), currentUser);
    if (note.getCategories() != null) {
      note.getCategories().forEach(cat -> {
        bdNote.getCategories().add(categoryDAO.findById(cat.getId()));
      });
    }
    noteDAO.create(bdNote);
    return new NoteDTO(bdNote);
  }

  @PreAuthorize("hasAuthority('USER')")
  @Transactional(readOnly = false)
  public NoteDTO update(@Valid NoteDTO note) throws NotFoundException, OperationNotAllowed {
    Note bdNote = noteDAO.findById(note.getId());
    if (bdNote == null) {
      throw new NotFoundException(note.getId().toString(), Note.class);
    }

    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    if (!bdNote.getOwner().getId().equals(currentUser.getId())) {
      throw new OperationNotAllowed("Current user is not the note owner");
    }

    bdNote.setTitle(note.getTitle());
    bdNote.setContent(note.getContent());
    bdNote.setArchived(note.getArchived());
    bdNote.getCategories().clear();
    if (note.getCategories() != null) {
      note.getCategories().forEach(cat -> {
        bdNote.getCategories().add(categoryDAO.findById(cat.getId()));
      });
    }
    noteDAO.update(bdNote);
    return new NoteDTO(bdNote);
  }

  @PreAuthorize("isAuthenticated()")
  @Transactional(readOnly = false)
  public void deleteById(Long id) throws NotFoundException, OperationNotAllowed {
    Note note = noteDAO.findById(id);
    if (note == null) {
      throw new NotFoundException(id.toString(), Note.class);
    }

    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    if (!currentUser.getId().equals(note.getOwner().getId())) {
      throw new OperationNotAllowed("Current user is not the note owner");
    }

    noteDAO.deleteById(id);
  }

  @PreAuthorize("hasAuthority('ADMIN')")   //
  @Transactional(readOnly = false)
  public NoteDTO updateNoteOwner(Long id, NoteOwnerDTO newOwner) throws NotFoundException, OperationNotAllowed {
    Note bdNote = noteDAO.findById(id);
    if (bdNote == null) {
      throw new NotFoundException(id.toString(), Note.class);
    }

    User user = userDAO.findById(newOwner.getUserId());
    if (user == null) {
      throw new NotFoundException(newOwner.getUserId().toString(), User.class);
    }
    if (user.getAuthority() == UserAuthority.ADMIN) {
      throw new OperationNotAllowed("Admins cannot own notes");
    }

    if (!bdNote.getOwner().equals(user)) {
      bdNote.setOwner(user);
      noteDAO.update(bdNote);
    }

    return new NoteDTO(bdNote);
  }

}
