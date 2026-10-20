package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.EstadoReserva;
import ar.edu.ubp.das.ristorino.beans.Localidad;
import ar.edu.ubp.das.ristorino.beans.ValorPreferencia;

import java.util.List;

public interface CatalogoService {

    List<ValorPreferencia> getPreferencias(String codIdioma);

    List<Localidad> getLocalidades();

    List<EstadoReserva> getEstadosReservas(String codIdioma);
}
