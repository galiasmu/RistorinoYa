package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import java.math.BigDecimal;

// Respuesta de obtenerContenidos (Req 46).
// El elemento XML <Contenido> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "Contenido")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContenidoExterno {
    private int nroContenido;
    private Integer nroSucursal;
    private String nomSucursal;
    private String contenidoAPublicar;
    private String imagenAPublicar;
    private BigDecimal costoClick;
}
