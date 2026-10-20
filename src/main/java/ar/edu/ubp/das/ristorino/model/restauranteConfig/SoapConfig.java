package ar.edu.ubp.das.ristorino.model.restauranteConfig;

import ar.edu.ubp.das.ristorino.model.RestauranteConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SoapConfig extends RestauranteConfig {
    private String namespace;       // Namespace del servicio SOAP
    private String serviceName;     // Nombre del service en el WSDL
    private String portName;        // Nombre del port en el WSDL
}
