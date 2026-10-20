package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

// Clic del usuario en un contenido (Req 50).
@Data
public class ClickRequest {
    private int nroRestaurante;
    private int nroIdioma;
    private int nroContenido;
}
