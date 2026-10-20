package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

// Sucursal encontrada por la búsqueda, con la cantidad de filtros que cumple.
@Data
public class ResultadoBusqueda {
    private int nroRestaurante;
    private String razonSocial;
    private int nroSucursal;
    private String nomSucursal;
    private String calle;
    private int nroCalle;
    private String barrio;
    private String nomLocalidad;
    private int coincidencias;
}
