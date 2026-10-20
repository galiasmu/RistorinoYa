package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.beans.Sucursal;
import ar.edu.ubp.das.ristorino.beans.restaurante.DisponibilidadExterna;
import ar.edu.ubp.das.ristorino.services.RestauranteService;
import ar.edu.ubp.das.ristorino.utils.IdiomaUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public List<Restaurante> getRestaurantes() {
        return restauranteService.getRestaurantes();
    }

    @GetMapping("/{nroRestaurante}")
    public ResponseEntity<Restaurante> getRestaurante(@PathVariable int nroRestaurante) {
        Restaurante restaurante = restauranteService.getRestaurante(nroRestaurante);
        return restaurante == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(restaurante);
    }

    // Req 11: sucursales con zonas, turnos y preferencias
    @GetMapping("/{nroRestaurante}/sucursales")
    public List<Sucursal> getSucursales(@RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage,
                                        @PathVariable int nroRestaurante) {
        return restauranteService.getSucursales(nroRestaurante, IdiomaUtils.codIdioma(acceptLanguage));
    }

    // Req 14/37: grilla de disponibilidad, consultada en tiempo real al restaurante (no requiere login)
    @GetMapping("/{nroRestaurante}/sucursales/{nroSucursal}/disponibilidad")
    public ResponseEntity<?> consultarDisponibilidad(@PathVariable int nroRestaurante,
                                                     @PathVariable int nroSucursal,
                                                     @RequestParam String fecha,
                                                     @RequestParam int adultos,
                                                     @RequestParam(defaultValue = "0") int menores,
                                                     @RequestParam(required = false) Integer codZona) {
        try {
            List<DisponibilidadExterna> disponibilidad =
                    restauranteService.consultarDisponibilidad(nroRestaurante, nroSucursal, fecha, adultos, menores, codZona);
            return ResponseEntity.ok(disponibilidad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error consultando al restaurante: " + e.getMessage());
        }
    }
}
