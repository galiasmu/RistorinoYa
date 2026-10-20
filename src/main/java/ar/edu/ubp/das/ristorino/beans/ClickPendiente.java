package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;

// Clic registrado en el portal que todavía no se informó al restaurante.
@Data
public class ClickPendiente {
    private int nroRestaurante;
    private int nroIdioma;
    private int nroContenido;
    private int nroClick;
    private String codContenidoRestaurante;
    private String fechaHoraRegistro;
    private BigDecimal costoClick;
    private String apellido;
    private String nombre;
    private String correo;
    private String telefonos;
}
