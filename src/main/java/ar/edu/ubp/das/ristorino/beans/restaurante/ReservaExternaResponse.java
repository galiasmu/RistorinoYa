package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import java.math.BigDecimal;

// Respuesta de registrarReserva. estado: CONFIRMADA | RECHAZADA.
// El elemento XML <Reserva> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "Reserva")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReservaExternaResponse {
    private String codReserva;
    private int nroSucursal;
    private int codZona;
    private String fecha;
    private String hora;
    private int cantAdultos;
    private int cantMenores;
    private BigDecimal costoReserva;
    private String estado;
    private String motivo;
}
