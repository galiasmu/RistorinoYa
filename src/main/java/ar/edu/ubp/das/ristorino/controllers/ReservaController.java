package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.ReservaCliente;
import ar.edu.ubp.das.ristorino.beans.ReservaRequest;
import ar.edu.ubp.das.ristorino.beans.ReservaResponse;
import ar.edu.ubp.das.ristorino.services.ReservaService;
import ar.edu.ubp.das.ristorino.utils.IdiomaUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ar.edu.ubp.das.ristorino.utils.Constantes.CONFIRMADA;

// Todas las rutas requieren sesión iniciada (SecurityConfig): el nroCliente viaja en el "sub" del JWT
@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Req 15: confirmar reserva → 201 CONFIRMADA | 409 RECHAZADA (el front vuelve a pedir la disponibilidad)
    @PostMapping
    public ResponseEntity<?> confirmarReserva(@AuthenticationPrincipal Jwt jwt,
                                              @RequestBody ReservaRequest reserva) {
        try {
            ReservaResponse respuesta = reservaService.confirmarReserva(Integer.parseInt(jwt.getSubject()), reserva);
            return CONFIRMADA.equals(respuesta.getEstado())
                    ? ResponseEntity.status(HttpStatus.CREATED).body(respuesta)
                    : ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error registrando la reserva en el restaurante: " + e.getMessage());
        }
    }

    // Req 16: reservas del cliente con filtros por estado y fechas
    @GetMapping
    public List<ReservaCliente> getReservas(@AuthenticationPrincipal Jwt jwt,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage,
                                            @RequestParam(required = false) Integer codEstado,
                                            @RequestParam(required = false) String fechaDesde,
                                            @RequestParam(required = false) String fechaHasta) {
        return reservaService.getReservasCliente(Integer.parseInt(jwt.getSubject()), IdiomaUtils.codIdioma(acceptLanguage), codEstado, fechaDesde, fechaHasta);
    }
}
