package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String clave;
}
