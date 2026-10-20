package ar.edu.ubp.das.ristorino.services;

import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.ResultadoBusqueda;

import java.util.List;

public interface BusquedaService {

    // Req 9: búsqueda con filtros
    List<ResultadoBusqueda> buscar(FiltrosBusqueda filtros);

    // Req 10/35: búsqueda en lenguaje natural (la IA convierte el texto en filtros)
    List<ResultadoBusqueda> buscarLenguajeNatural(String texto, String codIdioma);
}
