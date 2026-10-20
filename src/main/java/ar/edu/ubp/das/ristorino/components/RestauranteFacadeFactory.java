package ar.edu.ubp.das.ristorino.components;

import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.model.RestauranteConfig;
import ar.edu.ubp.das.ristorino.model.RestauranteFacade;
import ar.edu.ubp.das.ristorino.model.rest.RestauranteRestFacadeImpl;
import ar.edu.ubp.das.ristorino.model.restauranteConfig.RestConfig;
import ar.edu.ubp.das.ristorino.model.restauranteConfig.SoapConfig;
import ar.edu.ubp.das.ristorino.model.soap.RestauranteSoapFacadeImpl;
import org.springframework.stereotype.Component;

import static ar.edu.ubp.das.ristorino.utils.Constantes.MENSAJE_ERROR_CONFIGURACION_SERVICIO;

// Crea la facade de un restaurante según su configuración.
// CustomRowMapper ya armó un RestConfig o un SoapConfig a partir de tipo_servicio,
// así que alcanza con mirar de qué tipo es la configuración.
@Component
public class RestauranteFacadeFactory {

    private final RestClientFactory restClientFactory;
    private final SOAPClientFactory soapClientFactory;

    public RestauranteFacadeFactory(RestClientFactory restClientFactory, SOAPClientFactory soapClientFactory) {
        this.restClientFactory = restClientFactory;
        this.soapClientFactory = soapClientFactory;
    }

    public RestauranteFacade createFacade(RestauranteConfig config, Restaurante restaurante) {
        if (config instanceof RestConfig restConfig) {
            return new RestauranteRestFacadeImpl(restClientFactory, restConfig, restaurante);
        }
        if (config instanceof SoapConfig soapConfig) {
            return new RestauranteSoapFacadeImpl(soapClientFactory, soapConfig, restaurante);
        }
        throw new IllegalArgumentException(MENSAJE_ERROR_CONFIGURACION_SERVICIO + "el restaurante " + restaurante.getNroRestaurante());
    }
}
