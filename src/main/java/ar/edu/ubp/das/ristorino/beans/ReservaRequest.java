package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

// Confirmación de reserva desde el portal (Req 15). fecha yyyy-MM-dd, hora HH:mm.
@Data
public class ReservaRequest {
    private int nroRestaurante;
    private int nroSucursal;
    private int codZona;
    private String fecha;
    private String hora;
    private int cantAdultos;
    private int cantMenores;
}
