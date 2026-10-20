package ar.edu.ubp.das.ristorino.model.restauranteConfig;

import ar.edu.ubp.das.ristorino.model.RestauranteConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

// Los 2 restaurantes REST exponen el mismo contrato y se aseguran con JWT (como localidades-rest de la cátedra):
// alcanza con la URL base y el token Bearer generado con la clave secreta de cada restaurante
@Data
@EqualsAndHashCode(callSuper = true)
public class RestConfig extends RestauranteConfig {
    private String token;           // Token JWT (Bearer) para autenticarse en el servicio
}
