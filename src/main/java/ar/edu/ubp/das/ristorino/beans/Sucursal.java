package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

// Sucursal de un restaurante en el portal. zonas y preferencias vienen como JSON (FOR JSON PATH).
@Data
public class Sucursal {
    private int nroRestaurante;
    private String razonSocial;
    private int nroSucursal;
    private String nomSucursal;
    private String calle;
    private int nroCalle;
    private String barrio;
    private String nomLocalidad;
    private String codPostal;
    private String telefonos;
    private int totalComensales;
    private int minTolerenciaReserva;
    private String zonas;
    private String preferencias;
}
