package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.beans.Sucursal;
import ar.edu.ubp.das.ristorino.beans.restaurante.DisponibilidadExterna;
import ar.edu.ubp.das.ristorino.model.RestauranteFacade;

import java.util.List;

public interface RestauranteService {

    List<Restaurante> getRestaurantes();

    Restaurante getRestaurante(int nroRestaurante);

    List<Sucursal> getSucursales(int nroRestaurante, String codIdioma);

    List<DisponibilidadExterna> consultarDisponibilidad(int nroRestaurante, int nroSucursal, String fecha, int cantAdultos, int cantMenores, Integer codZona);

    // Req 45: devuelve cuántas sucursales se guardaron
    int actualizarSucursales(int nroRestaurante);

    // Crea la facade (REST o SOAP) del restaurante a partir de su configuración en la BD
    RestauranteFacade obtenerFacade(int nroRestaurante);

    // Req 11: trae la descripción del restaurante y la guarda en el portal
    void actualizarRestaurante(int nroRestaurante);
}
