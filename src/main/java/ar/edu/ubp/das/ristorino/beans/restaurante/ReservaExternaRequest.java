package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import java.math.BigDecimal;

// Parámetro de registrarReserva (Req 38).
// El elemento XML <reserva> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "reserva")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReservaExternaRequest {
    private int nroSucursal;
    private int codZona;
    private String fecha;
    private String hora;
    private int cantAdultos;
    private int cantMenores;
    private BigDecimal costoReserva;
    private String apellido;
    private String nombre;
    private String correo;
    private String telefonos;
}
