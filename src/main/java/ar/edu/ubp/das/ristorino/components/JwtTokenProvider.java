package ar.edu.ubp.das.ristorino.components;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Genera los tokens JWT (HS256) que el portal entrega al iniciar sesión (Req 2) y registra los que se
// invalidan al cerrar sesión (Req 5). La validación la hace Spring Security (SecurityConfig), igual que en
// localidades-rest de la cátedra: por eso se firma con la misma clave (security.jwt.secret).
@Component
public class JwtTokenProvider {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final String HEADER = ENCODER.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

    private final Gson gson = new Gson();

    // Tokens invalidados por "cerrar sesión" (se pierden al reiniciar, cuando igual ya vencieron o no)
    private final Set<String> tokensRevocados = ConcurrentHashMap.newKeySet();

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiracion-minutos:120}")
    private long expiracionMinutos;

    public String generarToken(int nroCliente, String correo) {
        JsonObject payload = new JsonObject();
        payload.addProperty("sub", String.valueOf(nroCliente));
        payload.addProperty("correo", correo);
        payload.addProperty("exp", Instant.now().plusSeconds(expiracionMinutos * 60).getEpochSecond());

        String contenido = HEADER + "." + ENCODER.encodeToString(gson.toJson(payload).getBytes(StandardCharsets.UTF_8));
        return contenido + "." + firmar(contenido);
    }

    public void revocarToken(String token) {
        if (token != null) {
            tokensRevocados.add(token);
        }
    }

    public boolean estaRevocado(String token) {
        return tokensRevocados.contains(token);
    }

    private String firmar(String contenido) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }
}
