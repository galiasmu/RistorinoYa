package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import java.math.BigDecimal;

// Parámetro de registrarClickContenido (Req 50).
// El elemento XML <clic> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "clic")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClickExternoRequest {
    private int nroContenido;
    private String fechaHoraRegistro;
    private BigDecimal costoClick;
    private String apellido;
    private String nombre;
    private String correo;
    private String telefonos;
}
