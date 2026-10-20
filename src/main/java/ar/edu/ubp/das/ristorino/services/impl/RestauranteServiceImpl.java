package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.beans.Sucursal;
import ar.edu.ubp.das.ristorino.beans.restaurante.DisponibilidadExterna;
import ar.edu.ubp.das.ristorino.beans.restaurante.SucursalExterna;
import ar.edu.ubp.das.ristorino.components.RestauranteFacadeFactory;
import ar.edu.ubp.das.ristorino.model.RestauranteConfig;
import ar.edu.ubp.das.ristorino.model.RestauranteFacade;
import ar.edu.ubp.das.ristorino.repository.RestauranteRepository;
import ar.edu.ubp.das.ristorino.services.RestauranteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteFacadeFactory restauranteFacadeFactory;
    private final RestauranteRepository restauranteRepository;

    public RestauranteServiceImpl(RestauranteFacadeFactory restauranteFacadeFactory, RestauranteRepository restauranteRepository) {
        this.restauranteFacadeFactory = restauranteFacadeFactory;
        this.restauranteRepository = restauranteRepository;
    }

    @Override
    public List<Restaurante> getRestaurantes() {
        return restauranteRepository.getRestaurantes();
    }

    @Override
    public Restaurante getRestaurante(int nroRestaurante) {
        return restauranteRepository.getRestaurante(nroRestaurante);
    }

    @Override
    public List<Sucursal> getSucursales(int nroRestaurante, String codIdioma) {
        return restauranteRepository.getSucursales(nroRestaurante, codIdioma);
    }

    // Req 14/37: se consulta en tiempo real al sistema del restaurante
    @Override
    public List<DisponibilidadExterna> consultarDisponibilidad(int nroRestaurante, int nroSucursal, String fecha, int cantAdultos, int cantMenores, Integer codZona) {
        return obtenerFacade(nroRestaurante).consultarDisponibilidad(nroSucursal, fecha, cantAdultos, cantMenores, codZona);
    }

    // Req 45: trae las sucursales del restaurante y las guarda en la BD del portal
    @Override
    public int actualizarSucursales(int nroRestaurante) {
        int guardadas = 0;
        for (SucursalExterna sucursal : obtenerFacade(nroRestaurante).obtenerSucursales()) {
            try {
                restauranteRepository.insUpdSucursal(nroRestaurante, sucursal);
                guardadas++;
            } catch (Exception e) {
                System.err.println("Error al guardar la sucursal " + sucursal.getNroSucursal() + ": " + e.getMessage());
            }
        }
        return guardadas;
    }

    @Override
    public RestauranteFacade obtenerFacade(int nroRestaurante) {
        return restauranteFacadeFactory.createFacade(obtenerConfiguracionRestaurante(nroRestaurante), getRestauranteExistente(nroRestaurante));
    }

    private Restaurante getRestauranteExistente(int nroRestaurante) {
        Restaurante restaurante = restauranteRepository.getRestaurante(nroRestaurante);
        if (restaurante == null) {
            throw new RuntimeException("No existe el restaurante número " + nroRestaurante);
        }
        return restaurante;
    }

    private RestauranteConfig obtenerConfiguracionRestaurante(int nroRestaurante) {
        return restauranteRepository.findRestauranteConfig(nroRestaurante).orElseThrow(() ->
                new RuntimeException("No se encontró la configuración del restaurante número " + nroRestaurante));
    }
}
