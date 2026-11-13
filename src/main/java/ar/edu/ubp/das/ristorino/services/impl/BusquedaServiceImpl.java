package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.FiltrosBusqueda;
import ar.edu.ubp.das.ristorino.beans.ResultadoBusqueda;
import ar.edu.ubp.das.ristorino.beans.ValorPreferencia;
import ar.edu.ubp.das.ristorino.model.ia.MotorIAFacade;
import ar.edu.ubp.das.ristorino.repository.CatalogoRepository;
import ar.edu.ubp.das.ristorino.repository.RestauranteRepository;
import ar.edu.ubp.das.ristorino.services.BusquedaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusquedaServiceImpl implements BusquedaService {

    private final RestauranteRepository restauranteRepository;
    private final CatalogoRepository catalogoRepository;
    private final MotorIAFacade motorIAFacade;

    public BusquedaServiceImpl(RestauranteRepository restauranteRepository, CatalogoRepository catalogoRepository, MotorIAFacade motorIAFacade) {
        this.restauranteRepository = restauranteRepository;
        this.catalogoRepository = catalogoRepository;
        this.motorIAFacade = motorIAFacade;
    }

    @Override
    public List<ResultadoBusqueda> buscar(FiltrosBusqueda filtros) {
        return restauranteRepository.buscarRestaurantes(filtros);
    }

    @Override
    public List<ResultadoBusqueda> buscarLenguajeNatural(String texto, String codIdioma) {
        List<ValorPreferencia> preferencias = catalogoRepository.getPreferencias(codIdioma);
        FiltrosBusqueda filtros = motorIAFacade.interpretarBusqueda(texto, preferencias);

        // Si la IA no encontró comida ni barrio, el pedido no tiene que ver con restaurantes
        if ((filtros.getValoresDominio() == null || filtros.getValoresDominio().isEmpty())
                && (filtros.getBarrio() == null || filtros.getBarrio().isBlank())) {
            return List.of();
        }
        return restauranteRepository.buscarRestaurantes(filtros);
    }
}
