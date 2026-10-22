package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class Restaurante {
    private int nroRestaurante;
    private String razonSocial;
    private String cuit;
    private String descripcion;
}
