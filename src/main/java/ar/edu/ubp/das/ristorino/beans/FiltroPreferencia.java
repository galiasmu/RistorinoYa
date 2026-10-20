package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

// Un valor de preferencia (categoría + valor de dominio) usado como filtro.
@Data
public class FiltroPreferencia {
    private int codCategoria;
    private int nroValorDominio;
}
