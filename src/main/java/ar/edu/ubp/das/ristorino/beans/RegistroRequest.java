package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.util.List;

// Alta de usuario (Req 1).
@Data
public class RegistroRequest {
    private String apellido;
    private String nombre;
    private String correo;
    private String clave;
    private String confirmacionClave;
    private String telefonos;
    private Integer nroLocalidad;
    private List<PreferenciaCliente> preferencias;
}
