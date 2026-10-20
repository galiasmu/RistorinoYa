package ar.edu.ubp.das.ristorino.config;

import ar.edu.ubp.das.ristorino.components.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

// Seguridad JWT (Bearer HS256) igual que localidades-rest de la cátedra (guía M2-2-4).
// Diferencia: el portal se navega sin login, así que solo se protegen las rutas personales.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Requieren sesión iniciada: confirmar y ver reservas, perfil y cerrar sesión (Req 2, 5, 15, 16)
                    .requestMatchers("/api/v1/reservas/**", "/api/v1/clientes/**", "/api/v1/auth/logout").authenticated()
                    // El resto del portal se navega sin autenticarse
                    .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        var key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).build();
        // Además de la firma y el vencimiento, rechaza los tokens de sesiones cerradas con logout (Req 5)
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), this::validarNoRevocado));
        return decoder;
    }

    private OAuth2TokenValidatorResult validarNoRevocado(Jwt jwt) {
        return jwtTokenProvider.estaRevocado(jwt.getTokenValue())
                ? OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "La sesión fue cerrada", null))
                : OAuth2TokenValidatorResult.success();
    }
}
