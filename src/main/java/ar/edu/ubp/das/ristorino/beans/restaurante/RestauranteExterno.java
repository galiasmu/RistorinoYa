package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

// Respuesta de obtenerRestaurante.
// El elemento XML <Restaurante> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "Restaurante")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestauranteExterno {
    private int nroRestaurante;
    private String razonSocial;
    private String cuit;
    private String descripcion;
    private String tonoComunicacion;
    private String platosEmblematicos;
}
