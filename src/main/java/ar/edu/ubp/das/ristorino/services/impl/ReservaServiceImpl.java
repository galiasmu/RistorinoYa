package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.Cliente;
import ar.edu.ubp.das.ristorino.beans.Costo;
import ar.edu.ubp.das.ristorino.beans.ReservaCliente;
import ar.edu.ubp.das.ristorino.beans.ReservaRequest;
import ar.edu.ubp.das.ristorino.beans.ReservaResponse;
import ar.edu.ubp.das.ristorino.beans.restaurante.ReservaExternaRequest;
import ar.edu.ubp.das.ristorino.beans.restaurante.ReservaExternaResponse;
import ar.edu.ubp.das.ristorino.repository.ClienteRepository;
import ar.edu.ubp.das.ristorino.repository.ReservaRepository;
import ar.edu.ubp.das.ristorino.services.ReservaService;
import ar.edu.ubp.das.ristorino.services.RestauranteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static ar.edu.ubp.das.ristorino.utils.Constantes.CONFIRMADA;
import static ar.edu.ubp.das.ristorino.utils.Constantes.COSTO_RESERVA;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteService restauranteService;

    public ReservaServiceImpl(ReservaRepository reservaRepository, ClienteRepository clienteRepository, RestauranteService restauranteService) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteService = restauranteService;
    }

    // 1) calcula el fee vigente, 2) registra la reserva en el restaurante (tiempo real),
    // 3) si el restaurante la confirma, la guarda en el portal con su código y el fee
    @Override
    public ReservaResponse confirmarReserva(int nroCliente, ReservaRequest reserva) {
        Cliente cliente = clienteRepository.getCliente(nroCliente);
        Costo costo = reservaRepository.getCostoVigente(COSTO_RESERVA);
        BigDecimal costoReserva = costo == null ? BigDecimal.ZERO : costo.getMonto();

        ReservaExternaResponse respuesta = restauranteService.obtenerFacade(reserva.getNroRestaurante())
                .registrarReserva(transformarReserva(reserva, cliente, costoReserva));

        ReservaResponse resultado = new ReservaResponse();
        resultado.setEstado(respuesta.getEstado());
        resultado.setMotivo(respuesta.getMotivo());
        resultado.setCodReservaSucursal(respuesta.getCodReserva());
        resultado.setNroRestaurante(reserva.getNroRestaurante());
        resultado.setNroSucursal(reserva.getNroSucursal());
        resultado.setCodZona(reserva.getCodZona());
        resultado.setFecha(reserva.getFecha());
        resultado.setHora(reserva.getHora());
        resultado.setCantAdultos(reserva.getCantAdultos());
        resultado.setCantMenores(reserva.getCantMenores());
        resultado.setCostoReserva(costoReserva);

        if (CONFIRMADA.equals(respuesta.getEstado())) {
            resultado.setNroReserva(reservaRepository.insReserva(nroCliente, reserva, costoReserva, respuesta.getCodReserva()));
        }
        return resultado;
    }

    @Override
    public List<ReservaCliente> getReservasCliente(int nroCliente, String codIdioma, Integer codEstado, String fechaDesde, String fechaHasta) {
        return reservaRepository.getReservasCliente(nroCliente, codIdioma, codEstado, fechaDesde, fechaHasta);
    }

    private ReservaExternaRequest transformarReserva(ReservaRequest reserva, Cliente cliente, BigDecimal costoReserva) {
        ReservaExternaRequest externa = new ReservaExternaRequest();
        externa.setNroSucursal(reserva.getNroSucursal());
        externa.setCodZona(reserva.getCodZona());
        externa.setFecha(reserva.getFecha());
        externa.setHora(reserva.getHora());
        externa.setCantAdultos(reserva.getCantAdultos());
        externa.setCantMenores(reserva.getCantMenores());
        externa.setCostoReserva(costoReserva);
        externa.setApellido(cliente.getApellido());
        externa.setNombre(cliente.getNombre());
        externa.setCorreo(cliente.getCorreo());
        externa.setTelefonos(cliente.getTelefonos());
        return externa;
    }
}
