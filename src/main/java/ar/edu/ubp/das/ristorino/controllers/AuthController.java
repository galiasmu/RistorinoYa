package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.LoginRequest;
import ar.edu.ubp.das.ristorino.beans.LoginResponse;
import ar.edu.ubp.das.ristorino.beans.RegistroRequest;
import ar.edu.ubp.das.ristorino.beans.RegistroResponse;
import ar.edu.ubp.das.ristorino.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Req 1: alta de usuario
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registrar(@RequestBody RegistroRequest registro) {
        RegistroResponse respuesta = authService.registrar(registro);
        return "REGISTRADO".equals(respuesta.getEstado())
                ? ResponseEntity.status(HttpStatus.CREATED).body(respuesta)
                : ResponseEntity.badRequest().body(respuesta);
    }

    // Req 2: iniciar sesión → devuelve el token JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest login) {
        LoginResponse respuesta = authService.login(login);
        return respuesta == null
                ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                : ResponseEntity.ok(respuesta);
    }

    // Req 5: cerrar sesión (invalida el token: SecurityConfig rechaza los tokens revocados)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logout(jwt.getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
