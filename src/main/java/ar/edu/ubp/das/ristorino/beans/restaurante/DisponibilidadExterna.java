package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

// Respuesta de consultarDisponibilidad (Req 37).
// El elemento XML <Disponibilidad> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "Disponibilidad")
@XmlAccessorType(XmlAccessType.FIELD)
public class DisponibilidadExterna {
    private int nroSucursal;
    private int codZona;
    private String nomZona;
    private String fecha;
    private String horaDesde;
    private String horaHasta;
    private int cantComensales;
    private int lugaresLibres;
    private boolean permiteMenores;
    private boolean disponible;
}
