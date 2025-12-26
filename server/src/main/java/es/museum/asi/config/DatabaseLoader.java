package es.museum.asi.config;

import es.museum.asi.model.domain.TipoEntrada;
import es.museum.asi.model.enums.*;
import es.museum.asi.model.service.*;
import es.museum.asi.model.service.dto.*;
import es.museum.asi.repository.TipoEntradaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class DatabaseLoader {

  private final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

  @Autowired private TipoEntradaDao tipoEntradaDao;
  @Autowired private UserService userService;
  @Autowired private SalaService salaService;
  @Autowired private ObraService obraService;
  @Autowired private ExpoService expoService;
  @Autowired private EdicionService edicionService;
  @Autowired private SesionService sesionService;
  @Autowired private PiezaExpuestaService piezaExpuestaService;

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void loadData() throws Exception {
    logger.info("Iniciando carga datos semilla...");

    Authentication seedAuth = new UsernamePasswordAuthenticationToken(
      "seed-loader", "N/A", Collections.singletonList(new SimpleGrantedAuthority(UserAuthority.ADMIN.name()))
    );
    SecurityContextHolder.getContext().setAuthentication(seedAuth);

    try {
      // 1. USUARIOS
      userService.createUser("admin", "admin", UserAuthority.ADMIN, null);
      userService.createUser("gestor1", "gestor1", UserAuthority.GESTOR, TipoPermiso.CREADOR);
      userService.createUser("gestor2", "gestor2", UserAuthority.GESTOR, TipoPermiso.EDITOR);
      userService.registerUser("visitante", "visitante");
      logger.info("Usuarios creados");

      // 2. TIPOS DE ENTRADA
      createTipoEntrada("General", 15.0f, "Entrada general para adultos");
      createTipoEntrada("Reducida", 7.5f, "Mayores de 65 años y grupos");
      createTipoEntrada("Estudiante", 5.0f, "Estudiantes con carnet");
      logger.info("Tipos de entrada creados");

      // 3. SALAS
      SalaDTO sala1 = salaService.create("Sala Principal", 0);
      SalaDTO sala2 = salaService.create("Sala de Esculturas", 0);
      SalaDTO sala3 = salaService.create("Sala de Proyecciones", 1);
      logger.info("Salas creadas");

      // 4. OBRAS
      ObraDTO obra1 = obraService.create("La Noche Estrellada", "Vincent van Gogh", 1889, "Óleo sobre lienzo", "73.7 cm × 92.1 cm", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/1200px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg", null, null, EstadoObra.EN_ALMACEN);
      ObraDTO obra2 = obraService.create("El Grito", "Edvard Munch", 1893, "Óleo, temple y pastel sobre cartón", "91 cm × 73.5 cm", "https://upload.wikimedia.org/wikipedia/commons/c/c5/Edvard_Munch%2C_1893%2C_The_Scream%2C_oil%2C_tempera_and_pastel_on_cardboard%2C_91_x_73_cm%2C_National_Gallery_of_Norway.jpg", null, null, EstadoObra.EN_ALMACEN);
      ObraDTO obra3 = obraService.create("Guernica", "Pablo Picasso", 1937, "Óleo sobre lienzo", "349 cm × 776 cm", "https://upload.wikimedia.org/wikipedia/en/7/74/PicassoGuernica.jpg", null, null, EstadoObra.EN_ALMACEN);
      ObraDTO obra4 = obraService.create("Las Meninas", "Diego Velázquez", 1656, "Óleo sobre lienzo", "318 cm × 276 cm", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Las_Meninas%2C_by_Diego_Vel%C3%A1zquez%2C_from_Prado_in_Google_Earth.jpg/1200px-Las_Meninas%2C_by_Diego_Vel%C3%A1zquez%2C_from_Prado_in_Google_Earth.jpg", null, null, EstadoObra.EN_ALMACEN);
      ObraDTO obra5 = obraService.create("El David", "Miguel Ángel", 1504, "Mármol de Carrara", "517 cm", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Michelangelo%27s_David_-_Victoria_and_Albert_Museum.jpg/800px-Michelangelo%27s_David_-_Victoria_and_Albert_Museum.jpg", null, null, EstadoObra.EN_ALMACEN);
      logger.info("Obras creadas");

      // 5. EXPOSICIONES

      // EXPO 1: ACTIVA (Maestros del Impresionismo)
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("gestor1", "N/A", Collections.singletonList(new SimpleGrantedAuthority(UserAuthority.GESTOR.name()))));
      ExposicionDTO expoImp = expoService.create("Maestros del Impresionismo", "Un recorrido por la luz y el color de finales del siglo XIX.");
      expoService.update(expoImp.getIdExposicion(), null, null, EstadoExpo.ACTIVA, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Irises-Vincent_van_Gogh.jpg/1200px-Irises-Vincent_van_Gogh.jpg");
      
      // EDICIÓN 1 (Publicada)
      EdicionDTO edicion1 = edicionService.create(expoImp.getIdExposicion(), LocalDate.now().minusDays(5), LocalDate.now().plusMonths(2), "Edición Invierno 2025");
      piezaExpuestaService.create(obra1.getIdObra(), edicion1.getIdEdicion(), sala1.getIdSala(), 1, "Obra maestra del postimpresionismo.", true);
      piezaExpuestaService.create(obra2.getIdObra(), edicion1.getIdEdicion(), sala1.getIdSala(), 2, "Icono del expresionismo.", false);
      
      // Sesiones
      sesionService.create(edicion1.getIdEdicion(), LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 30), 50, Arrays.asList(sala1.getIdSala()));
      sesionService.create(edicion1.getIdEdicion(), LocalDate.now().plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 30), 50, Arrays.asList(sala1.getIdSala()));

      edicionService.publicar(edicion1.getIdEdicion());
      
      // EXPO 2: BORRADOR (Arte Moderno)
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("gestor2", "N/A", Collections.singletonList(new SimpleGrantedAuthority(UserAuthority.GESTOR.name()))));
      ExposicionDTO expoMod = expoService.create("Arte Moderno y Abstracto", "Formas, colores y emociones en el siglo XX.");
      EdicionDTO edicion2 = edicionService.create(expoMod.getIdExposicion(), LocalDate.now().plusMonths(3), LocalDate.now().plusMonths(5), "Primavera 2025");
      piezaExpuestaService.create(obra3.getIdObra(), edicion2.getIdEdicion(), sala2.getIdSala(), 1, "El horror de la guerra.", true);

      // EXPO 3: ARCHIVADA (Antiguo Egipto)
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "N/A", Collections.singletonList(new SimpleGrantedAuthority(UserAuthority.ADMIN.name()))));
      ExposicionDTO expoEgipto = expoService.create("Tesoros del Antiguo Egipto", "Misterios de los faraones.");
      expoService.update(expoEgipto.getIdExposicion(), null, null, null, "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/AmenhotepIII-Statue-BritishMuseum-August19-2008.jpg/800px-AmenhotepIII-Statue-BritishMuseum-August19-2008.jpg");
      expoService.archivar(expoEgipto.getIdExposicion());

      logger.info("Datos semilla cargados correctamente");
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  private void createTipoEntrada(String nombre, float precio, String desc) {
    TipoEntrada t = new TipoEntrada();
    t.setNombre(nombre);
    t.setPrecio(precio);
    t.setDescripcion(desc);
    tipoEntradaDao.create(t);
  }
}
