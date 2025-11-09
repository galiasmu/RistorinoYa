package com.portal.ristorinoya.dto;

import java.time.LocalDate;

public class PromotionDTO {

    private Integer nroRestaurante;
    private Integer nroIdioma;
    private Integer nroContenido;
    private Integer nroSucursal;
    private String contenidoPromocional;
    private String imagenPromocional;
    private String contenidoAPublicar;
    private LocalDate fechaIniVigencia;
    private LocalDate fechaFinVigencia;
    private Double costoClick;
    private String codContenidoRestaurante;
    private String razonSocial;
    private String nomIdioma;
    private String nomSucursal;

    public PromotionDTO(
            Integer nroRestaurante,
            Integer nroIdioma,
            Integer nroContenido,
            Integer nroSucursal,
            String contenidoPromocional,
            String imagenPromocional,
            String contenidoAPublicar,
            LocalDate fechaIniVigencia,
            LocalDate fechaFinVigencia,
            Double costoClick,
            String codContenidoRestaurante,
            String razonSocial,
            String nomIdioma,
            String nomSucursal
    ) {
        this.nroRestaurante = nroRestaurante;
        this.nroIdioma = nroIdioma;
        this.nroContenido = nroContenido;
        this.nroSucursal = nroSucursal;
        this.contenidoPromocional = contenidoPromocional;
        this.imagenPromocional = imagenPromocional;
        this.contenidoAPublicar = contenidoAPublicar;
        this.fechaIniVigencia = fechaIniVigencia;
        this.fechaFinVigencia = fechaFinVigencia;
        this.costoClick = costoClick;
        this.codContenidoRestaurante = codContenidoRestaurante;
        this.razonSocial = razonSocial;
        this.nomIdioma = nomIdioma;
        this.nomSucursal = nomSucursal;
    }

    public Integer getNroRestaurante() { return nroRestaurante; }
    public Integer getNroIdioma() { return nroIdioma; }
    public Integer getNroContenido() { return nroContenido; }
    public Integer getNroSucursal() { return nroSucursal; }
    public String getContenidoPromocional() { return contenidoPromocional; }
    public String getImagenPromocional() { return imagenPromocional; }
    public String getContenidoAPublicar() { return contenidoAPublicar; }
    public LocalDate getFechaIniVigencia() { return fechaIniVigencia; }
    public LocalDate getFechaFinVigencia() { return fechaFinVigencia; }
    public Double getCostoClick() { return costoClick; }
    public String getCodContenidoRestaurante() { return codContenidoRestaurante; }
    public String getRazonSocial() { return razonSocial; }
    public String getNomIdioma() { return nomIdioma; }
    public String getNomSucursal() { return nomSucursal; }
}