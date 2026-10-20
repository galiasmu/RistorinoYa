package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.BusquedaRequest;
import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.ResultadoBusqueda;
import ar.edu.ubp.das.ristorino.services.BusquedaService;
import ar.edu.ubp.das.ristorino.utils.IdiomaUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/busqueda")
public class BusquedaController {

    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    // Req 9: búsqueda con filtros (valores de preferencias y barrio)
    @PostMapping("/filtros")
    public List<ResultadoBusqueda> buscar(@RequestBody FiltrosBusqueda filtros) {
        return busquedaService.buscar(filtros);
    }

    // Req 10: búsqueda en lenguaje natural (usa el motor de IA)
    @PostMapping("/lenguaje-natural")
    public ResponseEntity<?> buscarLenguajeNatural(@RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage,
                                                   @RequestBody BusquedaRequest busqueda) {
        try {
            return ResponseEntity.ok(busquedaService.buscarLenguajeNatural(busqueda.getTexto(), IdiomaUtils.codIdioma(acceptLanguage)));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        }
    }
}
