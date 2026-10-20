package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.ReservaCliente;
import ar.edu.ubp.das.ristorino.beans.ReservaRequest;
import ar.edu.ubp.das.ristorino.beans.ReservaResponse;

import java.util.List;

public interface ReservaService {

    // Req 15/38
    ReservaResponse confirmarReserva(int nroCliente, ReservaRequest reserva);

    // Req 16
    List<ReservaCliente> getReservasCliente(int nroCliente, String codIdioma, Integer codEstado, String fechaDesde, String fechaHasta);
}
