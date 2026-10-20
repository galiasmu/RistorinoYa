package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.services.ContenidoService;
import ar.edu.ubp.das.ristorino.services.RestauranteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Dispara a mano la sincronización con los restaurantes (lo mismo que hacen los ejecutores de batch/)
@RestController
@RequestMapping("/api/admin/restaurantes")
public class AdminTaskController {

    private final RestauranteService restauranteService;
    private final ContenidoService contenidoService;

    public AdminTaskController(RestauranteService restauranteService, ContenidoService contenidoService) {
        this.restauranteService = restauranteService;
        this.contenidoService = contenidoService;
    }

    // Req 45
    @PostMapping("/{nroRestaurante}/actualizar-sucursales")
    public ResponseEntity<String> actualizarSucursales(@PathVariable int nroRestaurante) {
        try {
            int guardadas = restauranteService.actualizarSucursales(nroRestaurante);
            return ResponseEntity.ok("Sucursales actualizadas para el restaurante " + nroRestaurante + ": " + guardadas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Req 46
    @PostMapping("/{nroRestaurante}/actualizar-contenidos")
    public ResponseEntity<String> actualizarContenidos(@PathVariable int nroRestaurante) {
        try {
            int guardados = contenidoService.actualizarContenidos(nroRestaurante);
            return ResponseEntity.ok("Contenidos actualizados para el restaurante " + nroRestaurante + ": " + guardados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Req 50
    @PostMapping("/{nroRestaurante}/notificar-clicks")
    public ResponseEntity<String> notificarClicks(@PathVariable int nroRestaurante) {
        try {
            int notificados = contenidoService.notificarClicks(nroRestaurante);
            return ResponseEntity.ok("Clics notificados al restaurante " + nroRestaurante + ": " + notificados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Req 34/48 (pendiente: requiere el motor de IA)
    @PostMapping("/generar-contenidos")
    public ResponseEntity<String> generarContenidos() {
        try {
            int generados = contenidoService.generarContenidosPromocionales();
            return ResponseEntity.ok("Contenidos generados: " + generados);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
