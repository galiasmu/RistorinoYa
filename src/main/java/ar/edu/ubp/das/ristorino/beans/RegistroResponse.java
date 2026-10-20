package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class RegistroResponse {
    private Integer nroCliente;
    private String estado;
    private String motivo;
}
