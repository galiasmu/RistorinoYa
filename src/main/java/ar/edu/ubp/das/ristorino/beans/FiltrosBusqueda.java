package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.util.List;

// Búsqueda estructurada. Es también lo que devuelve el motor de IA al interpretar el lenguaje natural.
@Data
public class FiltrosBusqueda {
    private List<FiltroPreferencia> valoresDominio;
    private String barrio;
}
