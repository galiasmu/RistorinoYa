package ar.edu.ubp.das.ristorino.model.ia;

import ar.edu.ubp.das.ristorino.beans.ContenidoSinGenerar;
import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.restaurante.RestauranteExterno;

// Motor de IA generativa (Req 34 y 35). Igual que RestauranteFacade, aísla al resto del sistema
// del proveedor de IA: para cambiar de proveedor alcanza con otra implementación de esta interfaz.
public interface MotorIAFacade {

    // Req 35: convierte una frase en lenguaje natural en filtros estructurados
    // (valores de dominio de tipo de cocina, especialidad, estilo, nivel de precio y barrio)
    FiltrosBusqueda interpretarBusqueda(String texto, String codIdioma);

    // Req 34: genera el texto promocional de un contenido, en el idioma pedido,
    // respetando la identidad del restaurante (tono, platos emblemáticos)
    String generarContenidoPromocional(ContenidoSinGenerar contenido, RestauranteExterno identidad, String codIdioma);
}
