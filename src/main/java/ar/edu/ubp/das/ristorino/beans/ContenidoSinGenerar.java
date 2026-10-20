package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;

// Contenido que todavía no tiene texto generado por IA (Req 34).
@Data
public class ContenidoSinGenerar {
    private int nroRestaurante;
    private int nroIdioma;
    private int nroContenido;
    private Integer nroSucursal;
    private String razonSocial;
    private String contenidoAPublicar;
    private String imagenPromocional;
    private BigDecimal costoClick;
    private String codContenidoRestaurante;
}
