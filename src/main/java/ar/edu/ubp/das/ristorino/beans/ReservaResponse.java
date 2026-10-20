package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;

// Resultado de confirmar una reserva. estado: CONFIRMADA | RECHAZADA.
@Data
public class ReservaResponse {
    private Integer nroReserva;
    private String codReservaSucursal;
    private String estado;
    private String motivo;
    private int nroRestaurante;
    private int nroSucursal;
    private int codZona;
    private String fecha;
    private String hora;
    private int cantAdultos;
    private int cantMenores;
    private BigDecimal costoReserva;
}
