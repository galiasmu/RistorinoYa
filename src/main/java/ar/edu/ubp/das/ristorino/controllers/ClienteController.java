package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.Cliente;
import ar.edu.ubp.das.ristorino.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final AuthService authService;

    public ClienteController(AuthService authService) {
        this.authService = authService;
    }

    // Perfil del cliente logueado (requiere token): el nroCliente viaja en el "sub" del JWT
    @GetMapping("/me")
    public ResponseEntity<Cliente> getPerfil(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(authService.getPerfil(Integer.parseInt(jwt.getSubject())));
    }
}
