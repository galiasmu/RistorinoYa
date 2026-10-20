package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private int nroCliente;
    private String apellido;
    private String nombre;
    private String correo;
}
