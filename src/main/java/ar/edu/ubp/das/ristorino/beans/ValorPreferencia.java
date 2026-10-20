package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class ValorPreferencia {
    private int codCategoria;
    private String categoria;
    private int nroValorDominio;
    private String valorDominio;
}
