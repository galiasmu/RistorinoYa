package ar.edu.ubp.das.ristorino.beans.restaurante;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

// Respuesta de obtenerSucursales (Req 45). turnos, zonas, tiposComidas, especialidades y estilos son JSON.
// El elemento XML <Sucursal> y los nombres de los campos coinciden con el contrato del servicio del restaurante.
@Data
@XmlRootElement(name = "Sucursal")
@XmlAccessorType(XmlAccessType.FIELD)
public class SucursalExterna {
    private int nroSucursal;
    private String nomSucursal;
    private String calle;
    private int nroCalle;
    private String barrio;
    private int nroLocalidad;
    private String nomLocalidad;
    private String nomProvincia;
    private String codPostal;
    private String telefonos;
    private int totalComensales;
    private int minTolerenciaReserva;
    private int nroCategoria;
    private String nomCategoria;
    private String turnos;
    private String zonas;
    private String tiposComidas;
    private String especialidades;
    private String estilos;
}
