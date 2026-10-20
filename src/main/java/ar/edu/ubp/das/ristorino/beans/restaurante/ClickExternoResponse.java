package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

// Respuesta de registrarClickContenido. estado: REGISTRADO | RECHAZADO.
// El elemento XML <Click> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "Click")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClickExternoResponse {
    private Integer nroClick;
    private int nroContenido;
    private String estado;
    private String motivo;
}
