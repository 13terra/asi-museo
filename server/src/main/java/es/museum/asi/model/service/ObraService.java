package es.museum.asi.model.service;

import es.museum.asi.model.enums.EstadoObra;
import es.museum.asi.model.service.dto.ObraDTO;
import es.museum.asi.repository.ObraDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de obras.
 * Por ahora solo HU44 (Listar obras).
 * HU43, HU45-HU47 se implementarán al final según indicación del profesor.
 */

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ObraService {

  private final Logger logger = LoggerFactory.getLogger(ObraService.class);

  @Autowired
  private ObraDao obraDao;

  // ==================== HU44: LISTAR OBRAS ====================

  /**
   * HU44 - Listar obras del catálogo
   * Con filtros opcionales por autor, año, estado, técnica
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<ObraDTO> findAll(String autor, Integer ano, EstadoObra estado, String tecnica) {

    return obraDao.findAll().stream()
      .filter(o -> autor == null ||
        (o.getAutor() != null && o.getAutor().toLowerCase().contains(autor.toLowerCase())))
      .filter(o -> ano == null ||
        (o.getAnoCreacion() != null && o.getAnoCreacion().equals(ano)))
      .filter(o -> estado == null || o.getEstado() == estado)
      .filter(o -> tecnica == null ||
        (o.getTecnica() != null && o.getTecnica().toLowerCase().contains(tecnica.toLowerCase())))
      .sorted((o1, o2) -> o1.getTitulo().compareTo(o2.getTitulo())) // Orden alfabético
      .map(ObraDTO::new)
      .collect(Collectors.toList());
  }


  /**
   * Buscar obras por término general (útil para selectores en formularios)
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
  public Collection<ObraDTO> search(String query) {
    if (query == null || query.trim().isEmpty()) {
      return findAll(null, null, null, null);
    }

    String queryLower = query.toLowerCase();
    return obraDao.findAll().stream()
      .filter(o ->
        (o.getTitulo() != null && o.getTitulo().toLowerCase().contains(queryLower)) ||
          (o.getAutor() != null && o.getAutor().toLowerCase().contains(queryLower)) ||
          (o.getTecnica() != null && o.getTecnica().toLowerCase().contains(queryLower))
      )
      .sorted((o1, o2) -> o1.getTitulo().compareTo(o2.getTitulo()))
      .map(ObraDTO::new)
      .collect(Collectors.toList());
  }


}
