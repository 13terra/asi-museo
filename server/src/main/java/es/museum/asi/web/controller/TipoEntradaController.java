package es.museum.asi.web.controller;

import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.service.TipoEntradaService;
import es.museum.asi.model.service.dto.TipoEntradaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class TipoEntradaController {

  @Autowired
  private TipoEntradaService tipoEntradaService;

  /**
   * HU50 - Listar tipos de entrada
   * Público: visitantes necesitan ver precios al reservar
   */
  @GetMapping("/tipos-entrada")
  public Collection<TipoEntradaDTO> findAllTiposEntrada() {
    return tipoEntradaService.findAll();
  }

  /**
   * HU50 - Obtener tipo de entrada por ID
   */
  @GetMapping("/tipos-entrada/{id}")
  public TipoEntradaDTO findTipoEntradaById(@PathVariable Long id) throws NotFoundException {
    return tipoEntradaService.findById(id);
  }
}
