package ar.edu.ubp.das.ristorino.components;

import ar.edu.ubp.das.ristorino.utils.Httpful;
import org.springframework.stereotype.Component;

// Crea el cliente REST de la cátedra (Httpful). Los servicios REST de los restaurantes
// se aseguran con JWT (Bearer), así que se le pasa el token configurado para cada restaurante.
@Component
public class RestClientFactory {
    public Httpful createRestClient(String baseUrl, String token) {
        return new Httpful(baseUrl).bearer(token);
    }
}
