package es.museum.asi.model.service;

import es.museum.asi.model.domain.TipoEntrada;
import es.museum.asi.model.service.dto.TipoEntradaDTO;
import es.museum.asi.repository.TipoEntradaDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Los tipos de entrada se precargan al arrancar la app
 * Lo único que querremos hacer será listarlos con sus características
 * Implementa HU50
 */
public class TipoEntradaService {

  @Autowired
  private TipoEntradaDao tipoEntradaDao;

  /**
   *  HU50 - Listar tipos entrada
   *  Accesible por todos (visitantes necesitan ver precios al reservar)
   */
  public Collection<TipoEntradaDTO> findAll() {
    return tipoEntradaDao.findAll().stream()
      .sorted(Comparator.comparing(TipoEntrada::getPrecio))
      .map(TipoEntradaDTO::new)
      .collect(Collectors.toList());
  }
}
