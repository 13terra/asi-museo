package es.museo.met.controller;

import es.museo.met.dto.ObraDetalleDTO;
import es.museo.met.dto.ObraSimplificadaDTO;
import es.museo.met.service.MetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para The MET microservice
 */
@RestController
@RequestMapping("api/met")
@CrossOrigin(origins = "*") // Permitir CORS para desarrollo
public class MetController {

    private static final Logger logger = LoggerFactory.getLogger(MetController.class);

    @Autowired
    private MetService metService;

    /**
     * HU48 - Buscar obras por término
     * GET /api/met/obras?query={term}
     */
    @GetMapping("/obras")
    public ResponseEntity<List<ObraSimplificadaDTO>> buscarObras(@RequestParam("query") String query) {

        logger.info("GET /api/met/obras?query={}", query);

        List<ObraSimplificadaDTO> obras = metService.buscarObras(query);

        return ResponseEntity.ok(obras);
    }


    /**
     * HU49 - Obtener detalle de obra
     * GET /api/met/obras/{met_id}
     */
    @GetMapping("/obras/{met_id}")
    public ResponseEntity<ObraDetalleDTO> obtenerDetalle(
            @PathVariable("met_id") Long metId
    ) {
        logger.info("GET /api/met/obras/{}", metId);

        ObraDetalleDTO obra = metService.obtenerDetalle(metId);

        if (obra == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(obra);
    }

}
