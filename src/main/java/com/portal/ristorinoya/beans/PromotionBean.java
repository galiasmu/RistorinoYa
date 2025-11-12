package com.portal.ristorinoya.beans;

import java.time.LocalDate;

public class PromotionBean {

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
    private Integer nroRestaurante;

    public Integer getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(Integer nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public Integer getNroIdioma() {
        return nroIdioma;
    }

    public void setNroIdioma(Integer nroIdioma) {
        this.nroIdioma = nroIdioma;
    }

    public Integer getNroContenido() {
        return nroContenido;
    }

    public void setNroContenido(Integer nroContenido) {
        this.nroContenido = nroContenido;
    }

    public Integer getNroSucursal() {
        return nroSucursal;
    }

    public void setNroSucursal(Integer nroSucursal) {
        this.nroSucursal = nroSucursal;
    }

    public String getContenidoPromocional() {
        return contenidoPromocional;
    }

    public void setContenidoPromocional(String contenidoPromocional) {
        this.contenidoPromocional = contenidoPromocional;
    }

    public String getImagenPromocional() {
        return imagenPromocional;
    }

    public void setImagenPromocional(String imagenPromocional) {
        this.imagenPromocional = imagenPromocional;
    }

    public String getContenidoAPublicar() {
        return contenidoAPublicar;
    }

    public void setContenidoAPublicar(String contenidoAPublicar) {
        this.contenidoAPublicar = contenidoAPublicar;
    }

    public LocalDate getFechaIniVigencia() {
        return fechaIniVigencia;
    }

    public void setFechaIniVigencia(LocalDate fechaIniVigencia) {
        this.fechaIniVigencia = fechaIniVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public Double getCostoClick() {
        return costoClick;
    }

    public void setCostoClick(Double costoClick) {
        this.costoClick = costoClick;
    }

    public String getCodContenidoRestaurante() {
        return codContenidoRestaurante;
    }

    public void setCodContenidoRestaurante(String codContenidoRestaurante) {
        this.codContenidoRestaurante = codContenidoRestaurante;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNomIdioma() {
        return nomIdioma;
    }

    public void setNomIdioma(String nomIdioma) {
        this.nomIdioma = nomIdioma;
    }

    public String getNomSucursal() {
        return nomSucursal;
    }

    public void setNomSucursal(String nomSucursal) {
        this.nomSucursal = nomSucursal;
    }
}