package ar.edu.ubp.das.ristorino.model.ia;

import ar.edu.ubp.das.ristorino.beans.ContenidoSinGenerar;
import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.ValorPreferencia;
import ar.edu.ubp.das.ristorino.beans.restaurante.RestauranteExterno;
import ar.edu.ubp.das.ristorino.components.RestClientFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ar.edu.ubp.das.ristorino.utils.Constantes.MENSAJE_IA_PENDIENTE;

// Implementación que consumirá una API REST de IA generativa (URL, clave y modelo en application.properties).
// PENDIENTE: por ahora sólo está el armado; los métodos todavía no llaman a la API.
@Component
public class MotorIARestFacadeImpl implements MotorIAFacade {

    @Autowired
    private RestClientFactory restClientFactory;

    @Value("${ia.api.url:}")
    private String apiUrl;

    @Value("${ia.api.key:}")
    private String apiKey;

    @Value("${ia.api.modelo:}")
    private String modelo;

    @Override
    public FiltrosBusqueda interpretarBusqueda(String texto, List<ValorPreferencia> preferencias) {
        String catalogo = preferencias.stream().map(p -> "{\"codCategoria\": " + p.getCodCategoria() + ", \"nroValorDominio\": " + p.getNroValorDominio() + "} = " + p.getCategoria() + ": " + p.getValorDominio()).collect(Collectors.joining("\n"));
        String prompt = """
        Eres un asistente de un portal de restaurantes de Córdoba. Convierte el pedido del usuario en filtros de búsqueda.
        1. Usa solo valores de este catálogo. Cada línea tiene el objeto a copiar en "valoresDominio" y lo que significa:
        %s
        2. Agrega un valor solo si el usuario lo pide o sale directo de una comida (pizza o pastas = Italiana, sushi = Japonesa, hamburguesas = Americana). No inventes: si ningún valor corresponde, deja "valoresDominio" vacío (tambien si el pedido no es sobre comida).
        3. Si el usuario nombra un barrio, ponlo en "barrio"; si no, deja null.
        4. Devuelve exclusivamente este JSON, sin texto adicional:
        {"valoresDominio": [{"codCategoria": {número}, "nroValorDominio": {número}}], "barrio": {barrio o null}}
        Ejemplos:
        "pastas en General Paz" -> {"valoresDominio": [{"codCategoria": 1, "nroValorDominio": 1}], "barrio": "General Paz"}
        "algo rico en Güemes" -> {"valoresDominio": [], "barrio": "Güemes"}
        """.formatted(catalogo);

        Map<String, Object> pedido = Map.of(
                "model", modelo,
                "stream", false,
                "format", "json",
                "keep_alive", "30m",
                "options", Map.of("temperature", 0),
                "messages", List.of(
                        Map.of("role", "system", "content", prompt),
                        Map.of("role", "user", "content", texto)));

        JsonObject respuesta = restClientFactory.createRestClient(apiUrl)
                .path("/chat")
                .post(pedido)
                .execute(JsonObject.class);

        String json = respuesta.getAsJsonObject("message").get("content").getAsString();
        return new Gson().fromJson(json, FiltrosBusqueda.class);
    }

    @Override
    public String generarContenidoPromocional(ContenidoSinGenerar contenido, RestauranteExterno identidad, String codIdioma) {
        // A implementar: armar el prompt con contenido.getContenidoAPublicar() y la identidad del restaurante
        // (tonoComunicacion, platosEmblematicos, descripcion) y devolver el texto generado.
        throw new UnsupportedOperationException(MENSAJE_IA_PENDIENTE);
    }
}
