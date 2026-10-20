package ar.edu.ubp.das.ristorino.model.soap;

import ar.edu.ubp.das.ristorino.beans.Restaurante;
import ar.edu.ubp.das.ristorino.beans.restaurante.*;
import ar.edu.ubp.das.ristorino.components.SOAPClientFactory;
import ar.edu.ubp.das.ristorino.model.RestauranteFacade;
import ar.edu.ubp.das.ristorino.model.restauranteConfig.SoapConfig;
import ar.edu.ubp.das.ristorino.utils.SOAPClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestauranteSoapFacadeImpl implements RestauranteFacade {

    private final SOAPClientFactory soapClientFactory;
    private final SoapConfig config;
    private final Restaurante restaurante;

    public RestauranteSoapFacadeImpl(SOAPClientFactory soapClientFactory, SoapConfig config, Restaurante restaurante) {
        this.soapClientFactory = soapClientFactory;
        this.config = config;
        this.restaurante = restaurante;
    }

    @Override
    public RestauranteExterno obtenerRestaurante() {
        return clienteParaOperacion("obtenerRestaurante")
                .callServiceForObject(RestauranteExterno.class, "obtenerRestauranteResponse");
    }

    @Override
    public List<SucursalExterna> obtenerSucursales() {
        try {
            List<SucursalExterna> sucursales = clienteParaOperacion("obtenerSucursales")
                    .callServiceForList(SucursalExterna.class, "obtenerSucursalesResponse");

            System.out.println("Sucursales recibidas de " + restaurante.getRazonSocial() + ": " + sucursales.size());
            return sucursales;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo sucursales desde servicio SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DisponibilidadExterna> consultarDisponibilidad(int nroSucursal, String fecha, int cantAdultos, int cantMenores, Integer codZona) {
        // El orden de los parámetros respeta la secuencia del XSD del servicio
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("nroSucursal", nroSucursal);
        params.put("fecha", fecha);
        params.put("cantAdultos", cantAdultos);
        params.put("cantMenores", cantMenores);
        if (codZona != null) {
            params.put("codZona", codZona);
        }
        return clienteParaOperacion("consultarDisponibilidad")
                .callServiceForList(DisponibilidadExterna.class, "consultarDisponibilidadResponse", params);
    }

    @Override
    public ReservaExternaResponse registrarReserva(ReservaExternaRequest reserva) {
        // Los parámetros complejos se serializan con JAXB (<reserva>...</reserva>)
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("reserva", reserva);
        return clienteParaOperacion("registrarReserva")
                .callServiceForObject(ReservaExternaResponse.class, "registrarReservaResponse", params);
    }

    @Override
    public List<ContenidoExterno> obtenerContenidos() {
        try {
            List<ContenidoExterno> contenidos = clienteParaOperacion("obtenerContenidos")
                    .callServiceForList(ContenidoExterno.class, "obtenerContenidosResponse");

            System.out.println("Contenidos recibidos de " + restaurante.getRazonSocial() + ": " + contenidos.size());
            return contenidos;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo contenidos desde servicio SOAP: " + e.getMessage(), e);
        }
    }

    @Override
    public ClickExternoResponse notificarClick(ClickExternoRequest click) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("clic", click);
        return clienteParaOperacion("registrarClickContenido")
                .callServiceForObject(ClickExternoResponse.class, "registrarClickContenidoResponse", params);
    }

    // En los servicios de los restaurantes el elemento raíz del request es <operacion>Request
    private SOAPClient clienteParaOperacion(String operacion) {
        return soapClientFactory.createSOAPClient(config.getUrlServicio(), config.getNamespace(), config.getServiceName(),
                config.getPortName(), operacion + "Request", config.getUsername(), config.getPassword());
    }
}
