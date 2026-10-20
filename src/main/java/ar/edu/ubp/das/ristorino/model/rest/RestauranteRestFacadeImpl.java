package ar.edu.ubp.das.ristorino.model.rest;

import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.beans.restaurante.*;
import ar.edu.ubp.das.ristorino.components.RestClientFactory;
import ar.edu.ubp.das.ristorino.model.RestauranteFacade;
import ar.edu.ubp.das.ristorino.utils.Httpful;
import ar.edu.ubp.das.ristorino.model.restauranteConfig.RestConfig;

import java.util.List;

public class RestauranteRestFacadeImpl implements RestauranteFacade {

    private final RestClientFactory restClientFactory;
    private final RestConfig config;
    private final Restaurante restaurante;

    public RestauranteRestFacadeImpl(RestClientFactory restClientFactory, RestConfig config, Restaurante restaurante) {
        this.restClientFactory = restClientFactory;
        this.config = config;
        this.restaurante = restaurante;
    }

    @Override
    public RestauranteExterno obtenerRestaurante() {
        return nuevoCliente()
                .path("/restaurante")
                .method("GET")
                .execute(RestauranteExterno.class);
    }

    @Override
    public List<SucursalExterna> obtenerSucursales() {
        try {
            List<SucursalExterna> sucursales = List.of(nuevoCliente()
                    .path("/sucursales")
                    .method("GET")
                    .execute(SucursalExterna[].class));

            System.out.println("Sucursales recibidas de " + restaurante.getRazonSocial() + ": " + sucursales.size());
            return sucursales;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo sucursales desde REST: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DisponibilidadExterna> consultarDisponibilidad(int nroSucursal, String fecha, int cantAdultos, int cantMenores, Integer codZona) {
        Httpful restClient = nuevoCliente()
                .path("/sucursales/" + nroSucursal + "/disponibilidad")
                .addQueryParam("fecha", fecha)
                .addQueryParam("adultos", String.valueOf(cantAdultos))
                .addQueryParam("menores", String.valueOf(cantMenores))
                .method("GET");
        if (codZona != null) {
            restClient.addQueryParam("codZona", String.valueOf(codZona));
        }
        return List.of(restClient.execute(DisponibilidadExterna[].class));
    }

    @Override
    public ReservaExternaResponse registrarReserva(ReservaExternaRequest reserva) {
        return nuevoCliente()
                .path("/reservas")
                .post(reserva)
                .execute(ReservaExternaResponse.class);
    }

    @Override
    public List<ContenidoExterno> obtenerContenidos() {
        try {
            List<ContenidoExterno> contenidos = List.of(nuevoCliente()
                    .path("/contenidos")
                    .method("GET")
                    .execute(ContenidoExterno[].class));

            System.out.println("Contenidos recibidos de " + restaurante.getRazonSocial() + ": " + contenidos.size());
            return contenidos;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo contenidos desde REST: " + e.getMessage(), e);
        }
    }

    @Override
    public ClickExternoResponse notificarClick(ClickExternoRequest click) {
        return nuevoCliente()
                .path("/contenidos/clics")
                .post(click)
                .execute(ClickExternoResponse.class);
    }

    // Httpful acumula el path, por eso se crea uno nuevo por operación
    private Httpful nuevoCliente() {
        return restClientFactory.createRestClient(config.getUrlServicio(), config.getToken());
    }
}
