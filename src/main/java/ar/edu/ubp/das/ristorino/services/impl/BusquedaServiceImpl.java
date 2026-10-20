package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.ResultadoBusqueda;
import ar.edu.ubp.das.ristorino.model.ia.MotorIAFacade;
import ar.edu.ubp.das.ristorino.repository.RestauranteRepository;
import ar.edu.ubp.das.ristorino.services.BusquedaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusquedaServiceImpl implements BusquedaService {

    private final RestauranteRepository restauranteRepository;
    private final MotorIAFacade motorIAFacade;

    public BusquedaServiceImpl(RestauranteRepository restauranteRepository, MotorIAFacade motorIAFacade) {
        this.restauranteRepository = restauranteRepository;
        this.motorIAFacade = motorIAFacade;
    }

    @Override
    public List<ResultadoBusqueda> buscar(FiltrosBusqueda filtros) {
        return restauranteRepository.buscarRestaurantes(filtros);
    }

    @Override
    public List<ResultadoBusqueda> buscarLenguajeNatural(String texto, String codIdioma) {
        // La IA interpreta el texto y devuelve los mismos filtros que usa la búsqueda estructurada
        FiltrosBusqueda filtros = motorIAFacade.interpretarBusqueda(texto, codIdioma);
        return restauranteRepository.buscarRestaurantes(filtros);
    }
}
