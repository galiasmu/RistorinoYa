package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class Cliente {
    private int nroCliente;
    private String apellido;
    private String nombre;
    private String clave;
    private String correo;
    private String telefonos;
    private Integer nroLocalidad;
    private boolean habilitado;
}
