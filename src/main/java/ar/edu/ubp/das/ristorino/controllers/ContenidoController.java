package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.ClickRequest;
import ar.edu.ubp.das.ristorino.beans.Contenido;
import ar.edu.ubp.das.ristorino.services.ContenidoService;
import ar.edu.ubp.das.ristorino.utils.IdiomaUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contenidos")
public class ContenidoController {

    private final ContenidoService contenidoService;

    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    // Req 12: contenidos promocionales vigentes (portada, o los de un restaurante)
    @GetMapping
    public List<Contenido> getContenidosVigentes(@RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage,
                                                 @RequestParam(required = false) Integer nroRestaurante) {
        return contenidoService.getContenidosVigentes(IdiomaUtils.codIdioma(acceptLanguage), nroRestaurante);
    }

    // Req 50: clic en un contenido. El login es opcional: si viene token, el clic queda asociado al cliente.
    @PostMapping("/clicks")
    public ResponseEntity<Map<String, Integer>> registrarClick(@RequestBody ClickRequest click, @AuthenticationPrincipal Jwt jwt) {
        Integer nroCliente = jwt == null ? null : Integer.valueOf(jwt.getSubject());
        Integer nroClick = contenidoService.registrarClick(click, nroCliente);
        // El SP no inserta nada si el contenido no existe en el portal
        return nroClick == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.status(HttpStatus.CREATED).body(Map.of("nroClick", nroClick));
    }
}
