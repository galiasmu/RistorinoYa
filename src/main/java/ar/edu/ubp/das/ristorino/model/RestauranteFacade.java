package ar.edu.ubp.das.ristorino.model;

import ar.edu.ubp.das.ristorino.beans.restaurante.*;

import java.util.List;

// Operaciones que el portal consume de cada restaurante, sin importar si el servicio es REST o SOAP.
// La facade solo se encarga de la comunicación: no accede a la BD del portal.
public interface RestauranteFacade {

    RestauranteExterno obtenerRestaurante();

    // Req 45: sucursales publicadas por el restaurante (el portal las guarda después)
    List<SucursalExterna> obtenerSucursales();

    // Req 37: disponibilidad en tiempo real (codZona null = todas las zonas)
    List<DisponibilidadExterna> consultarDisponibilidad(int nroSucursal, String fecha, int cantAdultos, int cantMenores, Integer codZona);

    // Req 38: registra la reserva en el sistema del restaurante
    ReservaExternaResponse registrarReserva(ReservaExternaRequest reserva);

    // Req 46: contenidos que el restaurante quiere publicar (el portal los guarda después)
    List<ContenidoExterno> obtenerContenidos();

    // Req 50: informa un clic al restaurante
    ClickExternoResponse notificarClick(ClickExternoRequest click);
}
