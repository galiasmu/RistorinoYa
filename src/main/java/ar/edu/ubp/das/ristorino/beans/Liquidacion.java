package ar.edu.ubp.das.ristorino.beans;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Liquidacion {
    private int nroRestaurante;
    private String razonSocial;
    private int cantReservas;
    private BigDecimal totalReservas;
    private int cantClicks;
    private BigDecimal totalClicks;
    private BigDecimal totalLiquidar;
}
