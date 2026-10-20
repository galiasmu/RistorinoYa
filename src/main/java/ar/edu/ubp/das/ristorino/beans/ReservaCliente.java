package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;

// Reserva del cliente (Req 16).
@Data
public class ReservaCliente {
    private int nroReserva;
    private String codReservaSucursal;
    private String razonSocial;
    private String nomSucursal;
    private String calle;
    private int nroCalle;
    private String barrio;
    private String fechaReserva;
    private String horaReserva;
    private int codZona;
    private String zona;
    private int cantAdultos;
    private int cantMenores;
    private int codEstado;
    private String estado;
    private BigDecimal costoReserva;
}
