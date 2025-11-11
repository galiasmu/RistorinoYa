package com.portal.ristorinoya.dto;

public class ClickCreateDTO {

    private Integer nroRestaurante;
    private Integer nroIdioma;
    private Integer nroContenido;
    private Integer nroCliente; // opcional, puede venir null

    public Integer getNroRestaurante() { return nroRestaurante; }
    public void setNroRestaurante(Integer nroRestaurante) { this.nroRestaurante = nroRestaurante; }

    public Integer getNroIdioma() { return nroIdioma; }
    public void setNroIdioma(Integer nroIdioma) { this.nroIdioma = nroIdioma; }

    public Integer getNroContenido() { return nroContenido; }
    public void setNroContenido(Integer nroContenido) { this.nroContenido = nroContenido; }

    public Integer getNroCliente() { return nroCliente; }
    public void setNroCliente(Integer nroCliente) { this.nroCliente = nroCliente; }
}
