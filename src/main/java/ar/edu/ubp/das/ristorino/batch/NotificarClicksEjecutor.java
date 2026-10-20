package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.RistorinoApplication;
import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.repository.RestauranteRepository;
import ar.edu.ubp.das.ristorino.services.impl.ContenidoServiceImpl;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

// Req 50: informa a cada restaurante los clics pendientes en sus contenidos.
// Se ejecuta sin levantar el servidor web, así se puede programar desde el cron del sistema operativo.
public class NotificarClicksEjecutor {

    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(RistorinoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        ContenidoServiceImpl contenidoService = context.getBean(ContenidoServiceImpl.class);
        RestauranteRepository restauranteRepository = context.getBean(RestauranteRepository.class);

        for (Restaurante restaurante : restauranteRepository.getRestaurantes()) {
            try {
                contenidoService.notificarClicks(restaurante.getNroRestaurante());
                System.out.println("OK: " + restaurante.getRazonSocial());
            } catch (Exception e) {
                // Si un restaurante no responde, se sigue con los demás
                System.err.println("Error con " + restaurante.getRazonSocial() + ": " + e.getMessage());
            }
        }
    }
}
