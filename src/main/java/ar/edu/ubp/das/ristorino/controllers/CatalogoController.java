package ar.edu.ubp.das.ristorino.controllers;

import ar.edu.ubp.das.ristorino.beans.EstadoReserva;
import ar.edu.ubp.das.ristorino.beans.Localidad;
import ar.edu.ubp.das.ristorino.beans.ValorPreferencia;
import ar.edu.ubp.das.ristorino.services.CatalogoService;
import ar.edu.ubp.das.ristorino.utils.IdiomaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {
    @Autowired
    private CatalogoService catalogoService;

    // Valores de preferencias gastronómicas (registro y filtros de búsqueda)
    @GetMapping("/preferencias")
    public List<ValorPreferencia> getPreferencias(@RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage) {
        return catalogoService.getPreferencias(IdiomaUtils.codIdioma(acceptLanguage));
    }

    @GetMapping("/localidades")
    public List<Localidad> getLocalidades() {
        return catalogoService.getLocalidades();
    }

    @GetMapping("/estados-reservas")
    public List<EstadoReserva> getEstadosReservas(@RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage) {
        return catalogoService.getEstadosReservas(IdiomaUtils.codIdioma(acceptLanguage));
    }
}
