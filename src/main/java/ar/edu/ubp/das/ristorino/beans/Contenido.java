package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;

// Contenido promocional vigente (Req 12).
@Data
public class Contenido {
    private int nroRestaurante;
    private int nroIdioma;
    private int nroContenido;
    private Integer nroSucursal;
    private String razonSocial;
    private String nomSucursal;
    private String contenidoPromocional;
    private String imagenPromocional;
    private String fechaIniVigencia;
    private String fechaFinVigencia;
    private BigDecimal costoClick;
    private String codContenidoRestaurante;
}
