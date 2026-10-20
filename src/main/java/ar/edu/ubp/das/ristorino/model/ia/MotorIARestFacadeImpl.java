package ar.edu.ubp.das.ristorino.model.ia;

import ar.edu.ubp.das.ristorino.beans.ContenidoSinGenerar;
import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.restaurante.RestauranteExterno;
import ar.edu.ubp.das.ristorino.components.RestClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    public FiltrosBusqueda interpretarBusqueda(String texto, String codIdioma) {
        // A implementar: armar el prompt con el texto y los valores de dominio disponibles,
        // llamar a la API con un RestClient (restClientFactory) y convertir la respuesta en FiltrosBusqueda.
        throw new UnsupportedOperationException(MENSAJE_IA_PENDIENTE);
    }

    @Override
    public String generarContenidoPromocional(ContenidoSinGenerar contenido, RestauranteExterno identidad, String codIdioma) {
        // A implementar: armar el prompt con contenido.getContenidoAPublicar() y la identidad del restaurante
        // (tonoComunicacion, platosEmblematicos, descripcion) y devolver el texto generado.
        throw new UnsupportedOperationException(MENSAJE_IA_PENDIENTE);
    }
}
