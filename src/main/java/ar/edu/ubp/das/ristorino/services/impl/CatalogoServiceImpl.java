package ar.edu.ubp.das.ristorino.services.impl;

import ar.edu.ubp.das.ristorino.beans.EstadoReserva;
import ar.edu.ubp.das.ristorino.beans.Localidad;
import ar.edu.ubp.das.ristorino.beans.ValorPreferencia;
import ar.edu.ubp.das.ristorino.repository.CatalogoRepository;
import ar.edu.ubp.das.ristorino.services.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Override
    public List<ValorPreferencia> getPreferencias(String codIdioma) {
        return catalogoRepository.getPreferencias(codIdioma);
    }

    @Override
    public List<Localidad> getLocalidades() {
        return catalogoRepository.getLocalidades();
    }

    @Override
    public List<EstadoReserva> getEstadosReservas(String codIdioma) {
        return catalogoRepository.getEstadosReservas(codIdioma);
    }
}
