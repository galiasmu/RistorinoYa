package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.ClickPendiente;
import ar.edu.ubp.das.ristorino.beans.ClickRequest;
import ar.edu.ubp.das.ristorino.beans.Contenido;
import ar.edu.ubp.das.ristorino.beans.ContenidoSinGenerar;
import ar.edu.ubp.das.ristorino.beans.restaurante.ClickExternoRequest;
import ar.edu.ubp.das.ristorino.beans.restaurante.ClickExternoResponse;
import ar.edu.ubp.das.ristorino.beans.restaurante.ContenidoExterno;
import ar.edu.ubp.das.ristorino.beans.restaurante.RestauranteExterno;
import ar.edu.ubp.das.ristorino.model.RestauranteFacade;
import ar.edu.ubp.das.ristorino.model.ia.MotorIAFacade;
import ar.edu.ubp.das.ristorino.repository.ContenidoRepository;
import ar.edu.ubp.das.ristorino.services.ContenidoService;
import ar.edu.ubp.das.ristorino.services.RestauranteService;
import org.springframework.stereotype.Service;

import java.util.List;

import static ar.edu.ubp.das.ristorino.utils.Constantes.IDIOMA_POR_DEFECTO;

@Service
public class ContenidoServiceImpl implements ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final RestauranteService restauranteService;
    private final MotorIAFacade motorIAFacade;

    public ContenidoServiceImpl(ContenidoRepository contenidoRepository, RestauranteService restauranteService, MotorIAFacade motorIAFacade) {
        this.contenidoRepository = contenidoRepository;
        this.restauranteService = restauranteService;
        this.motorIAFacade = motorIAFacade;
    }

    @Override
    public List<Contenido> getContenidosVigentes(String codIdioma, Integer nroRestaurante) {
        return contenidoRepository.getContenidosVigentes(codIdioma, nroRestaurante);
    }

    @Override
    public Integer registrarClick(ClickRequest click, Integer nroCliente) {
        return contenidoRepository.insClick(click.getNroRestaurante(), click.getNroIdioma(), click.getNroContenido(), nroCliente);
    }

    @Override
    public int actualizarContenidos(int nroRestaurante) {
        int guardados = 0;
        for (ContenidoExterno contenido : restauranteService.obtenerFacade(nroRestaurante).obtenerContenidos()) {
            try {
                contenidoRepository.insUpdContenido(nroRestaurante, contenido);
                guardados++;
            } catch (Exception e) {
                System.err.println("Error al guardar el contenido " + contenido.getNroContenido() + ": " + e.getMessage());
            }
        }
        return guardados;
    }

    @Override
    public int notificarClicks(int nroRestaurante) {
        List<ClickPendiente> pendientes = contenidoRepository.getClicksPendientes(nroRestaurante);
        if (pendientes.isEmpty()) {
            return 0;
        }

        RestauranteFacade facade = restauranteService.obtenerFacade(nroRestaurante);
        int notificados = 0;
        for (ClickPendiente pendiente : pendientes) {
            try {
                ClickExternoResponse respuesta = facade.notificarClick(transformarClick(pendiente));
                // Si el restaurante lo rechaza (contenido inexistente) igual se marca, para no reintentarlo siempre
                contenidoRepository.marcarClickNotificado(pendiente);
                notificados++;
                System.out.println("Clic " + pendiente.getNroClick() + " notificado: " + respuesta.getEstado());
            } catch (Exception e) {
                // Queda pendiente y se reintenta en la próxima ejecución
                System.err.println("Error notificando el clic " + pendiente.getNroClick() + ": " + e.getMessage());
            }
        }
        return notificados;
    }

    @Override
    public int generarContenidosPromocionales() {
        int generados = 0;
        for (ContenidoSinGenerar contenido : contenidoRepository.getContenidosSinGenerar()) {
            RestauranteExterno identidad = restauranteService.obtenerFacade(contenido.getNroRestaurante()).obtenerRestaurante();
            String texto = motorIAFacade.generarContenidoPromocional(contenido, identidad, IDIOMA_POR_DEFECTO);
            contenidoRepository.updContenidoPromocional(contenido, texto);
            generados++;
        }
        return generados;
    }

    private ClickExternoRequest transformarClick(ClickPendiente pendiente) {
        ClickExternoRequest click = new ClickExternoRequest();
        click.setNroContenido(Integer.parseInt(pendiente.getCodContenidoRestaurante()));
        click.setFechaHoraRegistro(pendiente.getFechaHoraRegistro());
        click.setCostoClick(pendiente.getCostoClick());
        click.setApellido(pendiente.getApellido());
        click.setNombre(pendiente.getNombre());
        click.setCorreo(pendiente.getCorreo());
        click.setTelefonos(pendiente.getTelefonos());
        return click;
    }
}
