package es.museum.asi.model.service;

import es.museum.asi.model.domain.TipoEntrada;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.service.dto.TipoEntradaDTO;
import es.museum.asi.repository.TipoEntradaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Los tipos de entrada se precargan al arrancar la app
 * Lo único que querremos hacer será listarlos con sus características
 * Implementa HU50
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
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

  public TipoEntradaDTO findById(Long id) throws NotFoundException {
    TipoEntrada tipoEntrada = tipoEntradaDao.findById(id);
    if(tipoEntrada == null) {
      throw new NotFoundException(id.toString(), TipoEntrada.class);
    }
    return new TipoEntradaDTO(tipoEntrada);
  }

  public TipoEntradaDTO findByNombre(String nombre) throws NotFoundException {
    TipoEntrada tipoEntrada = tipoEntradaDao.findByNombre(nombre);
    if (tipoEntrada == null) {
      throw new NotFoundException(nombre, TipoEntrada.class);
    }
    return new TipoEntradaDTO(tipoEntrada);
  }
}
