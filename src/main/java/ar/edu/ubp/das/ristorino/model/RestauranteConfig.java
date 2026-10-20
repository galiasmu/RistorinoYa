package ar.edu.ubp.das.ristorino.model;

import lombok.Data;

// Configuración de la integración con un restaurante (tabla configuracion_restaurantes)
@Data
public abstract class RestauranteConfig {
    private int nroRestaurante;
    private String tipoServicio;  // Tipo de servicio ('SOAP' o 'REST')
    private String urlServicio;   // URL base (REST) o URL del WSDL (SOAP)
    private String username;      // Usuario para autenticación
    private String password;      // Contraseña para autenticación
}
